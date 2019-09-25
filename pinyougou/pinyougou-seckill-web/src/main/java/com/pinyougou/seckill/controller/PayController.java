package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
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
    private SeckillOrderService orderService;

    @Reference
    private PayService payService;

    /**
     * 获取支付二维码链接等信息
     * @param outTradeNo 交易编号
     * @return 操作结果
     */
    @GetMapping("/createNative")
    public Map<String, String> createNative(String outTradeNo){
        //1、根据订单号查询redis中秒杀订单；
        TbSeckillOrder order = orderService.findSeckillOrderByOutTradeNo(outTradeNo);
        if (order != null) {
            //2、调用支付业务方法；
            String totalFee = (long)(order.getMoney()*100) +"";
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
            //在1分钟内未支付则表示超时未支付
            int count = 0;
            while (true) {
                //- 循环调用查询微信支付状态的业务方法
                Map<String, String> map = payService.queryPayStatus(outTradeNo);
                if (map == null) {
                    break;
                }
                //- 如果支付成功则更新支付日志、订单的支付状态为已支付
                if ("SUCCESS".equals(map.get("trade_state"))) {
                    orderService.saveSeckillOrderInRedisToDb(outTradeNo, map.get("transaction_id"));
                    result = Result.ok("支付成功");
                    break;
                }
                count++;
                if (count > 20) {
                    //关闭订单
                    Map<String, String> resultMap = payService.closeOrder(outTradeNo);
                    if (resultMap != null && "ORDERPAID".equals(resultMap.get("err_code"))) {
                        //如果关闭失败，在关闭时候被支付；要像正常支付一样保存订单
                        orderService.saveSeckillOrderInRedisToDb(outTradeNo, map.get("transaction_id"));
                        result = Result.ok("支付成功");
                        break;
                    }
                    //如果关闭成功则删除redis中订单
                    orderService.deleteSeckillOrderInRedis(outTradeNo);

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
