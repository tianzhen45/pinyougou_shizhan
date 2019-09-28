package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    //品优购购物车在redis中的键名
    private static final String CART_LIST = "CART_LIST";

    //用户选中的购物车在redis中的键名
    private static final String SELECTED_CART_LIST = "SELECTED_CART_LIST";

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("商品非法");
        }
        Cart cart = findCartInCartListBySellerId(cartList, item.getSellerId());
        if(cart == null) {
            //1、商品对应的商家（cart）不存在；创建一个购物车Cart，然后往其里面的订单商品列表添加一个订单商品，将该cart加入到原来的购物车列表cartList
            if (num > 0) {
                cart = new Cart();
                cart.setSellerId(item.getSellerId());
                cart.setSellerName(item.getSeller());

                List<TbOrderItem> orderItemList = new ArrayList<>();
                TbOrderItem orderItem = createOrderItem(item, num);
                orderItemList.add(orderItem);
                cart.setOrderItemList(orderItemList);
                cartList.add(cart);
            } else {
                throw new RuntimeException("购买数量非法！");
            }
        } else {
            //2、商品对应的商家（cart）存在
            TbOrderItem orderItem = findOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(orderItem == null) {
                //2.1、在该商家（cart）中订单商品是不存在；创建一个订单商品对象，然后将该订单商品添加到购物车对应的订单商品列表中（orderItemList）
                if (num > 0) {
                    orderItem = createOrderItem(item, num);
                    cart.getOrderItemList().add(orderItem);
                } else {
                    throw new RuntimeException("购买数量非法！");
                }
            } else {
                //2.2、在该商家（cart）中订单商品是存在
                //2.2.1、 将订单商品的购买数量与当前的购买数量进行叠加；重新计算总结
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(orderItem.getNum() * orderItem.getPrice());

                if(orderItem.getNum() < 1) {
                    //2.2.2、订单商品的购买数量小于1的时候；则需要将该订单商品从当前的购物车cart的订单商品列表中删除
                    cart.getOrderItemList().remove(orderItem);
                }
                //2.2.3、在上述删除之后，如果购物车cart的订单商品列表大小为0的时候；则需要将该购物车cart从购物车列表cartList中删除
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    /**
     *
     * @param cartList
     * @param itemId
     * @param num
     *
     * @return
     */
    @Override
    public List<Cart> setItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("商品非法");
        }
        Cart cart = findCartInCartListBySellerId(cartList, item.getSellerId());
        if(cart == null) {
            //1、商品对应的商家（cart）不存在；创建一个购物车Cart，然后往其里面的订单商品列表添加一个订单商品，将该cart加入到原来的购物车列表cartList
            if (num > 0) {
                cart = new Cart();
                cart.setSellerId(item.getSellerId());
                cart.setSellerName(item.getSeller());

                List<TbOrderItem> orderItemList = new ArrayList<>();
                TbOrderItem orderItem = createOrderItem(item, num);
                orderItemList.add(orderItem);
                cart.setOrderItemList(orderItemList);
                cartList.add(cart);
            } else {
                throw new RuntimeException("购买数量非法！");
            }
        } else {
            //2、商品对应的商家（cart）存在
            TbOrderItem orderItem = findOrderItemByItemId(cart.getOrderItemList(), itemId);
            if(orderItem == null) {
                //2.1、在该商家（cart）中订单商品是不存在；创建一个订单商品对象，然后将该订单商品添加到购物车对应的订单商品列表中（orderItemList）
                if (num > 0) {
                    orderItem = createOrderItem(item, num);
                    cart.getOrderItemList().add(orderItem);
                } else {
                    throw new RuntimeException("购买数量非法！");
                }
            } else {
                //2.2、在该商家（cart）中订单商品是存在
                //2.2.1、 将订单商品的购买数量与当前的购买数量进行叠加；重新计算总结

                /***/
                orderItem.setNum(num);

                orderItem.setTotalFee(orderItem.getNum() * orderItem.getPrice());

                if(orderItem.getNum() < 1) {
                    //2.2.2、订单商品的购买数量小于1的时候；则需要将该订单商品从当前的购物车cart的订单商品列表中删除
                    cart.getOrderItemList().remove(orderItem);
                }
                //2.2.3、在上述删除之后，如果购物车cart的订单商品列表大小为0的时候；则需要将该购物车cart从购物车列表cartList中删除
                if (cart.getOrderItemList().size() == 0) {
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    @Override
    public void saveSelectedItemInRedis(List<TbOrderItem> items, String username) {
        redisTemplate.boundHashOps(SELECTED_CART_LIST).put(username,items);
    }

    @Override
    public List<TbOrderItem> loadSelectedItem(String userId) {
        List<TbOrderItem> items = (List<TbOrderItem>) redisTemplate.boundHashOps(SELECTED_CART_LIST).get(userId);
        if (items != null) {
            return items;
        }
        return new ArrayList<>();
    }

    @Override
    public List<Cart> findCartListInRedisByUsername(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(CART_LIST).get(username);
        if (cartList != null) {
            return cartList;
        }
        return new ArrayList<>();
    }

    @Override
    public void saveCartListByUsername(List<Cart> cartList, String username) {
        redisTemplate.boundHashOps(CART_LIST).put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        for (Cart cart : cookieCartList) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                addItemToCartList(redisCartList, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCartList;
    }

    /**
     * 根据商品id查询订单商品
     * @param orderItemList 订单商品列表
     * @param itemId 商品sku id
     * @return 订单商品
     */
    private TbOrderItem findOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        if (orderItemList != null && orderItemList.size() > 0) {
            for (TbOrderItem orderItem : orderItemList) {
                if (itemId.equals(orderItem.getItemId())) {
                    return orderItem;
                }
            }
        }
        return null;
    }

    /**
     * 创建订单商品对象
     * @param item 商品sku
     * @param num 购买数量
     * @return 订单商品
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setSellerId(item.getSellerId());
        orderItem.setNum(num);
        orderItem.setPrice(item.getPrice());
        orderItem.setTitle(item.getTitle());
        orderItem.setPicPath(item.getImage());
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        //总价 = 单价*数量
        orderItem.setTotalFee(orderItem.getPrice()*orderItem.getNum());
        return orderItem;
    }

    /**
     * 根据商家id查询购物车对象（cart）
     * @param cartList 购物车列表
     * @param sellerId 商家id
     * @return cart
     */
    private Cart findCartInCartListBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null && cartList.size() > 0) {
            for (Cart cart : cartList) {
                if (sellerId.equals(cart.getSellerId())) {
                    return cart;
                }
            }
        }
        return null;
    }
}
