package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RequestMapping("/seller")
@RestController
public class SellerController{

    //重试0次，超时时间为100秒
    @Reference(retries = 0, timeout = 100000)
    private SellerService sellerService;

    /**
     * 查询
     * @return
     */
    @RequestMapping("/findUser")
    public TbSeller findUser() {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeller seller = sellerService.findOne(sellerId);
        seller.setPassword("");
        return seller;
    }

    /**
     * 修改用户信息
     * @param seller
     * @return
     */
    @PostMapping("/updateUser")
    public Result updateUser(@RequestBody TbSeller seller) {
        Result result = Result.fail("修改失败");
        try {
            //获取用户id(主键)
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            seller.setSellerId(sellerId);
            sellerService.update(seller);
             result = Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 修改密码
     * @param oldpassword   旧密码
     * @param password  新密码
     * @param password1 确定新密码
     * @return  操作结果
     */
    @PostMapping("/UpdatePassword")
    public Result UpdatePassword(String oldpassword,String password,String password1){
        Result result = Result.fail("修改失败");
        try {
            if(oldpassword =="" ){
                result = Result.fail("输入旧密码不能为空");
                return result;
            }
            if(password1 =="" ){
                result = Result.fail("输入新密码不能为空");
                return result;
            }
            if( oldpassword.equals(password)){
                result = Result.fail("旧密码不能与新密码相同");
                return result;
            }
            if (!password.equals(password1) ){
                result = Result.fail("两次输入的密码不一致!");
                return result;
            }

            //获取当前登录的用户名
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();

            TbSeller seller = sellerService.findOne(sellerId);
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //旧加密密码
            String oldmima = seller.getPassword();
            //页面传进来的旧密码与数据库的旧加密密码校验
            boolean flg = passwordEncoder.matches(oldpassword, oldmima);

            if (flg){
                //输入的旧密码正确,设置新密码保存
                //对明文进行加密
                seller.setPassword(passwordEncoder.encode(password1));
                sellerService.update(seller);
                result=Result.ok("修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 新增
     * @param seller 实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbSeller seller){
        try {
            //对明文进行加密
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            seller.setPassword(passwordEncoder.encode(seller.getPassword()));

            seller.setCreateTime(new Date());
            seller.setStatus("0");

            sellerService.add(seller);
            return Result.ok("新增商家成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("新增商家失败");
    }

    /**
     * 根据主键查询
     * @param id 主键
     * @return 实体
     */
    @GetMapping("/findOne/{id}")
    public TbSeller findOne(@PathVariable String id){
        return sellerService.findOne(id);
    }

    /**
     * 修改
     * @param seller 实体
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbSeller seller){
        try {
            if (StringUtils.isEmpty(seller.getPassword())) {
                seller.setPassword(null);
            } else {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                seller.setPassword(passwordEncoder.encode(seller.getPassword()));
            }
            sellerService.update(seller);
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
    public Result delete(String[] ids){
        try {
            sellerService.deleteByIds(ids);
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
     * @param seller 搜索条件
     * @return 分页信息
     */
    @PostMapping("/search")
    public PageInfo<TbSeller> search(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                           @RequestBody TbSeller seller) {
        return sellerService.search(pageNum, pageSize, seller);
    }

}
