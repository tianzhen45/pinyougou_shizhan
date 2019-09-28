package com.pinyougou.vo;

import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;

import java.io.Serializable;

/**
 * @author: Lewis
 * @date: 2019/9/27
 * @description:
 */
public class SellerSeckillOrderVo implements Serializable{

    private TbSeckillOrder seckillOrder;
    private TbSeckillGoods seckillGoods;

    public TbSeckillOrder getSeckillOrder() {
        return seckillOrder;
    }

    public void setSeckillOrder(TbSeckillOrder seckillOrder) {
        this.seckillOrder = seckillOrder;
    }

    public TbSeckillGoods getSeckillGoods() {
        return seckillGoods;
    }

    public void setSeckillGoods(TbSeckillGoods seckillGoods) {
        this.seckillGoods = seckillGoods;
    }
}
