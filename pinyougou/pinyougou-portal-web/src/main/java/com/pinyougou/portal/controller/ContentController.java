package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/content")
@RestController
public class ContentController {

    @Reference
    private ContentService contentService;

    /**
     * 根据内容分类id、状态查询内容数据并且根据排序属性降序排序
     * @param categoryId 内容分类id
     * @return 内容数据
     */
    @GetMapping("/findContentListByCategoryId")
    public List<TbContent> findContentListByCategoryId(Long categoryId){
        return contentService.findContentListByCategoryId(categoryId);
    }
}
