package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pay")
@RestController
public class PayController {

    @Reference
    private OrderService orderService;

    @Reference
    private PayService payService;

    /**
     * 获取支付二维码链接等信息
     * @param outTradeNo 交易编号
     * @return 操作结果
     */
    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo){
        //1、根据支付日志id获取支付日志；
        TbPayLog payLog = orderService.findPayLogByOutTradeNo(outTradeNo);
        if (payLog != null) {
            //2、调用支付业务方法；
            String totalFee = payLog.getTotalFee() +"";
            return payService.createNative(outTradeNo, totalFee);
        }
        //3、返回结果
        return new HashMap<>();
    }
}
