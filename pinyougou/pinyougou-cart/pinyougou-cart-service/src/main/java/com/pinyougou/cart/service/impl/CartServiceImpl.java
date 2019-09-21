package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.vo.Cart;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Override
    public List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1、商品对应的商家（cart）不存在；创建一个购物车Cart，然后往其里面的订单商品列表添加一个订单商品，将该cart加入到原来的购物车列表cartList
        //
        //2、商品对应的商家（cart）存在
        //
        //2.1、在该商家（cart）中订单商品是不存在；创建一个订单商品对象，然后将该订单商品添加到购物车对应的订单商品列表中（orderItemList）
        //
        //2.2、在该商家（cart）中订单商品是存在
        //
        //2.2.1、 将订单商品的购买数量与当前的购买数量进行叠加；重新计算总结
        //
        //2.2.2、订单商品的购买数量小于1的时候；则需要将该订单商品从当前的购物车cart的订单商品列表中删除
        //
        //2.2.3、在上述删除之后，如果购物车cart的订单商品列表大小为0的时候；则需要将该购物车cart从购物车列表cartList中删除
        return cartList;
    }
}
