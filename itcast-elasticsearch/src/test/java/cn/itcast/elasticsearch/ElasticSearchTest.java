package cn.itcast.elasticsearch;

import cn.itcast.es.dao.ItemDao;
import com.pinyougou.pojo.TbItem;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-es.xml")
public class ElasticSearchTest {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private ItemDao itemDao;

    //创建索引
    @Test
    public void createIndexAndMapping(){
        //创建索引
        esTemplate.createIndex(TbItem.class);
        //设置映射信息
        esTemplate.putMapping(TbItem.class);
    }

    //新增或更新
    @Test
    public void addOrUpdate(){
        TbItem item = new TbItem();
        item.setId(123L);
        item.setTitle("22222 OPPO Reno2 4800万变焦四摄 视频防抖 6.5英寸阳光护眼全面屏 8GB+128GB 深海夜光 拍照游戏智能手机");
        item.setSeller("欧柏");
        item.setBrand("欧柏");
        item.setPrice(2999.0);
        item.setCategory("手机");
        item.setImage("https://item.jd.com/100004418727.html");

        //设置规格
        Map<String, String> specMap = new HashMap<>();
        specMap.put("机身内存", "16G");
        specMap.put("屏幕尺寸", "6.1");
        item.setSpecMap(specMap);

        itemDao.save(item);
    }

    //分页查询
    @Test
    public void findAll(){
        //参数1：页号（从0开始）
        //参数2：页大小
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<TbItem> pageResult = itemDao.findAll(pageRequest);
        System.out.println("总记录数为：" + pageResult.getTotalElements());
        System.out.println("总页数为：" + pageResult.getTotalPages());
        for (TbItem tbItem : pageResult.getContent()) {
            System.out.println(tbItem);
        }
    }

    //条件删除
    @Test
    public void delete(){
        TbItem param = new TbItem();
        param.setId(123L);
        itemDao.delete(param);
    }

    //通配符 * 一个字符
    //匹配分词的词条中是否符合通配符的词条
    @Test
    public void wildcard() throws Exception {
        //查询条件对象构建对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.withQuery(QueryBuilders.wildcardQuery("title", "手*"));

        //查询条件对象
        NativeSearchQuery query = queryBuilder.build();
        //搜索
        AggregatedPage<TbItem> result = esTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数为：" + result.getTotalElements());
        System.out.println("总页数为：" + result.getTotalPages());
        for (TbItem item : result.getContent()) {
            System.out.println(item);
        }
    }

    //分词查询
    @Test
    public void matchQuery() throws Exception {
        //查询条件对象构建对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.withQuery(QueryBuilders.matchQuery("title", "拍照游戏智能手机"));

        //查询条件对象
        NativeSearchQuery query = queryBuilder.build();
        //搜索
        AggregatedPage<TbItem> result = esTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数为：" + result.getTotalElements());
        System.out.println("总页数为：" + result.getTotalPages());
        for (TbItem item : result.getContent()) {
            System.out.println(item);
        }
    }

    //分词查询
    @Test
    public void copyToQuery() throws Exception {
        //查询条件对象构建对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.withQuery(QueryBuilders.matchQuery("keywords", "欧柏"));

        //查询条件对象
        NativeSearchQuery query = queryBuilder.build();
        //搜索
        AggregatedPage<TbItem> result = esTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数为：" + result.getTotalElements());
        System.out.println("总页数为：" + result.getTotalPages());
        for (TbItem item : result.getContent()) {
            System.out.println(item);
        }
    }


    //嵌套组合查询
    @Test
    public void nestedQuery() throws Exception {
        //查询条件对象构建对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(QueryBuilders.matchQuery("keywords", "欧柏"));

        //因为有多个过滤查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //机身内存
        /**
         * 参数1：嵌套域对应类的变量名称
         * 参数2：查询条件，参数1：嵌套域完整域名；参数2：查询的值
         * 参数3：在查询条件和过滤条件查询的时候；都对每个文档有一个得分；max表示在这些分值中取最大的作为该文档的返回分值
         */
        NestedQueryBuilder query1 = QueryBuilders.nestedQuery("specMap", QueryBuilders.wildcardQuery("specMap.机身内存.keyword", "16G"), ScoreMode.Max);
        boolQueryBuilder.must(query1);

        //屏幕尺寸
        NestedQueryBuilder query2 = QueryBuilders.nestedQuery("specMap", QueryBuilders.wildcardQuery("specMap.屏幕尺寸.keyword", "6.1"), ScoreMode.Max);
        boolQueryBuilder.must(query2);

        //添加过滤查询
        queryBuilder.withFilter(boolQueryBuilder);

        //查询条件对象
        NativeSearchQuery query = queryBuilder.build();
        //搜索
        AggregatedPage<TbItem> result = esTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数为：" + result.getTotalElements());
        System.out.println("总页数为：" + result.getTotalPages());
        for (TbItem item : result.getContent()) {
            System.out.println(item);
        }
    }

}
