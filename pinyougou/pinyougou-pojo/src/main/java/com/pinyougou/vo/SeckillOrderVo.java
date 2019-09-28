package com.pinyougou.vo;

import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.pojo.TbSeller;

import java.io.Serializable;

/**
 * 用户秒杀订单vo
 */
public class SeckillOrderVo implements Serializable{

    //秒杀订单
    private TbSeckillOrder seckillOrder;
//    liu
    //商家名称
    private TbSeller seller;
    //秒杀商品（为了显示标题，原价，秒杀价）
    private TbSeckillGoods tbSeckillGoods;
    //原商品sku规格字符串（为了显示规格）
    private String specStr;

    public TbSeckillOrder getSeckillOrder() {
        return seckillOrder;
    }

    public void setSeckillOrder(TbSeckillOrder seckillOrder) {
        this.seckillOrder = seckillOrder;
    }

    public TbSeller getSeller() {
        return seller;
    }

    public void setSeller(TbSeller seller) {
        this.seller = seller;
    }

    public TbSeckillGoods getTbSeckillGoods() {
        return tbSeckillGoods;
    }

    public void setTbSeckillGoods(TbSeckillGoods tbSeckillGoods) {
        this.tbSeckillGoods = tbSeckillGoods;
    }

    public String getSpecStr() {
        return specStr;
    }

    public void setSpecStr(String specStr) {
        this.specStr = specStr;
    }
}
