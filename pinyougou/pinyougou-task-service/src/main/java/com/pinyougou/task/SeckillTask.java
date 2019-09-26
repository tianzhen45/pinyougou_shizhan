package com.pinyougou.task;

import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillTask {

    @Autowired
    private RedisTemplate redisTemplate;

    //在redis中的秒杀商品数据的键名
    public static final String SECKILL_GOODS = "SECKILL_GOODS";

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    /**
     * 更新秒杀数据
     * -- 查询已经审核，库存大于0， 开始时间小于等于当前时间，结束时间大于当前时间，并不在redis中的商品
     * select * from tb_seckill_goods where status='1' and
     * stock_count>0 and start_time<=? and end_time>? and id not in(...)
     */
    @Scheduled(cron = "0/2 * * * * ?")
    public void refreshSeckillGoods(){
        //1、查询redis中的现有秒杀商品的id集合
        Set set = redisTemplate.boundHashOps(SECKILL_GOODS).keys();
        List ids = new ArrayList<>(set);
        //2、查询数据
        Example example = new Example(TbSeckillGoods.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status", "1")
                .andGreaterThan("stockCount", 0)
                .andLessThanOrEqualTo("startTime", new Date())
                .andGreaterThan("endTime", new Date());
        if (ids != null && ids.size() > 0) {
            criteria.andNotIn("id", ids);
        }
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        //3、逐个遍历数据存入到redis中
        if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
            for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps(SECKILL_GOODS).put(tbSeckillGoods.getId(), tbSeckillGoods);
            }
            System.out.println("缓存了 " + seckillGoodsList.size() + " 个秒杀商品...");
        }
    }
    /**
     * 将在redis中结束时间小于等于当前时间的秒杀商品需要从redis中移除并更新到mysql数据库中。
     */
    @Scheduled(cron = "0/3 * * * * ?")
    public void removeSeckillGoods(){
        //1、获取redis中的秒杀商品列表；
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps(SECKILL_GOODS).values();
        //2、遍历每个秒杀商品，判断其结束时间是否小于当前时间
        if (seckillGoodsList != null && seckillGoodsList.size() > 0) {
            for (TbSeckillGoods tbSeckillGoods : seckillGoodsList) {
                if (tbSeckillGoods.getEndTime().getTime() <= System.currentTimeMillis()) {
                    //2.1、如果结束时间小于当前时间则需要将其同步到mysql，删除redis中商品
                    seckillGoodsMapper.updateByPrimaryKeySelective(tbSeckillGoods);
                    redisTemplate.boundHashOps(SECKILL_GOODS).delete(tbSeckillGoods.getId());
                    System.out.println("从redis中移除了id为：" + tbSeckillGoods.getId() + " 的秒杀商品...");
                }
            }
        }

    }
}
