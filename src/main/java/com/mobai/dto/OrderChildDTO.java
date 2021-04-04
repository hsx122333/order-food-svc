package com.mobai.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mobai.config.DoubleSerializeConfig;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import java.io.Serializable;

public class OrderChildDTO extends GoodsDTO implements Serializable {
    @Null
    private Integer id;

    private Integer parentId;
    @NotBlank(message = "商品id不能为空")
    private Integer goodsId;
    @NotBlank(message = "商品数量不能为空")
    private Integer number;
    @JsonSerialize(using = DoubleSerializeConfig.class)
    private Double price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
