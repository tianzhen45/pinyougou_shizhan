package com.pinyougou.seckill.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.service.BaseService;

import java.util.List;

public interface SeckillGoodsService extends BaseService<TbSeckillGoods> {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param seckillGoods 搜索条件
     * @return 分页信息
     */
    PageInfo<TbSeckillGoods> search(Integer pageNum, Integer pageSize, TbSeckillGoods seckillGoods);

    /**
     * 查询符合条件的秒杀商品
     * @return 秒杀商品列表
     */
    List<TbSeckillGoods> findList();
    /**
     * 根据主键查询
     * @param id 主键
     * @return 实体
     */
    TbSeckillGoods findOneInRedis(Long id);
}
