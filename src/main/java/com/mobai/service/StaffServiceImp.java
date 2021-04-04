package com.mobai.service;

import com.alibaba.fastjson.JSONObject;
import com.mobai.dto.StaffDTO;
import com.mobai.utils.RedisUtil;
import com.mobai.utils.SmsUtil;
import com.mobai.utils.TokenUtil;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StaffServiceImp {
    private static Logger log = LoggerFactory.getLogger(StaffServiceImp.class);
    private SqlSessionTemplate sqlSessionTemplate;
    private RedisUtil redisUtil;

    @Autowired
    public StaffServiceImp(SqlSessionTemplate sqlSessionTemplate, RedisUtil redisUtil) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.redisUtil = redisUtil;
    }

    /**
     * 注册新用户
     *
     * @param staffDTO 用户信息
     * @param request  请求
     * @return 用户唯一帐号
     */
    @Transactional
    public String insertStaffSvc(StaffDTO staffDTO, HttpServletRequest request) {
        log.info("===========插入新用户SVC=============insertStaffSvc==============");
        String sql = "StaffMapper.insertSelective";
        //验证手机号是否可用
        String phone = staffDTO.getPhone();
        // 从 request 获取填写的验证码
        String code = request.getParameter("code");
        //验证电话格式
        checkPhoneSvc(phone);
        // 从redisUtil获取电话验证码
        if (!redisUtil.hasKey("phone" + phone)) {
            log.info("===========插入新用户SVC=======失败，手机号未获取验证码或过期=============");
            throw new RuntimeException("手机号未获取验证码或过期，请先获取验证码！");
        }
        String codeRedis = (String) redisUtil.get("phone" + phone);
        if (!codeRedis.equals(code)) {
            log.info("===========插入新用户SVC=======失败，验证码错误=============");
            throw new RuntimeException("手机号或者验证码错误，请重新输入！");
        }
        //验证是否存在该用户
        boolean checkPhone = checkPhoneExitSvc(phone);
        if (checkPhone) {
            log.info("===========插入新用户SVC=======失败，一个手机号只能绑定一个账户=============");
            throw new RuntimeException("一个手机号只能绑定一个账户");
        }
        //利用时间戳+64进制生成唯一帐号
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String nowStr = fmt.format(now);
        String staffCode = to62String(new BigInteger(nowStr.substring(2)));
        staffDTO.setStaffCode(staffCode);
        String password = staffDTO.getPassword();
        //password加密
        if (StringUtils.isEmpty(password) || password.length() < 6) {
            log.info("===========插入新用户SVC=======失败，密码不能为空,密码长度至少6位=============");
            throw new RuntimeException("密码不能为空,密码长度至少6位");
        }
        String hex = DigestUtils.md5DigestAsHex(password.getBytes());
        staffDTO.setPassword(hex);
        //普通用户
        staffDTO.setStaffType("customer");
        int count = sqlSessionTemplate.insert(sql, staffDTO);
        if (count < 1) {
            log.info("===========插入新用户SVC=======失败，成功条数小于1=============");
            throw new RuntimeException("插入用户失败");
        }
        return staffCode;
    }

    /**
     * 验证手机是否符合格式
     *
     * @param phone 电话号码
     */
    void checkPhoneSvc(String phone) {
        log.info("===========手机号是否使用SVC=============checkPhoneSvc==============");
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
        if (phone.length() != 11) {
            log.info("===========插入新用户SVC=======失败，手机号格式错误=============");
            throw new RuntimeException("手机号格式错误");
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            if (!isMatch) {
                log.info("===========插入新用户SVC=======失败，手机号格式错误=============");
                throw new RuntimeException("手机号格式错误");
            }
        }
    }

    /**
     * 检测手机号是否使用
     *
     * @return 手机号是否使用
     */
    private boolean checkPhoneExitSvc(String phone) {
        String sql = "StaffMapper.checkPhone";
        Integer num = sqlSessionTemplate.selectOne(sql, phone);
        return num > 0;
    }

    /**
     * 发送验证码
     *
     * @param phone 电话号码
     */
    public void sendPassCodeSvc(String phone) {
        log.info("===========插入新用户SVC=============getPassCodeSvc==============");
        checkPhoneSvc(phone);
        String code = SmsUtil.randomCode();
        boolean hasPhone = redisUtil.hasKey("phone" + phone);
        if (hasPhone) {
            throw new RuntimeException("短信验证码尚未失效，请稍后再试！");
        }
        boolean hasNum = redisUtil.hasKey("num" + phone);
        if (hasNum) {
            Integer num = (Integer) redisUtil.get("num" + phone);
            if (num > 10) {
                throw new RuntimeException("短信验证码今天次数已用完，请明天再试！");
            }
        }
        System.out.println("验证码是："+code);
        boolean isSuccess = SmsUtil.sendMessage(phone, code);
        if (isSuccess) {
            log.info("发送短信成功，号码：" + phone + "，验证码：" + code);
            redisUtil.set("phone" + phone, code, 60);
            boolean hasNum2 = redisUtil.hasKey("num" + phone);
            if (hasNum2) {
                redisUtil.incr("num" + phone, 1);
            } else {
                redisUtil.set("num" + phone, 1, 60 * 60 * 12);
            }
        } else {
            log.info("=======发送短信验证码失败===========");
            throw new RuntimeException("发送短信验证码失败，请稍后再试！");
        }
    }

    /**
     * 可用帐号或者手机号登录
     *
     * @param staffDTO 帐号和密码
     * @return Token
     */
    public Map<String, Object> loginSvc(StaffDTO staffDTO) {
        String sql = "StaffMapper.login";
        String password = staffDTO.getPassword();
        String staffCode = staffDTO.getPhone();
        if (StringUtils.isEmpty(staffCode) || StringUtils.isEmpty(password)) {
            throw new RuntimeException("帐号密码不能为空");
        }
        String hex = DigestUtils.md5DigestAsHex(password.getBytes());
        staffDTO.setPassword(hex);
        StaffDTO staffInfo = sqlSessionTemplate.selectOne(sql, staffDTO);
        if (staffInfo == null) {
            throw new RuntimeException("帐号或密码不正确");
        }
        staffInfo.setPassword(null);
        Map<String, Object> map = new HashMap<>();
        String token = TokenUtil.sign(JSONObject.toJSONString(staffInfo));
        map.put("token", token);
        map.put("userInfo", staffInfo);
        return map;
    }

    public void forgetPasswordSvc(StaffDTO staffDTO, String code) {
        String phone = staffDTO.getPhone();
        String password = staffDTO.getPassword();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) {
            throw new RuntimeException("电话和密码不能为空");
        }
        if (redisUtil.hasKey("phone" + phone)) {
            String phoneCode = (String) redisUtil.get("phone" + phone);
            if (phoneCode.equals(code)) {
                String sql = "StaffMapper.updatePassword";
                String hex = DigestUtils.md5DigestAsHex(password.getBytes());
                staffDTO.setPassword(hex);
                sqlSessionTemplate.update(sql, staffDTO);
            } else {
                throw new RuntimeException("验证码错误，请重新输入");
            }
        } else {
            throw new RuntimeException("验证码不存在，请获取验证码");
        }
    }

    /**
     * 生成id
     *
     * @param i 时间戳
     * @return 唯一id
     */
    private static String to62String(BigInteger i) {
        BigDecimal divide = new BigDecimal(62);
        BigDecimal decimal = new BigDecimal(i);
        StringBuilder res = new StringBuilder();
        String s36bit_const = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        while (BigDecimal.ZERO.compareTo(decimal) != 0) {
            BigDecimal[] divRes = decimal.divideAndRemainder(divide);
            decimal = divRes[0];
            res.insert(0, s36bit_const.charAt(divRes[1].intValue()));
        }
        return res.toString();
    }

    /**
     * 更改个人信息
     *
     * @param staffDTO 个人信息
     */
    public void updateStaffSvc(StaffDTO staffDTO) {
        staffDTO.setPassword(null);
        staffDTO.setStaffCode(null);
        staffDTO.setScore(null);
        staffDTO.setPhone(null);
        String sql = "StaffMapper.updateStaff";
        int update = sqlSessionTemplate.update(sql, staffDTO);
        if (update < 1) {
            throw new RuntimeException("更新失败，请稍后再试！");
        }
    }

    /**
     * 查询所有个人信息
     *
     * @param staffDTO 个人信息
     */
    public List<StaffDTO> getAllStaffSvc(StaffDTO staffDTO) {
        String sql = "StaffMapper.getAllStaff";
        return sqlSessionTemplate.selectList(sql, staffDTO);
    }


}
