package com.pinyougou.seckill.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.service.BaseService;

import java.util.List;

public interface SeckillOrderService extends BaseService<TbSeckillOrder> {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param seckillOrder 搜索条件
     * @return 分页信息
     */
    PageInfo<TbSeckillOrder> search(Integer pageNum, Integer pageSize, TbSeckillOrder seckillOrder);

    /**
     * 生成秒杀订单并返回
     * @param seckillGoodsId 秒杀商品id
     * @param userId 用户id
     * @return 秒杀订单号
     */
    String submitOrder(Long seckillGoodsId, String userId) throws Exception;
}
