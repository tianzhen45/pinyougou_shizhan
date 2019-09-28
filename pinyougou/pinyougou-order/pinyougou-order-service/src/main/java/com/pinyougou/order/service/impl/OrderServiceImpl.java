package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.SeckillOrderVo;
import com.pinyougou.vo.SellerSeckillOrderVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.math.BigDecimal;
import java.util.*;

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

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    //品优购购物车在redis中的键名
    private static final String CART_LIST = "CART_LIST";

    //用户选中的购物车在redis中的键名
    private static final String SELECTED_CART_LIST = "SELECTED_CART_LIST";

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
        //将选中的所有商品从redis中取出
        List<TbOrderItem> items = (List<TbOrderItem>) redisTemplate.boundHashOps(SELECTED_CART_LIST).get(order.getUserId());
        if (items == null) {
            throw new RuntimeException("没有选中商品，无法结算");
        }
        //将orderItem按商家id分组: {sellerId:[orderItems...], sellerId:[orderItems...]}
        Map<String, List<TbOrderItem>> orderItemMap = this.groupTbOrderItemBySeller(items);

        String orderIds = "";
        String outTradeNo = "";
        Double totalFee = 0.0;

        //保存订单,每个商家一个订单
        for (Map.Entry<String, List<TbOrderItem>> orderItemEntry : orderItemMap.entrySet()) {
            String sellerId = orderItemEntry.getKey();


            // /生成主键id
            long orderId = idWorker.nextId();
            //创建一个订单
            TbOrder order1 = new TbOrder();
            //设置订单id
            order1.setOrderId(String.valueOf(orderId));
            //设置订单状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
            order1.setStatus("1");
            //设置支付类型
            order1.setPaymentType(order.getPaymentType());
            //设置发票类型
            order1.setInvoiceType(order.getInvoiceType());
            //设置订单创建时间
            order1.setCreateTime(new Date());
            //设置订单修改时间
            order1.setUpdateTime(order1.getCreateTime());
            //设置用户名
            order1.setUserId(order.getUserId());
            //设置收件人
            order1.setReceiver(order.getReceiver());
            //设置收件人手机号
            order1.setReceiverMobile(order.getReceiverMobile());
            //设置收件人地址
            order1.setReceiverAreaName(order.getReceiverAreaName());
            //设置订单来源
            order1.setSourceType(order.getSourceType());
            //设置商家id
            order1.setSellerId(sellerId);
            //定义订单总金额
            double money = 0.0;

            List<TbOrderItem> orderItemList = orderItemEntry.getValue();
            for (TbOrderItem orderItem : orderItemList) {
                money += orderItem.getTotalFee();
                //给订单详情设置主键
                orderItem.setId(idWorker.nextId());
                //给每个订单详情设置订单id,保存到数据库
                orderItem.setOrderId(String.valueOf(orderId));
                orderItemMapper.insert(orderItem);
            }

            //设置订单总金额
            order1.setPayment(money);

            //累加所有订单的总金额之和
            totalFee += order1.getPayment();

            //保存订单到数据库
            orderMapper.insertSelective(order1);

            if (orderIds.length() > 0) {
                    orderIds += "," + orderId;
                } else {
                    orderIds = orderId+"";
            }
        }



//        String outTradeNo = "";
//        //1、查询当前登录用户的购物车列表cartList
//        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(CART_LIST).get(order.getUserId());
//        if(cartList != null && cartList.size() > 0) {
//            //2、遍历购物车列表，每个购物车cart就是一个订单并保存
//            String orderId = "";
//            //订单总金额 = 订单商品的总金额之和
//            double payment = 0.0;
//            //支付总金额 = 所有订单总金额之和
//            double totalFee = 0.0;
//            //所有订单id字符串
//            String orderIds = "";
//            for (Cart cart : cartList) {
//                payment = 0.0;
//                TbOrder tbOrder = new TbOrder();
//                //订单号
//                orderId = idWorker.nextId()+"";
//                tbOrder.setOrderId(orderId);
//                tbOrder.setUserId(order.getUserId());
//                tbOrder.setReceiverMobile(order.getReceiverMobile());
//                tbOrder.setReceiver(order.getReceiver());
//                tbOrder.setReceiverAreaName(order.getReceiverAreaName());
//                tbOrder.setSourceType(order.getSourceType());
//                tbOrder.setCreateTime(new Date());
//                tbOrder.setUpdateTime(tbOrder.getCreateTime());
//                //未支付
//                tbOrder.setStatus("1");
//                tbOrder.setSellerId(cart.getSellerId());
//                tbOrder.setPaymentType(order.getPaymentType());
//                //2.1、遍历每个cart中的订单商品列表并保存
//                for (TbOrderItem orderItem : cart.getOrderItemList()) {
//                    orderItem.setId(idWorker.nextId());
//                    orderItem.setOrderId(orderId);
//                    orderItemMapper.insertSelective(orderItem);
//                    //累加订单总金额
//                    payment += orderItem.getTotalFee();
//                }
//                //订单总金额 = 这笔订单中的所有商品的总金额之和
//                tbOrder.setPayment(payment);
//
//                //累加所有订单的总金额之和
//                totalFee += tbOrder.getPayment();
//
//                add(tbOrder);
//
//                if (orderIds.length() > 0) {
//                    orderIds += "," + orderId;
//                } else {
//                    orderIds = orderId;
//                }
//            }


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

//            //4、删除redis中购物车列表
//            redisTemplate.boundHashOps(CART_LIST).delete(order.getUserId());

        //最后删除redis购物车中被订单提交了的商品
        List<Cart> cartList = this.findCartRedis(order.getUserId());
//        for (Cart cart : cartList) {
//            String sellerId = cart.getSellerId();
//
//            //判断是否有该商家的商品在订单中
//            if(orderItemMap.containsKey(sellerId)){
//                List<TbOrderItem> orderItems1 = orderItemMap.get(sellerId);
//                List<TbOrderItem> cartOrderItems = cart.getOrderItemList();
//                for (TbOrderItem item : orderItems1) {
//                    //重写了OrderItem的equals方法，itemId和name相同就相同
//                    cartOrderItems.remove(item);
//                }
//            }
//
//            if(cart.getOrderItemList().size() == 0){
//                cartList.remove(cart);
//            }
//        }
        Iterator<Cart> iterator = cartList.iterator();
        while(iterator.hasNext()){
            Cart cart = iterator.next();
            String sellerId = cart.getSellerId();

            //判断是否有该商家的商品在订单中
            if(orderItemMap.containsKey(sellerId)){
                List<TbOrderItem> orderItems1 = orderItemMap.get(sellerId);
                List<TbOrderItem> cartOrderItems = cart.getOrderItemList();
                for (TbOrderItem item : orderItems1) {
                    //重写了OrderItem的equals方法，itemId和name相同就相同
                    cartOrderItems.remove(item);
                }
            }

            // 当商家购物车下面没有商品时，删除该商家购物车
            if(cart.getOrderItemList().size() == 0){
                iterator.remove();
            }
        }


        //保存回到redis中
        this.saveCartRedis(order.getUserId(),cartList);



        //5、返回支付日志id
        return outTradeNo;
    }

    @Override
    public TbPayLog findPayLogByOutTradeNo(String outTradeNo) {
        return payLogMapper.selectByPrimaryKey(outTradeNo);
    }

    @Override
    public void updateOrderStatus(String outTradeNo, String transactionId) {
        //在支付成功之后需要更新支付日志（1已支付）、订单（2已支付）的状态为已支付
        //1、查询支付日志
        TbPayLog payLog = findPayLogByOutTradeNo(outTradeNo);
        //2、更新支付日志的支付状态
        payLog.setPayTime(new Date());
        payLog.setTradeState("1");
        payLog.setTransactionId(transactionId);
        payLogMapper.updateByPrimaryKeySelective(payLog);

        //3、更新该支付日志对应的所以订单的支付状态为已支付2
        //update tb_order set status=2 where order_id in(?,?,...)
        String[] orderIds = payLog.getOrderList().split(",");
        TbOrder order = new TbOrder();
        order.setPaymentTime(new Date());
        order.setStatus("2");
        order.setUpdateTime(new Date());

        //创建更新的条件对象
        Example example = new Example(TbOrder.class);
        example.createCriteria()
                .andIn("orderId", Arrays.asList(orderIds));
        orderMapper.updateByExampleSelective(order, example);
    }


    //将orderItem按商家id分组
    public Map<String,List<TbOrderItem>> groupTbOrderItemBySeller(List<TbOrderItem> orderItems){
        Map<String,List<TbOrderItem>> result = new HashMap<>();
        for (TbOrderItem orderItem : orderItems) {
            String sellerId = orderItem.getSellerId();
            if(!result.containsKey(sellerId)){
                result.put(sellerId,new ArrayList<TbOrderItem>());
            }
            List<TbOrderItem> orderItems1 = result.get(sellerId);
            orderItems1.add(orderItem);
        }
        return result;
    }






    @Override
    public PageInfo<TbOrder> findOrderList(Integer pageNum, Integer pageSize, TbOrder order) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbOrder.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(order.getStatus())) {

            criteria.andEqualTo("status", order.getStatus());

        }
        if (StringUtils.isNotBlank(order.getSellerId())) {

            criteria.andEqualTo("sellerId", order.getSellerId());
        }

        //模糊查询
        /**if (StringUtils.isNotBlank(user.getProperty())) {
         criteria.andLike("property", "%" + user.getProperty() + "%");
         }*/

        List<TbOrder> list = orderMapper.selectByExample(example);
        return new PageInfo<>(list);

    }

    @Override
    public void updateStatus(TbOrder order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public Map<String, Object> findSeckillOrderList(Integer pageNum, Integer pageSize, TbSeckillOrder order) {
        Map<String, Object> resultMap = new HashMap<>();
        //select * from tb_seckill_order where user_id = 'heima' ORDER BY create_time DESC
        //设置分页信息
        PageHelper.startPage(pageNum, pageSize);
        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(order.getSellerId())) {

            criteria.andEqualTo("sellerId", order.getSellerId());
        }
        //设置排序
        example.orderBy("createTime").desc();
        List<TbSeckillOrder> seckillOrderList = seckillOrderMapper.selectByExample(example);
        if (seckillOrderList == null || seckillOrderList.size() == 0) {
            return new HashMap<>();
        }
        PageInfo<TbSeckillOrder> pageInfo = PageInfo.of(seckillOrderList);

        List<SellerSeckillOrderVo> list = new ArrayList<>();
        for (TbSeckillOrder tbSeckillOrder : seckillOrderList) {

            SellerSeckillOrderVo SeckillOrderVo = new SellerSeckillOrderVo();

            SeckillOrderVo.setSeckillOrder(tbSeckillOrder);
            //2.根据秒杀商品id查询秒杀商品
            TbSeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(tbSeckillOrder.getSeckillId());
            if (seckillGoods == null) {
                return new HashMap<>();
            }
            SeckillOrderVo.setSeckillGoods(seckillGoods);
            list.add(SeckillOrderVo);
        }

        //vo列表
        resultMap.put("seckillOrderList", list);
        //当前页
        resultMap.put("pageNum", pageInfo.getPageNum());
        //总页数
        resultMap.put("total", pageInfo.getTotal());
        return resultMap;
    }

    @Override
    public void deleteSeckillOrder(Long[] ids) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                seckillOrderMapper.deleteByPrimaryKey(id.toString());
            }
        }
    }

    @Override
    public void updatePayment(TbOrder order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }





    public List<Cart> findCartRedis(String username) {
        List<Cart> carts = (List<Cart>) redisTemplate.boundHashOps(CART_LIST).get(username);
        if(carts == null){
            carts = new ArrayList<Cart>(0);
        }
        return carts;
    }

    public void saveCartRedis(String username,List<Cart> cartList) {
        redisTemplate.boundHashOps(CART_LIST).put(username,cartList);
    }


}
