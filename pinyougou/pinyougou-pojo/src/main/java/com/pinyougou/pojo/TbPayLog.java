package com.pinyougou.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;

@Table(name = "tb_pay_log")
public class TbPayLog implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String outTradeNo;

    private Timestamp createTime;

    private Timestamp payTime;

    private Long totalFee;

    private String userId;

    private String transactionId;

    private String tradeState;

    private String orderList;

    private String payType;

    private static final long serialVersionUID = 1L;

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }


    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }


    public Timestamp getPayTime() {
        return payTime;
    }

    public void setPayTime(Timestamp payTime) {
        this.payTime = payTime;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTradeState() {
        return tradeState;
    }

    public void setTradeState(String tradeState) {
        this.tradeState = tradeState;
    }

    public String getOrderList() {
        return orderList;
    }

    public void setOrderList(String orderList) {
        this.orderList = orderList;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    @Override
    public String toString() {
        return "TbPayLog{" +
                "outTradeNo='" + outTradeNo + '\'' +
                ", createTime=" + createTime +
                ", payTime=" + payTime +
                ", totalFee=" + totalFee +
                ", userId='" + userId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", tradeState='" + tradeState + '\'' +
                ", orderList='" + orderList + '\'' +
                ", payType='" + payType + '\'' +
                '}';
    }
}