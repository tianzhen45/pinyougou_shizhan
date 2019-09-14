package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {
    /**
     * 根据搜索条件搜索es中的商品数据
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    Map<String, Object> search(Map<String, Object> searchMap);

    /**
     * 批量更新es中的商品数据
     * @param itemList 商品列表
     */
    void importItemList(List<TbItem> itemList);

    /**
     * 根据商品spu id数组删除在es中的sku
     * @param goodsIds spu id数组
     */
    void deleteByGoodsIds(Long[] goodsIds);
}
