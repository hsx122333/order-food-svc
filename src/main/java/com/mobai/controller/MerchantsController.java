package com.mobai.controller;

import com.mobai.dto.MerchantsDTO;
import com.mobai.service.MerchantsServiceImp;
import com.mobai.utils.OutDto;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/merchants")
public class MerchantsController {
    private static Logger log = LoggerFactory.getLogger(MerchantsController.class);
    private MerchantsServiceImp merchantsServiceImp;

    @Autowired
    public MerchantsController(MerchantsServiceImp merchantsServiceImp) {
        this.merchantsServiceImp = merchantsServiceImp;
    }


    @PostMapping("/insertMerchants")
    @ApiOperation(value = "新增商家接口", notes = "新增商家接口")
    public OutDto insertMerchantsRC(@Valid @RequestBody MerchantsDTO merchantsDTO, BindingResult bindingResult, HttpServletRequest request) {
        log.info("==================进入新增商家接口======insertMerchantsRC===============");
        //若不符合约束
        if (bindingResult.hasErrors()) {
            log.info("============商家实体类规则校验失败：" + Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            return OutDto.error("500", Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
        }
        try {
            merchantsServiceImp.insertMerchantsSvc(merchantsDTO, request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============用户实体类插入失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/forgetPassword")
    @ApiOperation(value = "忘记密码重置密码", notes = "忘记密码重置密码")
    public OutDto forgetPasswordRC(@RequestBody MerchantsDTO merchantsDTO, HttpServletRequest request) {
        log.info("==================进入忘记密码重置密码接口======forgetPasswordRC===============");
        String code = request.getParameter("code");
        try {
            merchantsServiceImp.forgetPasswordSvc(merchantsDTO, code);
            return OutDto.success();
        } catch (RuntimeException e) {
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/updateMerchants")
    @ApiOperation(value = "更改商家信息", notes = "更改商家信息")
    public OutDto updateStaffRC(@RequestBody MerchantsDTO merchantsDTO, HttpServletRequest request) {
        log.info("==================更改商家信息======updateStaffRC===============");
        try {
            merchantsServiceImp.updateMerchantsSvc(merchantsDTO, request);
            return OutDto.success();
        } catch (RuntimeException e) {
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/auditMerchants")
    @ApiOperation(value = "审核商家状态", notes = "审核商家状态")
    public OutDto auditMerchantsRC(HttpServletRequest request) {
        log.info("==================审核商家状态======updateStaffRC===============");
        try {
            merchantsServiceImp.auditMerchantsSvc(request);
            return OutDto.success();
        } catch (RuntimeException e) {
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/login")
    @ApiOperation(value = "登录接口", notes = "登录接口")
    public OutDto loginRC(@RequestBody MerchantsDTO merchantsDTO) {
        log.info("==================登录商家接口======loginRC===============");
        try {
            Map<String, Object> out = merchantsServiceImp.loginSvc(merchantsDTO);
            return OutDto.success(out);
        } catch (RuntimeException e) {
            return OutDto.error("500", e.getMessage());
        }
    }

    /**
     * 上传图片
     *
     * @param file 图片
     * @return OutDTO
     */
    @PostMapping("/uploadPic")
    @ApiOperation(value = "上传图片", notes = "上传图片")
    public OutDto uploadPicRC(MultipartFile file) {
        log.info("==================上传图片======uploadPic===============");
        try {
            Map<String, String> map = merchantsServiceImp.uploadPicSvc(file);
            return OutDto.success(map);
        } catch (RuntimeException e) {
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/getAllMerchants")
    @ApiOperation(value = "用户查看所有商家", notes = "用户查看所有商家")
    public OutDto getAllMerchantsRC(@RequestBody MerchantsDTO merchantsDTO, HttpServletRequest request) {
        log.info("==================用户查看所有商家======getMerchantGoodsRC===============");
        try {
            List<MerchantsDTO> merchants = merchantsServiceImp.getAllMerchantsSvc(merchantsDTO, request);
            return OutDto.success(merchants);
        } catch (RuntimeException e) {
            log.info("============用户查看所有商家：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }


}
