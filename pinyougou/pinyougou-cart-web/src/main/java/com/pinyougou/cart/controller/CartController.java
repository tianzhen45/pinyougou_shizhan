package com.pinyougou.cart.controller;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.vo.Cart;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/cart")
@RestController
public class CartController {
    //在浏览器中购物车cookie的名字
    private static final String COOKIE_CART_LIST = "PYG_CART_LIST";
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 登录或者未登录情况下
     * 获取购物车列表
     * @return 购物车列表
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList(){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //如果要严格判断是否登录可以结合用户名和角色进行判断；最好的做法是禁止注册anonymousUser
            if ("anonymousUser".equals(username)) {
                //未登录；从cookie中获取购物车数据
                List<Cart> cookieCartList = null;
                //1、获取cookie中购物车列表字符串
                String cartListJsonStr = CookieUtils.getCookieValue(request, COOKIE_CART_LIST, true);
                //2、转换为对象
                if (StringUtils.isNotBlank(cartListJsonStr)) {
                    cookieCartList = JSON.parseArray(cartListJsonStr, Cart.class);
                }
                //3、返回
                return cookieCartList;
            } else {
                //已登录；从redis中获取购物车数据
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 获取当前登录的用户名
     * 如果是匿名登录则返回的用户名为anonymousUser
     * @return 用户信息
     */
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername(){
        Map<String, Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username", username);
        return map;
    }
}
