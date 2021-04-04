package com.mobai.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mobai.config.DoubleSerializeConfig;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

public class GoodsDTO implements Serializable {
    private Integer id;
    @NotBlank(message = "商铺关联id不能为空")
    private Integer relationId;
    @NotBlank(message = "商铺名称不能为空")
    private String goodsName;
    @JsonSerialize(using = DoubleSerializeConfig.class)
    private Double goodsUnitPrice;
    @NotBlank(message = "商铺数量不能为空")
    @Min(value = 0)
    private Integer goodsNumber;

    private String state;

    private Date createTime;
    @NotBlank(message = "商铺图片不能为空")
    private String goodsPic;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public Double getGoodsUnitPrice() {
        return goodsUnitPrice;
    }

    public void setGoodsUnitPrice(Double goodsUnitPrice) {
        this.goodsUnitPrice = goodsUnitPrice;
    }

    public Integer getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(Integer goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state == null ? null : state.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getGoodsPic() {
        return goodsPic;
    }

    public void setGoodsPic(String goodsPic) {
        this.goodsPic = goodsPic == null ? null : goodsPic.trim();
    }
}
