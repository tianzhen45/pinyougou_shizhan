package cn.itcast.elasticsearch;

import cn.itcast.es.dao.ItemDao;
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
        itemDao.save(item);
    }
}
