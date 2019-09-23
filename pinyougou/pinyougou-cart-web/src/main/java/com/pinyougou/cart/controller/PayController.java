package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.vo.Result;
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

    /**
     * 查询订单的支付状态
     * @param outTradeNo 交易编号、支付日志id
     * @return 操作结果
     */
    @GetMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo){
        Result result = Result.fail("支付失败！");
        try {
            //在3分钟内未支付则表示超时未支付
            int count = 0;
            while (true) {
                //- 循环调用查询微信支付状态的业务方法
                Map<String, String> map = payService.queryPayStatus(outTradeNo);
                if (map == null) {
                    break;
                }
                //- 如果支付成功则更新支付日志、订单的支付状态为已支付
                if ("SUCCESS".equals(map.get("trade_state"))) {
                    orderService.updateOrderStatus(outTradeNo, map.get("transaction_id"));
                    result = Result.ok("支付成功");
                    break;
                }
                count++;
                if (count > 3) {
                    result = Result.fail("支付超时");
                    break;
                }
                //- 如果还未支付则每隔3秒查询
                Thread.sleep(3000);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
