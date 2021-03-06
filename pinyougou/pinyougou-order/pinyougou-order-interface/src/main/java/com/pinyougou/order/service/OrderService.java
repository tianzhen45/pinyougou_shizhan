package com.pinyougou.order.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.service.BaseService;

import java.util.List;
import java.util.Map;

public interface OrderService extends BaseService<TbOrder> {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param order 搜索条件
     * @return 分页信息
     */
    PageInfo<TbOrder> search(Integer pageNum, Integer pageSize, TbOrder order);

    /**
     * 提交订单
     * @param order 订单信息
     * @return 支付日志id
     */
    String addOrder(TbOrder order);

    /**
     * 查询支付日志信息
     * @param outTradeNo 支付日志id
     * @return 支付日志
     */
    TbPayLog findPayLogByOutTradeNo(String outTradeNo);

    /**
     * 更新支付日志和所有订单的支付状态 已支付
     * @param outTradeNo 支付日志id
     * @param transactionId 微信订单号
     */
    void updateOrderStatus(String outTradeNo, String transactionId);

    /**
     * 查询商家的普通订单
     * @param pageNum
     * @param pageSize
     * @param order
     * @return
     */
    PageInfo<TbOrder> findOrderList(Integer pageNum, Integer pageSize, TbOrder order);

    /**
     * 修改普通订单状态（发货、交易成功）
     * @param order
     */
    void updateStatus(TbOrder order);

    /**
     * 查询商家秒杀订单
     * @param pageNum
     * @param pageSize
     * @param order
     * @return
     */
    Map<String,Object> findSeckillOrderList(Integer pageNum, Integer pageSize, TbSeckillOrder order);

    void deleteSeckillOrder(Long[] ids);


    void updatePayment(TbOrder order);
}
