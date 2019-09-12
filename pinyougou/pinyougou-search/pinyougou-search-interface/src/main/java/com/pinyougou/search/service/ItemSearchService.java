package com.pinyougou.search.service;

import java.util.Map;

public interface ItemSearchService {
    /**
     * 根据搜索条件搜索es中的商品数据
     * @param searchMap 搜索条件
     * @return 搜索结果
     */
    Map<String, Object> search(Map<String, Object> searchMap);
}
