package com.pinyougou.pay.service;

import java.util.Map;

public interface PayService {
    /**
     * 调用接口生成预订单；获取支付二维码链接信息
     * @param outTradeNo 交易号
     * @param totalFee 支付总金额
     * @return 操作结果
     */
    Map<String, String> createNative(String outTradeNo, String totalFee);

    /**
     * 查询订单的支付状态
     * @param outTradeNo 交易编号
     * @return 查询结果
     */
    Map<String, String> queryPayStatus(String outTradeNo);

    /**
     * 根据交易号关闭订单
     * @param outTradeNo 交易号
     * @return 操作结果
     */
    Map<String, String> closeOrder(String outTradeNo);
}
