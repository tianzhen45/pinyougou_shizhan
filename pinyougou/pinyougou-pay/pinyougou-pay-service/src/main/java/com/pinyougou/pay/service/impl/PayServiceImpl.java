package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.util.HttpClient;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.TbPayLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;

    @Autowired
    private PayLogMapper payLogMapper;

    @Override
    public Map<String, String> createNative(String outTradeNo, String totalFee) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            //1、封装请求参数
            Map<String, String> paramMap = new HashMap<>();
            //公众账号ID
            paramMap.put("appid", appid);
            //商户号
            paramMap.put("mch_id", partner);
            //随机字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //签名；可以在发送的时候；将参数数据统一加密并生成前面
            //paramMap.put("sign", null);
            //商品描述
            paramMap.put("body", "品优购-113");
            //商户订单号
            paramMap.put("out_trade_no", outTradeNo);
            //标价金额
            paramMap.put("total_fee", totalFee);
            //终端IP
            paramMap.put("spbill_create_ip", "127.0.0.1");
            //通知地址
            paramMap.put("notify_url", notifyurl);
            //交易类型
            paramMap.put("trade_type", "NATIVE");
            //2、发送请求
            //将上述的请求参数转换为xml并添加签名
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("调用微信 统一下单 的接口请求数据为：" + signedXml);

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //3、处理结果
            String content = httpClient.getContent();
            System.out.println("调用微信 统一下单 的接口返回数据为：" + content);
            Map<String, String> map = WXPayUtil.xmlToMap(content);

            resultMap.put("outTradeNo", outTradeNo);
            resultMap.put("totalFee", totalFee);
            resultMap.put("result_code", map.get("result_code"));
            resultMap.put("code_url", map.get("code_url"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public Map<String, String> queryPayStatus(String outTradeNo) {
        try {
            //1、封装请求参数
            Map<String, String> paramMap = new HashMap<>();
            //公众账号ID
            paramMap.put("appid", appid);
            //商户号
            paramMap.put("mch_id", partner);
            //随机字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //签名；可以在发送的时候；将参数数据统一加密并生成前面
            //paramMap.put("sign", null);
            //商户订单号
            paramMap.put("out_trade_no", outTradeNo);

            //2、发送请求
            //将上述的请求参数转换为xml并添加签名
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("调用微信 查询订单 的接口请求数据为：" + signedXml);

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //3、处理结果
            String content = httpClient.getContent();
            System.out.println("调用微信 查询订单 的接口返回数据为：" + content);
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, String> closeOrder(String outTradeNo) {
        try {
            //1、封装请求参数
            Map<String, String> paramMap = new HashMap<>();
            //公众账号ID
            paramMap.put("appid", appid);
            //商户号
            paramMap.put("mch_id", partner);
            //随机字符串
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
            //签名；可以在发送的时候；将参数数据统一加密并生成前面
            //paramMap.put("sign", null);
            //商户订单号
            paramMap.put("out_trade_no", outTradeNo);

            //2、发送请求
            //将上述的请求参数转换为xml并添加签名
            String signedXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            System.out.println("调用微信 关闭订单 的接口请求数据为：" + signedXml);

            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(signedXml);
            httpClient.post();

            //3、处理结果
            String content = httpClient.getContent();
            System.out.println("调用微信 关闭订单 的接口返回数据为：" + content);
            return WXPayUtil.xmlToMap(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public String queryOutTradeNo(Long orderId,String userId) {

        //创建查询条件
        Example example = new Example(TbPayLog.class);
        example.createCriteria().andEqualTo("userId",userId);
        List<TbPayLog> payLogList = payLogMapper.selectByExample(example);
        if (payLogList !=null && payLogList.size()>0){
            for (TbPayLog payLog : payLogList) {
                String orderList = payLog.getOrderList();
                if (orderList != null && orderList.length()>0){
                    String[] orderIds = orderList.split(",");
                    for (String id : orderIds) {
                        if (id.equals(orderId+"")){
                            return payLog.getOutTradeNo();
                        }
                    }
                }
            }
        }
        return null;
    }
}
