package com.pinyougou.vo;

import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;

import java.io.Serializable;

/**
 * @author: Lewis
 * @date: 2019/9/26
 * @description:
 */
public class SeckillOrderVo implements Serializable{

    //秒杀订单
    private TbSeckillOrder seckillOrder;
//    liu
    //商家名称
    private String sellerName;
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

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
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
