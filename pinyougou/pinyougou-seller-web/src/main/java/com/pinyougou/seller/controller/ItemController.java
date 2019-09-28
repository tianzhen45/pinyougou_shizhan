package com.pinyougou.seller.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.search.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/item")
@RestController
public class ItemController {



    @Reference
    private ItemService itemService;

    /**
     * 根据搜索条件搜索es中的商品数据
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
   @PostMapping("/search")
   @CrossOrigin(origins = "*", allowCredentials = "true")
   public Map<String, Object> search(@RequestBody Map<String, Object> searchMap){
       return itemService.search(searchMap);
    }

}
