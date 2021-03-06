package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtils;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.vo.Cart;
import com.pinyougou.vo.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    //在浏览器中购物车cookie的最大生存时间
    private static final int COOKIE_CART_LIST_MAX_AGE = 3600*24;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference
    private CartService cartService;

    /**
     * 将商品加入到购物车列表并存入对应的媒介
     * @param itemId 商品sku id
     * @param num 购买数量
     * @return 操作结果
     */
    @GetMapping("/addItemToCartList")
    @CrossOrigin(origins = "*", allowCredentials = "true")
    public Result addItemToCartList(Long itemId, Integer num){
        Result result = Result.fail("加入购物车失败！");
        try {
            //解决跨域：设置允许跨域请求的域名
            //response.setHeader("Access-Control-Allow-Origin", "http://item.pinyougou.com");
            //允许携带并接收cookie
            //response.setHeader("Access-Control-Allow-Credentials", "true");

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取当前的购物车
            List<Cart> cartList = findCartList();
            //将商品加入购物车
            List<Cart> newCartList = cartService.addItemToCartList(cartList, itemId, num);
            //存购物车数据
            if ("anonymousUser".equals(username)) {
                //未登录；将购物车存入到cookie
                CookieUtils.setCookie(request, response, COOKIE_CART_LIST,
                        JSON.toJSONString(newCartList), COOKIE_CART_LIST_MAX_AGE, true);
            } else {
                //已登录；将购物车存入到redis
                cartService.saveCartListByUsername(newCartList, username);
            }
            result = Result.ok("加入购物车成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 登录或者未登录情况下
     * 获取购物车列表
     * @return 购物车列表
     */
    @GetMapping("/findCartList")
    public List<Cart> findCartList(){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            //未登录；从cookie中获取购物车数据
            List<Cart> cookieCartList = new ArrayList<>();
            //1、获取cookie中购物车列表字符串
            String cartListJsonStr = CookieUtils.getCookieValue(request, COOKIE_CART_LIST, true);
            //2、转换为对象
            if (StringUtils.isNotBlank(cartListJsonStr)) {
                cookieCartList = JSON.parseArray(cartListJsonStr, Cart.class);
            }

            //如果要严格判断是否登录可以结合用户名和角色进行判断；最好的做法是禁止注册anonymousUser
            if ("anonymousUser".equals(username)) {
                //3、返回
                return cookieCartList;
            } else {
                //已登录；从redis中获取购物车数据
                List<Cart> redisCartList = cartService.findCartListInRedisByUsername(username);
                if (cookieCartList.size() > 0) {
                    //合并
                    redisCartList = cartService.mergeCartList(cookieCartList, redisCartList);
                    //保存
                    cartService.saveCartListByUsername(redisCartList, username);
                    //删除
                    CookieUtils.deleteCookie(request, response, COOKIE_CART_LIST);
                }
                return redisCartList;
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

    @GetMapping("/setItemToCartList")
    @CrossOrigin(origins = "http://item.pinyougou.com", allowCredentials = "true")
    public Result setItemToCartList(Long itemId, Integer num){
        Result result = Result.fail("加入购物车失败！");
        try {
            //解决跨域：设置允许跨域请求的域名
            //response.setHeader("Access-Control-Allow-Origin", "http://item.pinyougou.com");
            //允许携带并接收cookie
            //response.setHeader("Access-Control-Allow-Credentials", "true");

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //获取当前的购物车
            List<Cart> cartList = findCartList();
            //将商品加入购物车
            List<Cart> newCartList = cartService.setItemToCartList(cartList, itemId, num);
            //存购物车数据
            if ("anonymousUser".equals(username)) {
                //未登录；将购物车存入到cookie
                CookieUtils.setCookie(request, response, COOKIE_CART_LIST,
                        JSON.toJSONString(newCartList), COOKIE_CART_LIST_MAX_AGE, true);
            } else {
                //已登录；将购物车存入到redis
                cartService.saveCartListByUsername(newCartList, username);
            }
            result = Result.ok("加入购物车成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @PostMapping("/delBatch")
    public Boolean delBatch(@RequestBody TbOrderItem[] items){
        try {
            if (items != null && items.length > 0) {
                for (TbOrderItem item : items) {
                    setItemToCartList(item.getItemId(), 0);
                }
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    //保存选中的商品到redis中
    @PostMapping("/saveSelectedItem")
    public boolean saveSelectedItem(@RequestBody List<TbOrderItem> items){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            cartService.saveSelectedItemInRedis(items,username);
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    //读取redis中保存的选中的商品
    @GetMapping("/loadSelectedItem")
    public List<TbOrderItem> loadSelectedItem(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return cartService.loadSelectedItem(username);
    }
}
