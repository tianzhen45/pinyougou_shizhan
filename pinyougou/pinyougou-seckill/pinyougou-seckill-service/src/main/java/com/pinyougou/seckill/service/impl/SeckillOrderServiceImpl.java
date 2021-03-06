package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.common.util.RedisLock;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class SeckillOrderServiceImpl extends BaseServiceImpl<TbSeckillOrder> implements SeckillOrderService {

    // 秒杀订单在redis中的键名
    private static final String SECKILL_ORDERS = "SECKILL_ORDERS";
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public PageInfo<TbSeckillOrder> search(Integer pageNum, Integer pageSize, TbSeckillOrder seckillOrder) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbSeckillOrder.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        /**if (StringUtils.isNotBlank(seckillOrder.getProperty())) {
            criteria.andLike("property", "%" + seckillOrder.getProperty() + "%");
        }*/

        List<TbSeckillOrder> list = seckillOrderMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public String submitOrder(Long seckillGoodsId, String userId) throws Exception {
        String orderId = "";
        //添加分布式锁
        RedisLock redisLock = new RedisLock(redisTemplate);
        if(redisLock.lock(seckillGoodsId.toString())) {
            //1、查询秒杀商品；判断商品是否存在，库存>0
            TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).get(seckillGoodsId);
            if(tbSeckillGoods == null ){
                throw new RuntimeException("秒杀商品不存在！");
            }
            if (tbSeckillGoods.getStockCount() == 0) {
                throw new RuntimeException("商品售罄！");
            }
            //2、递减库存；
            tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()-1);
            //2.1、如果库存为0则需要更新回mysql，删除redis中该商品
            if (tbSeckillGoods.getStockCount() > 0) {
                //更新redis中的库存
                redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).put(seckillGoodsId, tbSeckillGoods);
            } else {
                //库存为0则需要更新回mysql，删除redis中该商品
                seckillGoodsMapper.updateByPrimaryKeySelective(tbSeckillGoods);
                redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).delete(seckillGoodsId);
            }
            //释放分布式锁
            redisLock.unlock(seckillGoodsId.toString());

            //3、创建订单并保存到redis中
            TbSeckillOrder seckillOrder = new TbSeckillOrder();
            orderId = idWorker.nextId()+"";
            seckillOrder.setId(orderId);
            //未支付
            seckillOrder.setSeckillId(seckillGoodsId);
            seckillOrder.setStatus("0");
            seckillOrder.setUserId(userId);
            seckillOrder.setSellerId(tbSeckillGoods.getSellerId());
            seckillOrder.setMoney(tbSeckillGoods.getCostPrice());
            seckillOrder.setCreateTime(new Date());

            redisTemplate.boundHashOps(SECKILL_ORDERS).put(orderId, seckillOrder);
        }
        //4、返回订单id
        return orderId;
    }

    @Override
    public TbSeckillOrder findSeckillOrderByOutTradeNo(String outTradeNo) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(SECKILL_ORDERS).get(outTradeNo);
    }

    @Override
    public void saveSeckillOrderInRedisToDb(String outTradeNo, String transactionId) {
        //1、查询订单
        TbSeckillOrder seckillOrder = findSeckillOrderByOutTradeNo(outTradeNo);
        //2、更新订单数据
        seckillOrder.setPayTime(new Date());
        seckillOrder.setStatus("1");
        seckillOrder.setTransactionId(transactionId);
        //3、保存订单到数据库
        seckillOrderMapper.insertSelective(seckillOrder);
        //4、删除redis中订单
        redisTemplate.boundHashOps(SECKILL_ORDERS).delete(outTradeNo);
    }

    @Override
    public void deleteSeckillOrderInRedis(String outTradeNo) throws Exception {
        //1、查询redis中的订单；
        TbSeckillOrder seckillOrder = findSeckillOrderByOutTradeNo(outTradeNo);
        //添加分布式锁
        RedisLock redisLock = new RedisLock(redisTemplate);
        if (redisLock.lock(seckillOrder.getSeckillId().toString())) {
            //2、查询在redis中的秒杀商品；如果不存在则从mysql中查询
            TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).get(seckillOrder.getSeckillId());
            if (tbSeckillGoods == null) {
                tbSeckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getSeckillId());
            }
            //3、秒杀商品库存加1
            tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()+1);
            //4、更新秒杀商品到redis
            redisTemplate.boundHashOps(SeckillGoodsServiceImpl.SECKILL_GOODS).put(tbSeckillGoods.getId(), tbSeckillGoods);

            //释放分布式锁
            redisLock.unlock(seckillOrder.getSeckillId().toString());
            //5、删除redis中的订单
            redisTemplate.boundHashOps(SECKILL_ORDERS).delete(outTradeNo);
        }
    }

}
