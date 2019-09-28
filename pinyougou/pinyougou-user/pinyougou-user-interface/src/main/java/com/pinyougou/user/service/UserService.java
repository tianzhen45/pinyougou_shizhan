package com.pinyougou.user.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.service.BaseService;

import java.util.Map;

public interface UserService extends BaseService<TbUser> {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param user 搜索条件
     * @return 分页信息
     */
    PageInfo<TbUser> search(Integer pageNum, Integer pageSize, TbUser user);

    /**
     * 发送短信验证码
     * @param phone 手机号
     */
    void sendSmsCode(String phone);

    /**
     * 校验用户输入的验证码是否正确
     * @param phone 用户的手机号
     * @param smsCode 用户输入的验证码
     * @return true or false
     */
    Boolean checkSmsCode(String phone, String smsCode);

    /**
     * 分页查询数据库中用户的秒杀订单
     * @return
     */
    Map<String,Object> findUserSeckillOrder(Integer pageNum, Integer pageSize, String userId);

    /**
     * 批量删除用户秒杀订单
     * @param ids
     */
    void deleteSeckillOrder(Long[] ids);
    TbUser findByUserName(String username);
}
