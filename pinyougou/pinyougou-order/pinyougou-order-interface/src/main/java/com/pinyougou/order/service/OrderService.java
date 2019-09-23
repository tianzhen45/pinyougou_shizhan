package com.pinyougou.order.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.BaseService;

import java.util.List;

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
}
