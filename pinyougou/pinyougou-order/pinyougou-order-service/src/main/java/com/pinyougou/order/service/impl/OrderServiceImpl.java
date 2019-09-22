package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Cart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends BaseServiceImpl<TbOrder> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    //品优购购物车在redis中的键名
    private static final String CART_LIST = "CART_LIST";

    @Override
    public PageInfo<TbOrder> search(Integer pageNum, Integer pageSize, TbOrder order) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbOrder.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        /**if (StringUtils.isNotBlank(order.getProperty())) {
            criteria.andLike("property", "%" + order.getProperty() + "%");
        }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public String addOrder(TbOrder order) {
        String outTradeNo = "";
        //1、查询当前登录用户的购物车列表cartList
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(CART_LIST).get(order.getUserId());
        if(cartList != null && cartList.size() > 0) {
            //2、遍历购物车列表，每个购物车cart就是一个订单并保存
            String orderId = "";
            //订单总金额 = 订单商品的总金额之和
            double payment = 0.0;
            //支付总金额 = 所有订单总金额之和
            double totalFee = 0.0;
            //所有订单id字符串
            String orderIds = "";
            for (Cart cart : cartList) {
                payment = 0.0;
                TbOrder tbOrder = new TbOrder();
                //订单号
                orderId = idWorker.nextId()+"";
                tbOrder.setOrderId(orderId);
                tbOrder.setUserId(order.getUserId());
                tbOrder.setReceiverMobile(order.getReceiverMobile());
                tbOrder.setReceiver(order.getReceiver());
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());
                tbOrder.setSourceType(order.getSourceType());
                tbOrder.setCreateTime(new Date());
                tbOrder.setUpdateTime(tbOrder.getCreateTime());
                //未支付
                tbOrder.setStatus("1");
                tbOrder.setSellerId(cart.getSellerId());
                tbOrder.setPaymentType(order.getPaymentType());
                //2.1、遍历每个cart中的订单商品列表并保存
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(orderId);
                    orderItemMapper.insertSelective(orderItem);
                    //累加订单总金额
                    payment += orderItem.getTotalFee();
                }
                //订单总金额 = 这笔订单中的所有商品的总金额之和
                tbOrder.setPayment(payment);

                //累加所有订单的总金额之和
                totalFee += tbOrder.getPayment();

                add(tbOrder);

                if (orderIds.length() > 0) {
                    orderIds += "," + orderId;
                } else {
                    orderIds = orderId;
                }
            }

            //3、保存支付日志；如果是微信付款则支付状态为未支付（0），如果货到付款则支付状态为已支付（1）
            TbPayLog payLog = new TbPayLog();
            outTradeNo = idWorker.nextId()+"";
            payLog.setOutTradeNo(outTradeNo);

            payLog.setUserId(order.getUserId());
            payLog.setCreateTime(new Date());
            //本次交易要支付的总金额 = 每一笔订单的总金额；一般在电商中价格都是长整型；单位精确到分
            payLog.setTotalFee((long)(totalFee*100));
            //本次交易对应的所有订单，使用,分隔
            payLog.setOrderList(orderIds);
            if ("1".equals(order.getPaymentType())) {
                //微信付款；支付状态为 未支付
                //未支付 0
                payLog.setTradeState("0");
            } else {
                //货到付款；支付状态为 已支付
                //未支付 1
                payLog.setTradeState("1");
            }
            payLogMapper.insertSelective(payLog);

            //4、删除redis中购物车列表
            redisTemplate.boundHashOps(CART_LIST).delete(order.getUserId());
        }
        //5、返回支付日志id
        return outTradeNo;
    }

}
