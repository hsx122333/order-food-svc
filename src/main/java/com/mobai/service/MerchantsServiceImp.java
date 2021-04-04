package com.mobai.service;

import com.alibaba.fastjson.JSONObject;
import com.mobai.dto.MerchantsDTO;
import com.mobai.utils.RedisUtil;
import com.mobai.utils.TokenUtil;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class MerchantsServiceImp {
    private static Logger log = LoggerFactory.getLogger(MerchantsServiceImp.class);
    private static final String gitPath = System.getProperty("user.dir") + File.separator + "logistics" + File.separator + "uploads" + File.separator;

    private SqlSessionTemplate sqlSessionTemplate;
    private StaffServiceImp staffServiceImp;
    private RedisUtil redisUtil;

    @Autowired
    public MerchantsServiceImp(SqlSessionTemplate sqlSessionTemplate, StaffServiceImp staffServiceImp, RedisUtil redisUtil) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.staffServiceImp = staffServiceImp;
        this.redisUtil = redisUtil;
    }

    /**
     * 注册新用户
     *
     * @param merchantsDTO 商家信息
     * @param request      请求
     */
    @Transactional
    public void insertMerchantsSvc(MerchantsDTO merchantsDTO, HttpServletRequest request) {
        log.info("===========插入新商家SVC=============insertMerchantsSvc==============");
        String sql = "MerchantsMapper.insertMerchants";
        request.getParameter("code");
        //验证手机号是否可用
        String phone = merchantsDTO.getPhone();
        // 从 request 获取填写的验证码
        String code = request.getParameter("code");
        //验证电话格式
        staffServiceImp.checkPhoneSvc(phone);
        // 从redisUtil获取电话验证码
        if (!redisUtil.hasKey("phone" + phone)) {
            log.info("===========插入新商家SVC=======失败，手机号未获取验证码或过期=============");
            throw new RuntimeException("手机号未获取验证码或过期，请先获取验证码！");
        }
        String codeRedis = (String) redisUtil.get("phone" + phone);
        if (!codeRedis.equals(code)) {
            log.info("===========插入新商家SVC=======失败，验证码错误=============");
            throw new RuntimeException("手机号或者验证码错误，请重新输入！");
        }
        merchantsDTO.setCreateTime(new Date());
        merchantsDTO.setStaffCode(merchantsDTO.getPhone());
        merchantsDTO.setOrderNumber(0);
        merchantsDTO.setScore(0.0);
        merchantsDTO.setState("0");
        String password = merchantsDTO.getPassword();
        String hex = DigestUtils.md5DigestAsHex(password.getBytes());
        merchantsDTO.setPassword(hex);
        int count = sqlSessionTemplate.insert(sql, merchantsDTO);
        if (count < 1) {
            log.info("===========插入新商家SVC=======失败，成功条数小于1=============");
            throw new RuntimeException("插入商家失败");
        }
    }

    /**
     * 商家忘记密码
     *
     * @param merchantsDTO 商家密码
     * @param code         验证码
     */
    public void forgetPasswordSvc(MerchantsDTO merchantsDTO, String code) {
        String phone = merchantsDTO.getPhone();
        String password = merchantsDTO.getPassword();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) {
            throw new RuntimeException("电话和密码不能为空");
        }
        if (redisUtil.hasKey("phone" + phone)) {
            String phoneCode = (String) redisUtil.get("phone" + phone);
            if (phoneCode.equals(code)) {
                String sql = "MerchantsMapper.updatePassword";
                String hex = DigestUtils.md5DigestAsHex(password.getBytes());
                merchantsDTO.setPassword(hex);
                sqlSessionTemplate.update(sql, merchantsDTO);
            } else {
                throw new RuntimeException("验证码错误，请重新输入");
            }
        } else {
            throw new RuntimeException("验证码不存在，请获取验证码");
        }
    }

    /**
     * 商家手机号登录
     *
     * @param merchantsDTO 商家帐号和密码
     * @return Token
     */
    public Map<String, Object> loginSvc(MerchantsDTO merchantsDTO) {
        String sql = "MerchantsMapper.login";
        String password = merchantsDTO.getPassword();
        String phone = merchantsDTO.getPhone();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(password)) {
            throw new RuntimeException("手机密码不能为空");
        }
        log.info("==============密码加密查询================");
        String hex = DigestUtils.md5DigestAsHex(password.getBytes());
        merchantsDTO.setPassword(hex);
        MerchantsDTO merchants = sqlSessionTemplate.selectOne(sql, merchantsDTO);
        log.info("==============帐号密码错误================");
        if (merchants == null) {
            throw new RuntimeException("帐号或密码不正确");
        }
        Map<String, Object> map = new HashMap<>();
        String token = TokenUtil.sign(JSONObject.toJSONString(merchants));
        map.put("token", token);
        map.put("userInfo", merchants);
        return map;
    }

    /**
     * 更改商家信息
     *
     * @param merchantsDTO 商家信息
     */
    public void updateMerchantsSvc(MerchantsDTO merchantsDTO, HttpServletRequest request) {
        String state = merchantsDTO.getState();
        if (!StringUtils.isEmpty(state)) {
            JSONObject info = TokenUtil.getStaffInfo(request);
            String currentState = info.getString("state");
            //用户当前状态为注销、封停、审核中时，无法修改状态
            if ("0".equals(currentState) || "4".equals(currentState) || "3".equals(currentState)) {
                throw new RuntimeException("当前状态无法修改信息！");
            }
            //用户只能更改状态为注销、暂停营业、正常营业
            if ("4".equals(state)) {
                throw new RuntimeException("无法修改为当前状态！");
            }
        } else {
            state = "0";
        }
        merchantsDTO.setPassword(null);
        merchantsDTO.setStaffCode(null);
        merchantsDTO.setScore(null);
        merchantsDTO.setPhone(null);
        merchantsDTO.setCreateTime(null);
        merchantsDTO.setOrderNumber(null);
        //修改信息后需要重新审核
        merchantsDTO.setState(state);
        String sql = "MerchantsMapper.updateMerchants";
        int update = sqlSessionTemplate.update(sql, merchantsDTO);
        if (update < 1) {
            throw new RuntimeException("更新失败，请稍后再试！");
        }
    }

    /**
     * 审核商家状态
     */
    public void auditMerchantsSvc(HttpServletRequest request) {
        JSONObject info = TokenUtil.getStaffInfo(request);
        if (!"admin".equals(info.getString("staffType"))) {
            throw new RuntimeException("权限不够");
        }
        String state = request.getParameter("state");
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(state)) {
            throw new RuntimeException("参数不正确");
        }
        MerchantsDTO merchantsDTO = new MerchantsDTO();
        merchantsDTO.setState(state);
        merchantsDTO.setId(Integer.parseInt(id));
        String sql = "MerchantsMapper.updateMerchants";
        int update = sqlSessionTemplate.update(sql, merchantsDTO);
        if (update < 1) {
            throw new RuntimeException("更新失败，请稍后再试！");
        }
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 生成的唯一文件名
     */
    public Map<String, String> uploadPicSvc(MultipartFile file) {
        String filename = file.getOriginalFilename();
        assert filename != null;
        String suffix = filename.substring(filename.lastIndexOf("."));
        String newFileName = UUID.randomUUID() + suffix;
        try {
            File newFile = new File(gitPath + newFileName);
            /*if (!newFile.exists()){
                boolean hasCreate = newFile.createNewFile();
                if (!hasCreate){
                    throw new RuntimeException("创建文件失败！");
                }
            }*/
            file.transferTo(newFile);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("fileName", newFileName);
        return map;
    }

    /**
     * 查询所有店铺
     *
     * @param merchantsDTO 店铺查询条件
     * @param request      请求
     * @return 满足条件的店铺
     */
    public List<MerchantsDTO> getAllMerchantsSvc(MerchantsDTO merchantsDTO, HttpServletRequest request) {
        String sql = "MerchantsMapper.getAllMerchants";
        JSONObject staffInfo = TokenUtil.getStaffInfo(request);
        //如果不是超级管理员，只能查看营业的店铺
        if (!"admin".equals(staffInfo.getString("staffType"))) {
            merchantsDTO.setState("1");
        }
        log.info("查询条件是：" + merchantsDTO);
        return sqlSessionTemplate.selectList(sql, merchantsDTO);
    }
}
