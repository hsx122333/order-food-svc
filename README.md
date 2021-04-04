# 工程简介

本次毕业设计将应用所学的专业知识，理论联系实际，采用SpringBoot和VUE等技术对网上预定餐的一个前后台进行功能的开发，使用设计前端界面，具体内容包括：
1. 顾客寻找商家预定往后三天内的早餐午餐晚餐，定餐越早，价格越优惠。订单生效前24小时内与商家协商后可取消。（支付界面功能可以不实现，界面有就行）
2. 商家设定接单额上限（可适当增加），按照顾客需求购买食材，避免不必要的损失。
3. 商家后台可对自己的店铺卖出餐品做出统计分析，使用echarts将数据可视化，挖掘出热销餐品及推荐套餐信息，增加利润。
4. 顾客取餐后，商家客户相互评级打分，主要看守信度和餐品的口味好坏。
5. 顾客守信程度越高，享受优惠越多。
6. 管理员可以对申请开店的商家进行审核，审核通过后才能正常营业。可以对被投诉过多的商家或顾客执行停封账户处罚。

# 延伸阅读

1. merchants 商家信息表：  
    Id（店铺id），merchants_name（店铺名称），(merchants_content)店铺简介，merchants_address（店铺位置）,create_time（开店时间）,
    state（状态 0待审核 1正常营业 2休息中 3注销店铺 4封停），merchants（图片名称），order_limit（接单上限），score（评分）,
    order_number（上月订单），phone（商家手机号码），staff（商家登录帐号），password（商家登录密码）,start_time（预定开始时间）,
    end_time(预定结束时间)

2. goods 商品表：  
    Id（商品id），relation_id（关联店铺id），goods_name（商品名称）,goods_unit_price（商品单价），goods_number（商品库存），
    state(状态：0 审核 1 上架 2 下架 3 审核不通过``)，create_time（上架时间），goods_pic（商品图片名称）

3. staff 人员信息表:  
    Id（人员id）,staff_code(帐号),password（密码）,phone（电话）,state（状态:0正常1异常）,staff_name（昵称）,sex（性别），
    age（年龄），score（评分）,staff_type（类型:admin、customer）

4. order_parent 父订单表：  
    Id（父订单id），merchants_id（店铺id），staff_id（用户id），order_code（订单编码），create_time（下单时间），
    state（状态：0未付款，1已付款，2退款中，3退款成功，4退款失败，5取消订单，6已完成），score_to_merchants（用户对店铺打分）,
    score_to_staff（店铺对用户打分），sum_price（总金额）,order_time（预定时间），discount（折扣），complaints（投诉）

5. order_child 子订单表：  
    Id（子订单id），parent_id（父订单id）,goods_id（商品id）,number（数量）,price（价格）

