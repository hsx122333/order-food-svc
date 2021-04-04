package com.mobai.controller;

import com.mobai.service.OrderServiceImp;
import com.mobai.utils.OutDto;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/task")
public class TaskController {
    private static Logger log = LoggerFactory.getLogger(OrderController.class);
    private OrderServiceImp orderServiceImp;

    @Autowired
    public TaskController(OrderServiceImp orderServiceImp) {
        this.orderServiceImp = orderServiceImp;
    }

    @PostMapping("/updateLastMonth")
    @ApiOperation(value = "修改商家上月的订单数", notes = "修改商家上月的订单数")
    public OutDto updateLastMonthRC(HttpServletRequest request) {
        log.info("==================修改商家上月的订单数======updateLastMonthRC===============");
        try {
            String id = request.getParameter("merchantsId");
            orderServiceImp.updateLastMonthSvc(Integer.parseInt(id));
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============修改商家上月的订单数：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/updateAvgScore")
    @ApiOperation(value = "修改商家评分", notes = "修改商家评分")
    public OutDto updateAvgScoreRC(HttpServletRequest request) {
        log.info("==================修改商家评分======updateLastMonthRC===============");
        try {
            String id = request.getParameter("merchantsId");
            orderServiceImp.updateAvgScoreSvc(Integer.parseInt(id));
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============修改商家评分：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }

    @PostMapping("/updateStaffScore")
    @ApiOperation(value = "修改用户评分", notes = "修改用户评分")
    public OutDto updateStaffScoreRC(HttpServletRequest request) {
        log.info("==================修改用户评分======updateStaffScoreRC===============");
        try {
            String id = request.getParameter("merchantsId");
            orderServiceImp.updateStaffScoreSvc(Integer.parseInt(id));
            return OutDto.success();
        } catch (RuntimeException e) {
            log.info("============修改用户评分：" + e.getMessage());
            return OutDto.error("500", e.getMessage());
        }
    }
}
