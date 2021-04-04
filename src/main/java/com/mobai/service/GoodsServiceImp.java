package com.mobai.service;

import com.alibaba.fastjson.JSONObject;
import com.mobai.dto.GoodsDTO;
import com.mobai.utils.TokenUtil;
import io.swagger.models.auth.In;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class GoodsServiceImp {
    private static Logger log = LoggerFactory.getLogger(GoodsServiceImp.class);
//    private static final String gitPath = System.getProperty("user.dir") + File.separator + "logistics" + File.separator + "uploads" + File.separator;

    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public GoodsServiceImp(SqlSessionTemplate sqlSessionTemplate) {
        this.sqlSessionTemplate = sqlSessionTemplate;
    }


    /**
     * 商家查看自己的商品
     *
     * @param goodsDTO 商家自己的货物信息
     * @return List<GoodsDTO>
     */
    public List<GoodsDTO> getSelfGoodsSvc(GoodsDTO goodsDTO, HttpServletRequest request) {
        String sql = "GoodsMapper.getSelfGoods";
        JSONObject info = TokenUtil.getStaffInfo(request);
        Integer id = info.getInteger("id");
        goodsDTO.setRelationId(id);
        return sqlSessionTemplate.selectList(sql, goodsDTO);
    }

    /**
     * 用户或者超级管理员查看商家的商品
     *
     * @return List<GoodsDTO>
     */
    public List<GoodsDTO> getMerchantGoodsSvc(GoodsDTO goodsDTO, HttpServletRequest request) {
        String merchantId = request.getParameter("merchantId");
        String sql = "GoodsMapper.getMerchantGoods";
        goodsDTO.setRelationId(Integer.parseInt(merchantId));
        JSONObject staffInfo = TokenUtil.getStaffInfo(request);
        //如果不是超级管理员，只能查看上架的商品
        if (!"admin".equals(staffInfo.getString("state"))) {
            goodsDTO.setState("1");
        }
        return sqlSessionTemplate.selectList(sql, goodsDTO);
    }

    /**
     * 点击新增商品
     *
     * @param goodsDTO 商品信息
     * @param request  请求
     */
    public void insertGoodsSvc(GoodsDTO goodsDTO, HttpServletRequest request) {
        String sql = "GoodsMapper.insertGoods";
        JSONObject info = TokenUtil.getStaffInfo(request);
        Integer id = info.getInteger("id");
        goodsDTO.setRelationId(id);
        goodsDTO.setCreateTime(new Date());
        //设置商品为审核状态
        goodsDTO.setState("0");
        int insert = sqlSessionTemplate.insert(sql, goodsDTO);
        if (insert < 1) {
            throw new RuntimeException("上传失败");
        }
    }

    /**
     * 商家修改商品
     *
     * @param goodsDTO 商品信息
     * @param request  请求
     */
    public void updateGoodsSvc(GoodsDTO goodsDTO, HttpServletRequest request) {
        JSONObject info = TokenUtil.getStaffInfo(request);
        Integer id = info.getInteger("id");
        if (goodsDTO.getId() == null || goodsDTO.getRelationId() == null || !goodsDTO.getRelationId().equals(id)) {
            throw new RuntimeException("修改失败");
        }
        String sql = "GoodsMapper.updateGoods";
        int insert = sqlSessionTemplate.update(sql, goodsDTO);
        if (insert < 1) {
            throw new RuntimeException("修改失败");
        }
    }

    /**
     * 更改状态
     */
    private void updateStateGoods(GoodsDTO goodsDTO, HttpServletRequest request) {
        JSONObject info = TokenUtil.getStaffInfo(request);
        //设置物品关联登录商家的id
        Integer id = info.getInteger("id");
        goodsDTO.setRelationId(id);
        updateGoodsSvc(goodsDTO, request);
    }

    /**
     * 上/下架
     */
    public void onStateGoodsSvc(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        String state = checkState(id);
        GoodsDTO goodsDTO = new GoodsDTO();
        state = "1".equals(state) ? "1" : "2";
        goodsDTO.setState(state);
        goodsDTO.setId(id);
        updateStateGoods(goodsDTO, request);
    }

    /**
     * 超级管理员审核
     */
    public void auditStateGoodsSvc(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        JSONObject staffInfo = TokenUtil.getStaffInfo(request);
        String staffType = staffInfo.getString("staffType");
        //校验是否是超级管理员
        if (!"admin".equals(staffType)) {
            throw new RuntimeException("权限不够");
        }
        String state = request.getParameter("state");
        if (StringUtils.isEmpty(state) || StringUtils.isEmpty(id)) {
            throw new RuntimeException("参数错误");
        }
        GoodsDTO goodsDTO = new GoodsDTO();
        goodsDTO.setState(state);
        goodsDTO.setId(id);
        updateStateGoods(goodsDTO, request);
    }

    private String checkState(int id) {
        String sqlId = "GoodsMapper.selectById";
        String state = sqlSessionTemplate.selectOne(sqlId, id);
        if (null == state) {
            throw new RuntimeException("该商品不存在");
        }
        if (!"1".equals(state) && !"2".equals(state)) {
            throw new RuntimeException("审核中或者封停状态不能修改状态");
        }
        return state;
    }

    /**
     * 删除某个商品
     *
     * @param goodsDTO 商品信息
     * @param request  请求
     */
    public void deleteGoodsSvc(GoodsDTO goodsDTO, HttpServletRequest request) {
        String sql = "GoodsMapper.deleteGoods";
        JSONObject info = TokenUtil.getStaffInfo(request);
        Integer id = info.getInteger("id");
        if (goodsDTO.getId() == null || goodsDTO.getRelationId() == null || !goodsDTO.getRelationId().equals(id)) {
            throw new RuntimeException("删除失败");
        }
        int insert = sqlSessionTemplate.delete(sql, goodsDTO);
        if (insert < 1) {
            throw new RuntimeException("删除失败");
        }
    }

    /**
     * 减少库存
     *
     * @param id  商品id
     * @param num 减少数量
     */
    void decGoodsNumSvc(Integer id, Integer num) {
        String sql = "GoodsMapper.decGoodsNum";
        HashMap<String, Integer> map = new HashMap<>();
        map.put("id", id);
        map.put("num", num);
        sqlSessionTemplate.update(sql, map);
    }

    /**
     * 增加库存
     * @param id 商品id
     * @param num 增加数量
     */
    void incGoodsNumSvc(Integer id, Integer num) {
        num = 0 - num;
        decGoodsNumSvc(id, num);
    }

}
