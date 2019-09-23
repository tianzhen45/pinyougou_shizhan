package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pay.service.PayService;

import java.util.Map;

@Service
public class PayServiceImpl implements PayService {
    @Override
    public Map<String, String> createNative(String outTradeNo, String totalFee) {
        return null;
    }
}
