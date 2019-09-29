package com.pinyougou.vo;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;

import java.io.Serializable;
import java.util.List;

public class MyOrderVo implements Serializable {

    //商家id
    private String sellerId;
    //支付日志列表
    private  TbPayLog payLog;
    //商家名称
    private String sellerName;
    //订单
    private List<TbOrder> orderList;
    //订单商品列表
    private List<TbOrderItem> orderItemList;

    //规格spec
    private String specStr;



    public String getSpecStr() {
        return specStr;
    }

    public void setSpecStr(String specStr) {
        this.specStr = specStr;
    }

    /*public List<String> getSpecList() {
        return specList;
    }

    public void setSpecList(List<String> specList) {
        this.specList = specList;
    }*/


    public List<TbOrder> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<TbOrder> orderList) {
        this.orderList = orderList;
    }

    public TbPayLog getPayLog() {
        return payLog;
    }

    public void setPayLog(TbPayLog payLog) {
        this.payLog = payLog;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<TbOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<TbOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }

}
