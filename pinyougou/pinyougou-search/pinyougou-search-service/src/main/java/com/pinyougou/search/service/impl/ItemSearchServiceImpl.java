package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        //是否高亮
        boolean isHighLight = false;

        if(searchMap != null){
            String keywords = searchMap.get("keywords")+"";
            if (StringUtils.isNotBlank(keywords)) {
                //分词之后；查询标题、品牌、分类、商家中查询符合条件的数据
                queryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords, "title", "brand", "category", "seller"));

                //设置高亮
                isHighLight = true;
                //设置高亮域
                HighlightBuilder.Field highLightField = new HighlightBuilder.Field("title");
                //高亮的起始标签
                highLightField.preTags("<span style='color:red'>");
                //高亮的结束标签
                highLightField.postTags("</span>");

                queryBuilder.withHighlightFields(highLightField);
            }
        }
        //搜索对象
        NativeSearchQuery query = queryBuilder.build();

        //搜索
        AggregatedPage<TbItem> pageResult;
        if (isHighLight) {
            pageResult = esTemplate.queryForPage(query, TbItem.class, new SearchResultMapper() {
                @Override
                public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                    List<T> itemList = new ArrayList<>();

                    //处理高亮结果
                    //获取高亮数据
                    SearchHits hits = searchResponse.getHits();
                    TbItem item = null;
                    for (SearchHit hit : hits) {
                        //商品的json格式字符串
                        String itemJsonStr = hit.getSourceAsString();
                        item = JSON.parseObject(itemJsonStr, TbItem.class);

                        //获取高亮标题
                        HighlightField highlightField = hit.getHighlightFields().get("title");
                        if (highlightField != null) {
                            StringBuilder sb = new StringBuilder();
                            for (Text fragment : highlightField.getFragments()) {
                                sb.append(fragment.string());
                            }
                            //设置高亮标题
                            item.setTitle(sb.toString());
                        }

                        itemList.add((T)item);
                    }

                    //处理查询结果
                    return new AggregatedPageImpl<>(itemList, pageable, searchResponse.getHits().getTotalHits());
                }
            });
        } else {
            pageResult = esTemplate.queryForPage(query, TbItem.class);
        }

        //商品列表
        resultMap.put("itemList", pageResult.getContent());
        return resultMap;
    }
}
