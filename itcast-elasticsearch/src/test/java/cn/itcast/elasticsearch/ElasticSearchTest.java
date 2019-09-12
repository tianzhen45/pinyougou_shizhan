package cn.itcast.elasticsearch;

import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-es.xml")
public class ElasticSearchTest {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    //创建索引
    @Test
    public void createIndexAndMapping(){
        //创建索引
        esTemplate.createIndex(TbItem.class);
        //设置映射信息
        esTemplate.putMapping(TbItem.class);
    }
}
