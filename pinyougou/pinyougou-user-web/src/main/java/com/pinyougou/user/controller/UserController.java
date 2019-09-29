package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.order.service.MyOrderService;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference(timeout = 9999999)
    private UserService userService;

    @PostMapping("/saveUserInfo")
    public Result saveUserInfo(@RequestBody TbUser user){
        Result result = Result.fail("修改失败");
        try {
            userService.update(user);
            result = Result.ok("修改成功");
        }catch (Exception e){
            e.printStackTrace();
        }

        return result;
    }

    @Reference
    private MyOrderService myOrderService;



    /**
     * 取消订单
     * @param orderId 选中的订单id
     * @return
     */
    @GetMapping("/deleteOrder")
    public Result deleteOrder(Long orderId){
        try {
            if (orderId != null) {
                myOrderService.deleteOrder(orderId);
                return Result.ok("取消订单成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("取消订单失败");
    }
    /**
     * 查询所有订单
     * @return
     */
    @GetMapping("/findOrder")
    public Map<String, Object> findOrder(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> resultMap = myOrderService.findOrderByUserId(pageNum, pageSize, userId);
        return resultMap;
    }

    /**
     * 批量删除用户秒杀订单
     * @param ids
     * @return
     */
    @GetMapping("/deleteSeckillOrder")
    public Result deleteSeckillOrder(Long[] ids){
        try {
            userService.deleteSeckillOrder(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 查询用户秒杀订单
     * @param pageNum
     * @param pageSize
     * @return
     */
    @PostMapping("/findUserSeckillOrder")
    public Map<String,Object> search(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "2") Integer pageSize){
        //获取用户id
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findUserSeckillOrder(pageNum,pageSize,userId);
    }



    /**
     * 获取当前登录的用户信息
     * @return 用户信息
     */
    @GetMapping("/getUserInfo")
    public Map<String, Object> getUserInfo(){
        Map<String, Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        TbUser tbUser = userService.findByUserName(username);
        map.put("user", tbUser);
        return map;
    }
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername(){
        Map<String, Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",username);
        return map;
    }

    /**
     * 发送短信验证码
     * @param phone 手机号
     * @return 操作结果
     */
    @GetMapping("/sendSmsCode")
    public Result sendSmsCode(String phone){
        Result result = Result.fail("发送短信验证码失败！");
        try {
            if (PhoneFormatCheckUtils.isPhoneLegal(phone)) {
                userService.sendSmsCode(phone);
                result = Result.ok("发送短信验证码成功！");
            } else {
                result = Result.fail("手机号码格式错误；发送短信验证码失败！");
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 新增
     * @param smsCode 验证码
     * @param user 用户信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(String smsCode, @RequestBody TbUser user){
        try {
            if (userService.checkSmsCode(user.getPhone(), smsCode)) {
                user.setCreated(new Date());
                user.setUpdated(user.getCreated());
                user.setPassword(DigestUtils.md5Hex(user.getPassword()));
                userService.add(user);

                return Result.ok("注册用户成功！");
            } else {
                return Result.fail("验证码错误；注册用户失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("注册用户失败！");
    }

    /**
     * 根据主键查询
     * @param id 主键
     * @return 实体
     */
    @GetMapping("/findOne/{id}")
    public TbUser findOne(@PathVariable Long id){
        return userService.findOne(id);
    }

    /**
     * 修改
     * @param user 实体
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbUser user){
        try {
            userService.update(user);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 根据主键数组批量删除
     * @param ids 主键数组
     * @return 实体
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            userService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param user 搜索条件
     * @return 分页信息
     */
    @PostMapping("/search")
    public PageInfo<TbUser> search(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                   @RequestBody TbUser user) {
        return userService.search(pageNum, pageSize, user);
    }
    @PostMapping("/updatePassword")
    public Result updatePassword(@RequestBody TbUser tbUser){
        Result result = Result.fail("修改失败");
        try{
            tbUser.setPassword(DigestUtils.md5Hex(tbUser.getPassword()));
            userService.update(tbUser);
            result = Result.ok("修改成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    @GetMapping("/checkTwoCode")
    public Result checkTwoCode(String msgCode,String phone){
        Result result = Result.fail("验证码错误！！");
        try {
            userService.checkSmsCode(msgCode,phone);
            result = Result.ok("验证成功！！");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @GetMapping("/bindNewPhoneNumber")
    public Result bindNewPhoneNumber(String msgCode,String phone){
        Result result = Result.fail("验证码错误！！");
        try {
            if (userService.checkSmsCode(msgCode,phone)){
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                TbUser tbUser = userService.findByUserName(username);
                tbUser.setPhone(phone);
                userService.update(tbUser);
                result = Result.ok("验证成功！！");
            }else {
                result = Result.fail("验证码错误！！");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
