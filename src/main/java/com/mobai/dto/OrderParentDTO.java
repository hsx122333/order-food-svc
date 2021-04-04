package com.mobai.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mobai.config.DoubleSerializeConfig;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OrderParentDTO implements Serializable {
    @Null
    private Integer id;
    @NotBlank(message = "商家id不能为空")
    private Integer merchantsId;

    private String merchantsName;
    @NotBlank(message = "顾客id不能为空")
    private Integer staffId;
    private String staffName;
    private String orderCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date createTime;

    private String state;
    @Null
    private String scoreToMerchants;
    @Null
    private String scoreToStaff;
    @JsonSerialize(using = DoubleSerializeConfig.class)
    private Double sumPrice;
    @NotBlank(message = "预定时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date orderTime;
    @JsonSerialize(using = DoubleSerializeConfig.class)
    private Double discount;
    @Null
    private String complaints;
    @Size(min = 1)
    private List<@Valid OrderChildDTO> orderChildDTOList;

    public List<OrderChildDTO> getOrderChildDTOList() {
        return orderChildDTOList;
    }

    public void setOrderChildDTOList(List<OrderChildDTO> orderChildDTOList) {
        this.orderChildDTOList = orderChildDTOList;
    }

    public String getMerchantsName() {
        return merchantsName;
    }

    public void setMerchantsName(String merchantsName) {
        this.merchantsName = merchantsName;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMerchantsId() {
        return merchantsId;
    }

    public void setMerchantsId(Integer merchantsId) {
        this.merchantsId = merchantsId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
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

    public String getScoreToMerchants() {
        return scoreToMerchants;
    }

    public void setScoreToMerchants(String scoreToMerchants) {
        this.scoreToMerchants = scoreToMerchants == null ? null : scoreToMerchants.trim();
    }

    public String getScoreToStaff() {
        return scoreToStaff;
    }

    public void setScoreToStaff(String scoreToStaff) {
        this.scoreToStaff = scoreToStaff == null ? null : scoreToStaff.trim();
    }

    public Double getSumPrice() {
        return sumPrice;
    }

    public void setSumPrice(Double sumPrice) {
        this.sumPrice = sumPrice;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getComplaints() {
        return complaints;
    }

    public void setComplaints(String complaints) {
        this.complaints = complaints == null ? null : complaints.trim();
    }
}
