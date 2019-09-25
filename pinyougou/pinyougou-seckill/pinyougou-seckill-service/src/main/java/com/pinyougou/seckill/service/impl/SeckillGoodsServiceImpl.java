package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class SeckillGoodsServiceImpl extends BaseServiceImpl<TbSeckillGoods> implements SeckillGoodsService {
    //在redis中的秒杀商品数据的键名
    public static final String SECKILL_GOODS = "SECKILL_GOODS";
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageInfo<TbSeckillGoods> search(Integer pageNum, Integer pageSize, TbSeckillGoods seckillGoods) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbSeckillGoods.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        /**if (StringUtils.isNotBlank(seckillGoods.getProperty())) {
            criteria.andLike("property", "%" + seckillGoods.getProperty() + "%");
        }*/

        List<TbSeckillGoods> list = seckillGoodsMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public List<TbSeckillGoods> findList() {
        List<TbSeckillGoods> seckillGoodsList = null;

        try {
            //从缓存中查询数据
            seckillGoodsList = redisTemplate.boundHashOps(SECKILL_GOODS).values();
            if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                return seckillGoodsList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * -- 在秒杀系统首页加载库存大于0，已经审核，正在秒杀期间的秒杀商品。
         * select * from tb_seckill_goods where stock_count > 0 and `status` = 1
         * and start_time <= NOW() and end_time > NOW() order by start_time asc
         */
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", "1");
        criteria.andGreaterThan("stockCount", 0);
        criteria.andLessThanOrEqualTo("startTime", new Date());
        criteria.andGreaterThan("endTime", new Date());

        example.orderBy("startTime");
        seckillGoodsList = seckillGoodsMapper.selectByExample(example);

        try {
            //将数据存入缓存
            if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
                for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
                    redisTemplate.boundHashOps(SECKILL_GOODS).put(tbSeckillGoods.getId(), tbSeckillGoods);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return seckillGoodsList;
    }

    @Override
    public TbSeckillGoods findOneInRedis(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS).get(id);
    }

}
