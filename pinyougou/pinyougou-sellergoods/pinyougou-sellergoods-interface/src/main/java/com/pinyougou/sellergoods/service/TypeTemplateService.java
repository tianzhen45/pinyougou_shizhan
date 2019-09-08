package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.service.BaseService;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService extends BaseService<TbTypeTemplate> {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param typeTemplate 搜索条件
     * @return 分页信息
     */
    PageInfo<TbTypeTemplate> search(Integer pageNum, Integer pageSize, TbTypeTemplate typeTemplate);

    /**
     * 根据分类模板id查询分类模板对应的规格及其选项
     * @param id 分类模板id
     * @return 规格及其选项列表
     */
    List<Map> findSpecList(Long id);
}
