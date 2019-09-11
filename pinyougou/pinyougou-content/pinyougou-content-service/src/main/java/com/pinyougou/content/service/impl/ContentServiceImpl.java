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
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ContentServiceImpl extends BaseServiceImpl<TbContent> implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

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

        List<TbContent> contentList = contentMapper.selectByExample(example);

        return contentList;
    }

}
