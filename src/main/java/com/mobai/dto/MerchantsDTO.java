package com.mobai.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mobai.config.DoubleSerializeConfig;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class MerchantsDTO implements Serializable {
    private Integer id;
    @NotBlank(message = "商铺名称不能为空")
    private String merchantsName;
    @NotBlank(message = "商铺简介不能为空")
    private String merchantsContent;
    @NotBlank(message = "商铺地址不能为空")
    private String merchantsAddress;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;
    private String state;
    @NotBlank(message = "商铺图片不能为空")
    private String merchants;
    @NotBlank(message = "商铺接单上限不能为空")
    private Integer orderLimit;
    @JsonSerialize(using = DoubleSerializeConfig.class)
    private Double score;
    private Integer orderNumber;
    private String staffCode;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "电话不能为空")
    private String phone;
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "HH:mm:ss")
    @NotBlank(message = "每天预定开始时间不能为空")
    private Time startTime;
    @JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "HH:mm:ss")
    @NotBlank(message = "每天预定结束时间不能为空")
    private Time endTime;

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMerchantsName() {
        return merchantsName;
    }

    public void setMerchantsName(String merchantsName) {
        this.merchantsName = merchantsName == null ? null : merchantsName.trim();
    }

    public String getMerchantsContent() {
        return merchantsContent;
    }

    public void setMerchantsContent(String merchantsContent) {
        this.merchantsContent = merchantsContent == null ? null : merchantsContent.trim();
    }

    public String getMerchantsAddress() {
        return merchantsAddress;
    }

    public void setMerchantsAddress(String merchantsAddress) {
        this.merchantsAddress = merchantsAddress == null ? null : merchantsAddress.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getMerchants() {
        return merchants;
    }

    public void setMerchants(String merchants) {
        this.merchants = merchants == null ? null : merchants.trim();
    }

    public Integer getOrderLimit() {
        return orderLimit;
    }

    public void setOrderLimit(Integer orderLimit) {
        this.orderLimit = orderLimit;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode == null ? null : staffCode.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }
}
