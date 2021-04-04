package com.mobai.controller;

import com.alibaba.fastjson.JSONObject;
import com.mobai.dto.StaffDTO;
import com.mobai.service.StaffServiceImp;
import com.mobai.utils.OutDto;
import com.mobai.utils.TokenUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/staff")
public class StaffController {
    private static Logger log = LoggerFactory.getLogger(StaffController.class);
    private StaffServiceImp staffServiceImp;

    @Autowired
    public StaffController(StaffServiceImp staffServiceImp) {
        this.staffServiceImp = staffServiceImp;
    }

    @PostMapping("/insertStaff")
    @ApiOperation(value = "新增用户接口", notes = "新增用户接口")
    public OutDto insertStaffRC(@Valid @RequestBody StaffDTO staffDTO, BindingResult bindingResult, HttpServletRequest request) {
        log.info("==================进入新增用户接口======insertStaffRC===============");
        //若不符合约束
        if (bindingResult.hasErrors()) {
            log.info("============用户实体类规则校验失败：" + Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return OutDto.error("500", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        try {
            //返回生成的唯一帐号
            String staffCode = staffServiceImp.insertStaffSvc(staffDTO, request);
            return OutDto.success(staffCode);
        } catch (RuntimeException e) {
            log.info("============用户实体类插入失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/login")
    @ApiOperation(value = "登录接口", notes = "登录接口")
    public OutDto loginRC(@RequestBody StaffDTO staffDTO) {
        log.info("==================个人登录接口======loginRC===============");
        try {
            Map<String, Object> out = staffServiceImp.loginSvc(staffDTO);
            return OutDto.success(out);
        } catch (RuntimeException e) {
            return OutDto.error("500", e.getMessage());
        }
    }

    @GetMapping("/sendPassCode")
    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    public OutDto sendPassCodeRC(HttpServletRequest request) {
        log.info("==================进入获取验证码接口======getPassCodeRC===============");
        String phone = request.getParameter("phone");
        try {
            staffServiceImp.sendPassCodeSvc(phone);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============获取验证码失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/forgetPassword")
    @ApiOperation(value = "忘记密码重置密码", notes = "忘记密码重置密码")
    public OutDto forgetPasswordRC(@RequestBody StaffDTO staffDTO, HttpServletRequest request) {
        log.info("==================进入忘记密码重置密码接口======forgetPasswordRC===============");
        String code = request.getParameter("code");
        try {
            staffServiceImp.forgetPasswordSvc(staffDTO, code);
            return OutDto.success();
        } catch (RuntimeException e) {
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/updateStaff")
    @ApiOperation(value = "更改个人信息", notes = "更改个人信息")
    public OutDto updateStaffRC(@RequestBody StaffDTO staffDTO, HttpServletRequest request) {
        log.info("==================更改个人信息======updateStaffRC===============");
        try {
            JSONObject staffInfo = TokenUtil.getStaffInfo(request);
            if (!"admin".equals(staffInfo.getString("staffType"))) {
                staffDTO.setState(null);
            }
            staffServiceImp.updateStaffSvc(staffDTO);
            return OutDto.success();
        } catch (RuntimeException e) {
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/getAllStaff")
    @ApiOperation(value = "查询所有用户", notes = "查询所有用户")
    public OutDto getAllStaffRC(@RequestBody StaffDTO staffDTO, HttpServletRequest request) {
        log.info("==================查询所有用户======updateStaffRC===============");
        try {
            JSONObject staffInfo = TokenUtil.getStaffInfo(request);
            if (!"admin".equals(staffInfo.getString("staffType"))) {
                return OutDto.error("500", "不能查看所有用户信息");
            }
            List<StaffDTO> allStaff = staffServiceImp.getAllStaffSvc(staffDTO);
            return OutDto.success(allStaff);
        } catch (RuntimeException e) {
            return OutDto.error("500", e.getMessage());
        }
    }

}
