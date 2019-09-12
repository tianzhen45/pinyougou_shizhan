package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();

        //搜索对象构建对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //如果没有输入搜索关键字则查询全部
        queryBuilder.withQuery(QueryBuilders.matchAllQuery());

        if(searchMap != null){
            String keywords = searchMap.get("keywords")+"";
            if (StringUtils.isNotBlank(keywords)) {
                //分词之后；查询标题、品牌、分类、商家中查询符合条件的数据
                queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords, "title", "brand", "category", "seller"));
            }
        }
        //搜索对象
        NativeSearchQuery query = queryBuilder.build();

        //搜索
        AggregatedPage<TbItem> pageResult = esTemplate.queryForPage(query, TbItem.class);

        //商品列表
        resultMap.put("itemList", pageResult.getContent());
        return resultMap;
    }
}
