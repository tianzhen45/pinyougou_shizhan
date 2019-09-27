package com.pinyougou.cart.service;

import com.pinyougou.pojo.TbOrderItem;
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

    /**
     * 合并购物车
     * @param cookieCartList 购物车1
     * @param redisCartList 购物车2
     * @return 新购物车列表
     */
    List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList);

    List<Cart> setItemToCartList(List<Cart> cartList, Long itemId, Integer num);

    void saveSelectedItemInRedis(List<TbOrderItem> items, String username);

    List<TbOrderItem> loadSelectedItem(String userId);
}
