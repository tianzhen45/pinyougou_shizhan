package com.pinyougou.user.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbCollection;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;

import java.util.List;

public interface CollectionService extends BaseService<TbCollection> {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param collection 搜索条件
     * @return 分页信息
     */
    PageInfo<TbCollection> search(Integer pageNum, Integer pageSize, TbCollection collection);

    void collect(String username, String itemId);

    void uncollect(String username, String itemId);

    boolean checkCollect(String username, String itemId);

    List<TbItem> checkCollectItemList(String username, List<TbItem> itemList);
}
