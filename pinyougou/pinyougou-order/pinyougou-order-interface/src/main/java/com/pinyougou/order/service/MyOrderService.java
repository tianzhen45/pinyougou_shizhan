package com.pinyougou.order.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbOrder;

import java.util.Map;

public interface MyOrderService {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param order 搜索条件
     * @return 分页信息
     */
    PageInfo<TbOrder> search(Integer pageNum, Integer pageSize, TbOrder order);

    /**
     * 查询所有订单
     * @return
     */
    Map<String, Object> findOrderByUserId(Integer pageNum, Integer pageSize, String userId);

    /**
     * 取消订单
     * @param orderId 选中的订单ids数组
     * @return
     */
    void deleteOrder(Long orderId);
}
