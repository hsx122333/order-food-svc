package com.mobai.controller;

import com.mobai.dto.GoodsDTO;
import com.mobai.service.GoodsServiceImp;
import com.mobai.utils.OutDto;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/merchants")
public class GoodsController {
    private static Logger log = LoggerFactory.getLogger(GoodsController.class);
    private GoodsServiceImp goodsServiceImp;

    @Autowired
    public GoodsController(GoodsServiceImp goodsServiceImp) {
        this.goodsServiceImp = goodsServiceImp;
    }


    @PostMapping("/getSelfGoods")
    @ApiOperation(value = "商家自己的货物信息", notes = "商家自己的货物信息")
    public OutDto getSelfGoodsRC(@RequestBody GoodsDTO goodsDTO, HttpServletRequest request) {
        log.info("==================商家自己的货物信息======getSelfGoodsRC===============");
        try {
            List<GoodsDTO> selfGoods = goodsServiceImp.getSelfGoodsSvc(goodsDTO, request);
            return OutDto.success(selfGoods);
        } catch (RuntimeException e) {
            log.info("============商家自己的货物信息：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/getMerchantGoods")
    @ApiOperation(value = "用户查看商家货物信息", notes = "用户查看商家货物信息")
    public OutDto getMerchantGoodsRC(@RequestBody GoodsDTO goodsDTO, HttpServletRequest request) {
        log.info("==================用户查看商家货物信息======getMerchantGoodsRC===============");
        try {
            List<GoodsDTO> merchantGoods = goodsServiceImp.getMerchantGoodsSvc(goodsDTO, request);
            return OutDto.success(merchantGoods);
        } catch (RuntimeException e) {
            log.info("============用户查看商家货物信息：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/insertGoods")
    @ApiOperation(value = "点击新增商品", notes = "点击新增商品")
    public OutDto insertGoodsRC(@Valid @RequestBody GoodsDTO goodsDTO, HttpServletRequest request) {
        log.info("==================点击新增商品======insertGoodsRC===============");
        try {
            goodsServiceImp.insertGoodsSvc(goodsDTO, request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============点击新增商品：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/updateGoods")
    @ApiOperation(value = "商家修改商品", notes = "商家修改商品")
    public OutDto updateGoodsRC(@RequestBody GoodsDTO goodsDTO, HttpServletRequest request) {
        log.info("==================商家修改商品======updateGoodsRC===============");
        try {
            if ("0".equals(goodsDTO.getState())) {
                return OutDto.error("500", "审核中禁止修改状态");
            }
            //修改信息时，提交审核
            goodsDTO.setState("0");
            goodsServiceImp.updateGoodsSvc(goodsDTO, request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============商家修改商品：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/updateNumAndPrice")
    @ApiOperation(value = "商家修改商品数量和价格", notes = "商家修改商品数量和价格")
    public OutDto updateNumAndPriceRC(@RequestBody GoodsDTO goodsDTO, HttpServletRequest request) {
        log.info("==================商家修改商品数量和价格======updateGoodsRC===============");
        try {
            GoodsDTO goods = new GoodsDTO();
            goods.setId(goodsDTO.getId());
            goods.setRelationId(goodsDTO.getRelationId());
            goods.setGoodsUnitPrice(goodsDTO.getGoodsUnitPrice());
            goods.setGoodsNumber(goodsDTO.getGoodsNumber());
            goodsServiceImp.updateGoodsSvc(goods, request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============商家修改商品数量和价格：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/onStateGoods")
    @ApiOperation(value = "状态修改商品", notes = "状态修改商品")
    public OutDto onStateGoodsRC(HttpServletRequest request) {
        log.info("==================状态修改商品======onStateGoods===============");
        try {
            goodsServiceImp.onStateGoodsSvc(request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============状态修改商品：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/stopStateGoods")
    @ApiOperation(value = "超级管理员审核", notes = "超级管理员审核")
    public OutDto stopStateGoodsRC(HttpServletRequest request) {
        log.info("==================超级管理员审核======stopStateGoodsRC===============");
        try {
            goodsServiceImp.auditStateGoodsSvc(request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============超级管理员审核：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/deleteGoods")
    @ApiOperation(value = "删除某个商品", notes = "删除某个商品")
    public OutDto deleteGoodsRC(@RequestBody GoodsDTO goodsDTO, HttpServletRequest request) {
        log.info("==================删除某个商品======deleteGoodsRC===============");
        try {
            goodsServiceImp.deleteGoodsSvc(goodsDTO, request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============删除某个商品：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }
}
