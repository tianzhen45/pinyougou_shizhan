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
}
