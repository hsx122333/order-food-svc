package com.mobai.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mobai.config.DoubleSerializeConfig;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class StaffDTO implements Serializable {
    private Integer id;

    private String staffCode;

    private String password;
    @NotBlank(message = "电话不能为空")
    private String phone;
    @NotBlank(message = "状态不能为空")
    private String state;
    @NotBlank(message = "呢称不能为空")
    private String staffName;
    @NotBlank(message = "性别不能为空")
    private String sex;
    @NotNull(message = "年龄不能为空")
    @Max(value = 100,message = "年龄最大不能超过100岁")
    @Min(value = 0,message = "年龄最小为0岁")
    private Integer age;
    @JsonSerialize(using = DoubleSerializeConfig.class)
    @Max(value = 100,message = "分数最大不能超过100岁")
    @Min(value = 0,message = "分数最小为0岁")
    private Double score;
    @NotBlank(message = "类型不能为空")
    private String staffType;

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName == null ? null : staffName.trim();
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex == null ? null : sex.trim();
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
