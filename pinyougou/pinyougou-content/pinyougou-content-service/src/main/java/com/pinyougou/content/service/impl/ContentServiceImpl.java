package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    //内容数据在redis中的键名
    private static final String REDIS_CONTENT_LIST = "CONTENT_LIST";
    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageInfo<TbContent> search(Integer pageNum, Integer pageSize, TbContent content) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbContent.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        /**if (StringUtils.isNotBlank(content.getProperty())) {
            criteria.andLike("property", "%" + content.getProperty() + "%");
        }*/

        List<TbContent> list = contentMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public List<TbContent> findContentListByCategoryId(Long categoryId) {
        List<TbContent> contentList = null;
        //1、查询redis中内容数据
        try {
            contentList = (List<TbContent>) redisTemplate.boundHashOps(REDIS_CONTENT_LIST).get(categoryId);
            if (contentList != null) {
                //2、如果是存在数据则直接返回；
                return contentList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //3、如果不存在则根据条件从mysql数据库中查询，将查询到的数据设置到redis；返回

        /**
         * --查询：根据内容分类id、状态查询内容数据并且根据排序属性降序排序
         * select * from tb_content where category_id=1 and status='1' order by sort_order desc
         */
        Example example = new Example(TbContent.class);
        example.createCriteria()
                .andEqualTo("status", "1")
                .andEqualTo("categoryId", categoryId);
        //根据排序属性降序排序
        example.orderBy("sortOrder").desc();

        contentList = contentMapper.selectByExample(example);

        try {
            //设置缓存数据
            redisTemplate.boundHashOps(REDIS_CONTENT_LIST).put(categoryId, contentList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentList;
    }

}
