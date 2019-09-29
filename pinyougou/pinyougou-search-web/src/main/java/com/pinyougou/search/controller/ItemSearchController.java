package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.user.service.CollectionService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/itemSearch")
@RestController
public class ItemSearchController {

    @Reference
    private ItemSearchService itemSearchService;

    @Reference
    private CollectionService collectionService;

    /**
     * 根据搜索条件搜索es中的商品数据
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    @PostMapping("/search")
    public Map<String, Object> search(@RequestBody Map<String, Object> searchMap){
        Map<String, Object> map = itemSearchService.search(searchMap);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!"anonymousUser".equals(username)){
            List<TbItem> itemList = (List<TbItem>) map.get("itemList");
            itemList = collectionService.checkCollectItemList(username,itemList);
            map.put("itemList",itemList);
        }

        return map;
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
