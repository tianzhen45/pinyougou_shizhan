package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.order.service.MyOrderService;
import com.pinyougou.pojo.*;
import com.pinyougou.vo.MyOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.*;

@Service
public class MyOrderServiceImpl implements MyOrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    //品优购购物车在redis中的键名
    private static final String CART_LIST = "CART_LIST";

    @Override
    public PageInfo<TbOrder> search(Integer pageNum, Integer pageSize, TbOrder order) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbOrder.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        /**if (StringUtils.isNotBlank(order.getProperty())) {
            criteria.andLike("property", "%" + order.getProperty() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public Map<String, Object> findOrderByUserId(Integer pageNum, Integer pageSize, String userId) {
        //开启分页
        PageHelper.startPage(pageNum,pageSize);
        //返回结果
        Map<String, Object> resultMap = new HashMap<>();
        //创建订单vo列表对象
        List<MyOrderVo> myOrderList = new ArrayList<>();

        //根据用户id查询支付日志
        TbPayLog param = new TbPayLog();
        param.setUserId(userId);
        List<TbPayLog> payLogList = payLogMapper.select(param);
        if (payLogList!= null && payLogList.size()>0) {

            //将订单设置分页
            PageInfo<TbPayLog> pageInfo = PageInfo.of(payLogList);
            //创建订单vo对象
            for (TbPayLog payLog : payLogList) {
                MyOrderVo myOrderVo = new MyOrderVo();
                myOrderVo.setPayLog(payLog);

                //根据支付日志id查询订单列表
                //创建查询条件查询订单基本信息
                Example example = new Example(TbOrder.class);
                example.createCriteria().andEqualTo("orderId", payLog.getOrderList());
                example.orderBy("createTime").desc();
                List<TbOrder> orderList = orderMapper.selectByExample(example);

                if (orderList != null && orderList.size() > 0) {

                    //设置订单基本信息
                    myOrderVo.setOrderList(orderList);
                    //遍历订单列表，查询每个订单商品
                    for (TbOrder tbOrder : orderList) {
                        Example orderItemExample = new Example(TbOrderItem.class);
                        orderItemExample.createCriteria().andEqualTo("orderId", tbOrder.getOrderId());

                        //查询订单商品信息
                        List<TbOrderItem> orderItemList = (List<TbOrderItem>) orderItemMapper.selectByExample(orderItemExample);
                        if (orderItemList != null && orderItemList.size() > 0) {
                            for (TbOrderItem orderItem : orderItemList) {
                                //查询商品规格信息
                                TbItem item = itemMapper.selectByPrimaryKey(orderItem.getItemId());

                                String specStr = item.getSpec().replaceAll("\\{", " ")
                                        .replaceAll("\"", " ")
                                        .replaceAll(",", " ")
                                        .replaceAll("}", " ");
                                myOrderVo.setSpecStr(specStr);

                            }
                            /*ArrayList specList = new ArrayList();
                            for (TbOrderItem orderItem : orderItemList) {
                                String spec = itemMapper.selectByPrimaryKey(orderItem.getItemId()).getSpec();
                                JSONObject parseObject = JSONArray.parseObject(spec);
                                StringBuilder sb = new StringBuilder();
                                for (Object o : parseObject.values()) {
                                    sb.append(o).append(" ");
                                }
                                specList.add(sb.toString());
                                myOrderVo.setSpecList(specList);
                            }*/
                        }
                        myOrderVo.setOrderItemList(orderItemList);

                        //查询商家信息
                        if (tbOrder.getSellerId() != null) {
                            TbSeller seller = sellerMapper.selectByPrimaryKey(tbOrder.getSellerId());
                            if (seller != null) {
                                myOrderVo.setSellerId(tbOrder.getSellerId());
                                myOrderVo.setSellerName(seller.getName());
                            }else {
                                return null;
                            }
                        } else {
                            myOrderVo.setSellerId("pinyougou");
                            myOrderVo.setSellerName("品优购自营");
                        }
                        myOrderList.add(myOrderVo);
                    }
                    resultMap.put("myOrderList", myOrderList);
                    resultMap.put("pageNum", pageInfo.getPageNum());
                    resultMap.put("pages", pageInfo.getPages());
                }
            }
        }
        return resultMap;
    }

    @Override
    public void deleteOrder(Long orderId) {
        if (orderId != null ) {
            TbOrder order = orderMapper.selectByPrimaryKey(orderId);
            //取消订单，设置交易状态为6：交易关闭
            order.setStatus("6");
            orderMapper.updateByPrimaryKeySelective(order);

        }
    }
}
