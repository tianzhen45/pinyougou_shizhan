package com.pinyougou.es;

import com.alibaba.fastjson.JSON;
import com.pinyougou.es.dao.ItemElasticSearchDao;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring/applicationContext-*.xml")
public class ItemImport2ElasticSearchTest {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemElasticSearchDao itemElasticSearchDao;

    @Test
    public void itemImport(){
        //- 查询已启用的sku列表；遍历每个sku并将spec字符串转化为一个map设置到specMap类变量中
        //      select * from tb_item where status='1'
        TbItem param = new TbItem();
        param.setStatus("1");
        List<TbItem> itemList = itemMapper.select(param);
        for (TbItem tbItem : itemList) {
            Map specMap = JSON.parseObject(tbItem.getSpec(), Map.class);
            tbItem.setSpecMap(specMap);
        }

        //- 利用ElasticSearch中的Repository操作接口类保存数据到es
        itemElasticSearchDao.saveAll(itemList);
    }

}
