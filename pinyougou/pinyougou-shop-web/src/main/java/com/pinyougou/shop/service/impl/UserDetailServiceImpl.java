package com.pinyougou.shop.service.impl;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态认证授权
 */
public class UserDetailServiceImpl implements UserDetailsService {
    /**
     * 根据用户名查询用户信息
     * @param username 用户在前端页面中输入的用户名
     * @return 用户信息
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //角色权限集合
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

        //将用户输入的密码使用加密算法加密之后与第二个参数进行对比；如果一致则认证通过
        return new User(username, "123456", authorities);
    }
}
