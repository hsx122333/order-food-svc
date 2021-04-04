package com.mobai.controller;

import com.mobai.dto.OrderParentDTO;
import com.mobai.service.OrderServiceImp;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    private static Logger log = LoggerFactory.getLogger(OrderController.class);
    private OrderServiceImp orderServiceImp;

    @Autowired
    public OrderController(OrderServiceImp orderServiceImp) {
        this.orderServiceImp = orderServiceImp;
    }


    @PostMapping("/getAllOrder")
    @ApiOperation(value = "管理员查看所有订单", notes = "管理员查看所有订单")
    public OutDto getAllOrderRC(@RequestBody HashMap<String, Object> inMap, HttpServletRequest request) {
        log.info("==================管理员查看所有订单======getAllOrderRC===============");
        try {
            List<OrderParentDTO> orderList = orderServiceImp.getAllOrderSvc(inMap, request);
            return OutDto.success(orderList);
        } catch (RuntimeException e) {
            log.info("============管理员查看所有订单失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/getSelfOrder")
    @ApiOperation(value = "顾客或个人查看自己订单", notes = "顾客或个人查看自己订单")
    public OutDto getSelfOrderRC(@RequestBody HashMap<String, Object> inMap, HttpServletRequest request) {
        log.info("==================顾客或个人查看自己订单======getSelfOrderRC===============");
        try {
            List<OrderParentDTO> orderList = orderServiceImp.getSelfOrderSvc(inMap, request);
            return OutDto.success(orderList);
        } catch (RuntimeException e) {
            log.info("============顾客或个人查看自己订单失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/createOrder")
    @ApiOperation(value = "创建订单", notes = "创建订单")
    public OutDto createOrderRC(@Valid @RequestBody OrderParentDTO orderParent, HttpServletRequest request) {
        log.info("==================创建订单======createOrderRC===============");
        try {
            OrderParentDTO order = orderServiceImp.createOrderSvc(orderParent, request);
            return OutDto.success(order);
        } catch (RuntimeException e) {
            log.info("============创建订单失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/cancelOrder")
    @ApiOperation(value = "取消订单", notes = "取消订单")
    public OutDto cancelOrderRC(HttpServletRequest request) {
        log.info("==================取消订单======cancelOrderRC===============");
        try {
            orderServiceImp.cancelOrderSvc(request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============取消订单失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/payOrder")
    @ApiOperation(value = "支付订单", notes = "支付订单")
    public OutDto payOrderRC(HttpServletRequest request) {
        log.info("==================支付订单======payOrderRC===============");
        try {
            orderServiceImp.payOrderSvc(request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============支付订单失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/auditOrder")
    @ApiOperation(value = "审核订单退款", notes = "审核订单退款")
    public OutDto auditOrderRC(HttpServletRequest request) {
        log.info("==================审核订单退款======auditOrderRC===============");
        try {
            orderServiceImp.auditOrderSvc(request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============审核订单退款失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/completeOrder")
    @ApiOperation(value = "完成订单", notes = "完成订单")
    public OutDto completeOrderRC(HttpServletRequest request) {
        log.info("==================完成订单======completeOrderRC===============");
        try {
            orderServiceImp.completeOrderSvc(request);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============完成订单失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/updateOrderInfo")
    @ApiOperation(value = "评价、投诉", notes = "评价、投诉")
    public OutDto updateOrderInfoRC(@RequestBody Map<String, Object> map) {
        log.info("==================评价、投诉======updateOrderInfoRC===============");
        try {
            orderServiceImp.updateOrderInfoSvc(map);
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============评价失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/getSumGroupByMonth")
    @ApiOperation(value = "统计商家1-12每月销售额", notes = "统计商家1-12每月销售额")
    public OutDto getSumGroupByMonthRC(@RequestBody HashMap<String, Integer> inMap) {
        log.info("==================统计商家1-12每月销售额======getSumGroupByMonthRC===============");
        try {
            Map<Integer, Double> sumGroupByMonth = orderServiceImp.getSumGroupByMonthSvc(inMap);
            return OutDto.success(sumGroupByMonth);
        } catch (RuntimeException e) {
            log.info("============统计商家1-12每月销售额失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/sumPriceByGoodsId")
    @ApiOperation(value = "统计商家在某个月份的销售额", notes = "统计商家在某个月份的销售额")
    public OutDto sumPriceByGoodsIdRC(@RequestBody HashMap<String, Integer> inMap) {
        log.info("==================统计商家在某个月份的销售额======getSumGroupByMonthRC===============");
        try {
            Map<String, Object> sumPriceMap = orderServiceImp.sumPriceByGoodsIdSvc(inMap);
            return OutDto.success(sumPriceMap);
        } catch (RuntimeException e) {
            log.info("============统计商家在某个月份的销售额失败：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }


}
