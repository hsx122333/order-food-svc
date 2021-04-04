package com.mobai.job;

import com.mobai.service.OrderServiceImp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Configuration
@EnableScheduling
public class updateInfoJob {
    private static Logger logger = LogManager.getLogger(updateInfoJob.class);
    private OrderServiceImp orderServiceImp;
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public updateInfoJob(OrderServiceImp orderServiceImp, SqlSessionTemplate sqlSessionTemplate) {
        this.orderServiceImp = orderServiceImp;
        this.sqlSessionTemplate = sqlSessionTemplate;
    }

    /**
     * 每天上午1点触发
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateLastMonth() {
        logger.info("修改商家上月的订单数====定时任务====");
        String sql = "MerchantsMapper.selectAllId";
        List<Integer> ids = sqlSessionTemplate.selectList(sql);
        for (Integer id : ids) {
            orderServiceImp.updateLastMonthSvc(id);
        }
        logger.info("修改商家上月的订单数====定时任务完成====");
    }

    /**
     * 每天上午2点触发
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateAvgScore() {
        logger.info("修改商家评分====定时任务====");
        String sql = "MerchantsMapper.selectAllId";
        List<Integer> ids = sqlSessionTemplate.selectList(sql);
        for (Integer id : ids) {
            orderServiceImp.updateAvgScoreSvc(id);
        }
        logger.info("修改商家评分=====定时任务完成===");
    }

    /**
     * 每天上午3点触发
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void updateStaffScore() {
        logger.info("修改用户评分====定时任务====");
        String sql = "StaffMapper.selectAllId";
        List<Integer> ids = sqlSessionTemplate.selectList(sql);
        for (Integer id : ids) {
            orderServiceImp.updateStaffScoreSvc(id);
        }
        logger.info("修改用户评分=====定时任务完成===");
    }
}
