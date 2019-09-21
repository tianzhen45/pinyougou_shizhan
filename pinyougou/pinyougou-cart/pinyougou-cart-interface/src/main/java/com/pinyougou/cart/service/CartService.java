package com.pinyougou.cart.service;

import com.pinyougou.vo.Cart;

import java.util.List;

public interface CartService {
    /**
     * 将商品加入到购物车列表并存入对应的媒介
     * @param itemId 商品sku id
     * @param num 购买数量
     * @return 新购物车列表
     */
    List<Cart> addItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 到redis中查询用户对应的购物车列表
     * @param username 用户id
     * @return 购物车列表
     */
    List<Cart> findCartListInRedisByUsername(String username);

    /**
     * 保存用户的购物车列表
     * @param cartList 购物车列表
     * @param username 用户ID
     */
    void saveCartListByUsername(List<Cart> cartList, String username);
}
