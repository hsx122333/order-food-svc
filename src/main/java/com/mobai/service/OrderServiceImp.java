package com.mobai.service;

import com.alibaba.fastjson.JSONObject;
import com.mobai.dto.*;
import com.mobai.utils.TokenUtil;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderServiceImp {

    private SqlSessionTemplate sqlSessionTemplate;
    private MerchantsServiceImp merchantsServiceImp;
    private GoodsServiceImp goodsServiceImp;

    @Autowired
    public OrderServiceImp(SqlSessionTemplate sqlSessionTemplate, MerchantsServiceImp merchantsServiceImp, GoodsServiceImp goodsServiceImp) {
        this.sqlSessionTemplate = sqlSessionTemplate;
        this.merchantsServiceImp = merchantsServiceImp;
        this.goodsServiceImp = goodsServiceImp;
    }

    /**
     * 管理员查询所有满足条件的订单
     *
     * @param inMap   条件
     * @param request 请求
     * @return 订单集合
     */
    public List<OrderParentDTO> getAllOrderSvc(HashMap<String, Object> inMap, HttpServletRequest request) {
        String sql = "OrderParentMapper.getOrderByParam";
        JSONObject info = TokenUtil.getStaffInfo(request);
        if (!"admin".equals(info.getString("staffType"))) {
            throw new RuntimeException("权限不够");
        }
        return sqlSessionTemplate.selectList(sql, inMap);
    }

    /**
     * 店铺或者顾客查看所有与自己有关的订单
     *
     * @param inMap   查询条件
     * @param request 请求
     * @return 订单集合
     */
    public List<OrderParentDTO> getSelfOrderSvc(HashMap<String, Object> inMap, HttpServletRequest request) {
        String sql = "OrderParentMapper.getOrderByParam";
        JSONObject info = TokenUtil.getStaffInfo(request);
        //判断调用者是不是商家
        if (null == info.getString("merchantsName")) {
            inMap.put("staffId", info.getString("id"));
        } else {
            inMap.put("merchantsId", info.getString("id"));
        }
        return sqlSessionTemplate.selectList(sql, inMap);
    }

    /**
     * 父订单插入
     *
     * @param orderParent 父订单对象
     * @return 返回插入生成的id
     */
    private Integer insertOrderParentSvc(OrderParentDTO orderParent) {
        String sql = "OrderParentMapper.insertOrderParent";
        int insert = sqlSessionTemplate.insert(sql, orderParent);
        if (insert < 1) {
            throw new RuntimeException("生成父订单失败");
        }
        return orderParent.getId();
    }

    /**
     * 插入子订单
     *
     * @param orderChildList 子订单集合
     */
    private void insertOrderChildSvc(List<OrderChildDTO> orderChildList) {
        String sql = "OrderParentMapper.insertOrderChildren";
        int insert = sqlSessionTemplate.insert(sql, orderChildList);
        if (insert < 1) {
            throw new RuntimeException("生成子订单失败");
        }
    }

    /**
     * 生成订单
     *
     * @param orderParent 订单信息
     */
    @Transactional
    public OrderParentDTO createOrderSvc(OrderParentDTO orderParent, HttpServletRequest request) {
        Date orderTime = orderParent.getOrderTime();
        LocalDateTime newOrderTime = orderTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        int day = newOrderTime.getDayOfYear() - now.getDayOfYear();
        if (day > 3 || day < 0) {
            throw new RuntimeException("只能预定未来3天以内的");
        }
        double discount = 1.00;
        discount = day == 0 ? discount : day == 1 ? 0.90 : day == 2 ? 0.85 : 0.80;
        orderParent.setDiscount(discount);
        Integer merchantsId = orderParent.getMerchantsId();
        MerchantsDTO merchantsDTO = new MerchantsDTO();
        merchantsDTO.setId(merchantsId);
        List<MerchantsDTO> merchants = merchantsServiceImp.getAllMerchantsSvc(merchantsDTO, request);
        if (CollectionUtils.isEmpty(merchants)) {
            throw new RuntimeException("该商家已关闭");
        }
        MerchantsDTO merchant = merchants.get(0);
        LocalTime startTime = merchant.getStartTime().toLocalTime();
        LocalTime endTime = merchant.getEndTime().toLocalTime();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        if (newOrderTime.getHour() - startTime.getHour() < 0 || newOrderTime.getHour() - endTime.getHour() > 0) {
            throw new RuntimeException("该商家营业时间在 " + startTime.format(dateTimeFormatter) + " 到 " + startTime.format(dateTimeFormatter) + " 之间");
        }
        dateTimeFormatter = DateTimeFormatter.ofPattern("YYYYMMddHHmmss");
        String orderCode = now.format(dateTimeFormatter);
        StringBuilder strBuilder = new StringBuilder(orderParent.getStaffId().toString());
        if (strBuilder.length() < 4) {
            for (int i = 0; i < 4 - strBuilder.length(); i++) {
                strBuilder.insert(0, "0");
            }
        } else if (strBuilder.length() > 4) {
            strBuilder.substring(strBuilder.length() - 4);
        }
        orderCode += strBuilder.toString();
        orderParent.setOrderCode(orderCode);
        //未支付状态
        orderParent.setState("0");
        orderParent.setCreateTime(new Date());
        double sumPrice = 0.0;
        List<OrderChildDTO> list = new ArrayList<>();
        List<OrderChildDTO> orderChildList = orderParent.getOrderChildDTOList();
        for (OrderChildDTO orderChild : orderChildList) {
            if (StringUtils.isEmpty(orderChild.getGoodsId()) || StringUtils.isEmpty(orderChild.getNumber()) || orderChild.getNumber() == 0) {
                continue;
            }
            Integer goodsId = orderChild.getGoodsId();
            GoodsDTO goodsDTO = new GoodsDTO();
            goodsDTO.setId(goodsId);
            List<GoodsDTO> goodsList = goodsServiceImp.getMerchantGoodsSvc(goodsDTO, request);
            if (!CollectionUtils.isEmpty(goodsList)) {
                GoodsDTO goods = goodsList.get(0);
                if (goods.getGoodsNumber() < orderChild.getNumber()) {
                    throw new RuntimeException(goods.getGoodsName() + "数量不足，请重新选择");
                }
                double sum = goods.getGoodsUnitPrice() * orderChild.getNumber();
                orderChild.setPrice(sum);
                orderChild.setGoodsName(goods.getGoodsName());
                orderChild.setGoodsPic(goods.getGoodsPic());
                list.add(orderChild);
                sumPrice += sum;
                //商品数量减少
                goodsServiceImp.decGoodsNumSvc(goodsId, orderChild.getNumber());
            }
            //更新完毕再次验证数据库数量是否为负数
            List<GoodsDTO> goodsList2 = goodsServiceImp.getMerchantGoodsSvc(goodsDTO, request);
            if (goodsList2.get(0).getGoodsNumber() < 0) {
                throw new RuntimeException(goodsList2.get(0).getGoodsName() + "数量不足，请重新选择");
            }
        }
        orderParent.setSumPrice(sumPrice);
        Integer parentId = insertOrderParentSvc(orderParent);
        orderParent.setId(parentId);
        for (OrderChildDTO orderChildDTO : list) {
            orderChildDTO.setParentId(parentId);
        }
        insertOrderChildSvc(list);
        orderParent.setOrderChildDTOList(list);
        return orderParent;
    }

    private void updateStateSvc(Integer orderId, String state) {
        if (StringUtils.isEmpty(orderId)) {
            throw new RuntimeException("订单参数不正确");
        }
        String sql = "OrderParentMapper.updateInfo";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", orderId);
        map.put("state", state);
        int update = sqlSessionTemplate.update(sql, map);
        if (update < 1) {
            throw new RuntimeException("更新失败");
        }
    }

    /**
     * 取消订单
     *
     * @param request 请求 ? orderId
     */
    public void cancelOrderSvc(HttpServletRequest request) {
        Integer orderId = Integer.parseInt(request.getParameter("orderId"));
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", orderId);
        List<OrderParentDTO> orderList = getSelfOrderSvc(map, request);
        if (CollectionUtils.isEmpty(orderList)) {
            throw new RuntimeException("订单不存在");
        }
        OrderParentDTO order = orderList.get(0);
        String state = order.getState();
        String newState = null;
        if ("0".equals(state)) {
            newState = "5";
        } else if ("1".equals(state)) {
            newState = "2";
        }
        updateStateSvc(orderId, newState);
        //取消订单增加对应的商品数量
        List<OrderChildDTO> orderChildList = order.getOrderChildDTOList();
        for (OrderChildDTO orderChild : orderChildList) {
            Integer goodsId = orderChild.getGoodsId();
            Integer number = orderChild.getNumber();
            goodsServiceImp.incGoodsNumSvc(goodsId, number);
        }
    }

    /**
     * 支付接口
     *
     * @param request 请求 ? orderId
     */
    public void payOrderSvc(HttpServletRequest request) {
        Integer orderId = Integer.parseInt(request.getParameter("orderId"));
        updateStateSvc(orderId, "1");
    }

    /**
     * 审核退款
     * isAgree 0代表同意
     */
    public void auditOrderSvc(HttpServletRequest request) {
        Integer orderId = Integer.parseInt(request.getParameter("orderId"));
        String isAgree = request.getParameter("isAgree");
        String state = "0".equals(isAgree) ? "3" : "4";
        updateStateSvc(orderId, state);
    }

    /**
     * 已完成
     *
     * @param request 请求 orderId
     */
    public void completeOrderSvc(HttpServletRequest request) {
        Integer orderId = Integer.parseInt(request.getParameter("orderId"));
        updateStateSvc(orderId, "6");
    }

    /**
     * 投诉、评价
     *
     * @param map 至少包括id
     */
    public void updateOrderInfoSvc(Map<String, Object> map) {
        //防止这个接口修改状态
        map.put("state", null);
        Object id = map.get("id");
        if (id == null) {
            throw new RuntimeException("参数不正确");
        }
        String sql = "OrderParentMapper.updateInfo";
        int update = sqlSessionTemplate.update(sql, map);
        if (update < 1) {
            throw new RuntimeException("投诉失败");
        }
    }

    /**
     * 统计商家1-12每月销售额
     *
     * @return 反回map（1,100）（2,2000）
     */
    public Map<Integer, Double> getSumGroupByMonthSvc(HashMap<String, Integer> inMap) {
        String sql = "OrderParentMapper.getSumGroupByMonth";
        List<HashMap> mapList = sqlSessionTemplate.selectList(sql, inMap);
        Map<Integer, Double> returnMap = new HashMap<>();
        for (Integer i = 1; i <= 12; i++) {
            boolean isNoExit = true;
            Integer month = 0;
            Double price = 0.00;
            for (HashMap map : mapList) {
                month = (Integer) map.get("month");
                if (i.equals(month)) {
                    isNoExit = false;
                    price = (Double) map.get("price");
                    break;
                }
            }
            if (isNoExit) {
                returnMap.put(i, 0.00);
            } else {
                returnMap.put(month, price);
            }
        }
        return returnMap;
    }

    /**
     * 统计商家在某个月份的销售额
     *
     * @param inMap 月份数，merchantsId
     * @return 前4的商品名称和销售额，以及总金额
     */
    public Map<String, Object> sumPriceByGoodsIdSvc(HashMap<String, Integer> inMap) {
        String sql = "sumPriceByGoodsId";
        List<HashMap<String, Object>> list = sqlSessionTemplate.selectList(sql, inMap);
        List<Double> prices = new ArrayList<>();
        List<String> goodsNames = new ArrayList<>();
        for (HashMap<String, Object> map : list) {
            Double price = (Double) map.get("price");
            String goodsNa = (String) map.get("goodsName");
            prices.add(price);
            goodsNames.add(goodsNa);
        }
        Double sumAll = 0.00;
        Double sum4ToLast = 0.00;
        for (int i = 0; i < prices.size(); i++) {
            Double price = prices.get(0);
            sumAll += price;
            if (i > 3) {
                sum4ToLast += price;
            }
        }
        if (prices.size() > 4) {
            prices.subList(0, 4);
            goodsNames.subList(0, 4);
        } else {
            int size = prices.size();
            for (int i = 0; i < 4 - size; i++) {
                prices.add(0.00);
                goodsNames.add(" ");
            }
        }
        prices.add(sum4ToLast);
        goodsNames.add("其它");
        Map<String, Object> map = new HashMap<>();
        map.put("prices", prices);
        map.put("goodsName", goodsNames);
        map.put("sum", sumAll);
        return map;
    }

    /**
     * 修改商家上月的订单数
     *
     * @param merchantsId 商家id
     */
    public void updateLastMonthSvc(Integer merchantsId) {
        if (merchantsId == null) {
            throw new RuntimeException("参数错误，merchantsId不能为空");
        }
        String sql = "OrderParentMapper.countLastMonth";
        HashMap<String, Object> inMap = new HashMap<>();
        inMap.put("merchantsId", merchantsId);
        Integer count = sqlSessionTemplate.selectOne(sql, inMap);
        if (count != null && count >= 0) {
            sql = "MerchantsMapper.updateMerchants";
            MerchantsDTO merchantsDTO = new MerchantsDTO();
            merchantsDTO.setId(merchantsId);
            merchantsDTO.setOrderNumber(count);
            sqlSessionTemplate.update(sql, merchantsDTO);
        }
    }

    /**
     * 修改商家评分
     *
     * @param merchantsId 商家id
     */
    public void updateAvgScoreSvc(Integer merchantsId) {
        if (merchantsId == null) {
            throw new RuntimeException("参数错误，merchantsId不能为空");
        }
        String sql = "OrderParentMapper.avgScore";
        HashMap<String, Object> inMap = new HashMap<>();
        inMap.put("merchantsId", merchantsId);
        Double count = sqlSessionTemplate.selectOne(sql, inMap);
        if (count != null && count >= 0) {
            sql = "MerchantsMapper.updateMerchants";
            MerchantsDTO merchantsDTO = new MerchantsDTO();
            merchantsDTO.setId(merchantsId);
            merchantsDTO.setScore(count);
            sqlSessionTemplate.update(sql, merchantsDTO);
        }
    }

    /**
     * 修改用户评分
     *
     * @param staffId 用户id
     */
    public void updateStaffScoreSvc(Integer staffId) {
        if (staffId == null) {
            throw new RuntimeException("参数错误，staffId");
        }
        String sql = "OrderParentMapper.avgScore2";
        HashMap<String, Object> inMap = new HashMap<>();
        inMap.put("merchantsId", staffId);
        Double count = sqlSessionTemplate.selectOne(sql, inMap);
        if (count != null && count >= 0) {
            sql = "StaffMapper.updateStaff";
            StaffDTO staffDTO = new StaffDTO();
            staffDTO.setId(staffId);
            staffDTO.setScore(count);
            sqlSessionTemplate.update(sql, staffDTO);
        }
    }

}
