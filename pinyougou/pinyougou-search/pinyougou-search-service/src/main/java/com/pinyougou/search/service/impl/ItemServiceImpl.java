package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.HashMap;
import java.util.Map;


@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();




        //搜索对象构建对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //如果没有输入搜索关键字则查询全部
        queryBuilder.withQuery(QueryBuilders.matchAllQuery());

        if(searchMap != null && searchMap.size()>0){
            String keywords = searchMap.get("keywords")+"";
            if (StringUtils.isNotBlank(keywords)) {
                //分词之后；查询标题、品牌、分类、商家中查询符合条件的数据
                queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords,  "seller"));
            }
        }


        //设置分页信息
        int pageNo = 1;
        String pageNoStr = searchMap.get("pageNo")+"";
        if (StringUtils.isNotBlank(pageNoStr)) {
            pageNo = Integer.parseInt(pageNoStr);
        }
        int pageSize = 20;
        String pageSizeStr = searchMap.get("pageSize")+"";
        if (StringUtils.isNotBlank(pageSizeStr)) {
            pageSize = Integer.parseInt(pageSizeStr);
        }

        queryBuilder.withPageable(PageRequest.of(pageNo - 1, pageSize));

        //搜索对象
        NativeSearchQuery query = queryBuilder.build();

        //搜索
        AggregatedPage<TbItem> pageResult = esTemplate.queryForPage(query, TbItem.class);

        //商品列表
        resultMap.put("itemList", pageResult.getContent());
        //总记录数
        resultMap.put("total", pageResult.getTotalElements());
        //总页数
        resultMap.put("totalPages", pageResult.getTotalPages());
        return resultMap;
    }
}

