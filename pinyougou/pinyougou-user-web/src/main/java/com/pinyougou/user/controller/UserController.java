package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference(timeout = 10000)
    private UserService userService;

    /**
     * 新增
     * @param smsCode 验证码
     * @param user 用户信息
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(String smsCode, @RequestBody TbUser user){
        try {
            user.setCreated(new Date());
            user.setUpdated(user.getCreated());
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
            userService.add(user);

            return Result.ok("注册用户成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("注册用户失败");
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

}
