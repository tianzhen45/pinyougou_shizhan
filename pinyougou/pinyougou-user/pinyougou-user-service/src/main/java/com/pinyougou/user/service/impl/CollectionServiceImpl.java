package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.CollectionMapper;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.TbCollection;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.user.service.CollectionService;
import com.pinyougou.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class CollectionServiceImpl extends BaseServiceImpl<TbCollection> implements CollectionService {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Override
    public PageInfo<TbCollection> search(Integer pageNum, Integer pageSize, TbCollection collection) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbCollection.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        /**if (StringUtils.isNotBlank(collection.getProperty())) {
            criteria.andLike("property", "%" + collection.getProperty() + "%");
        }*/

        List<TbCollection> list = collectionMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public void collect(String username, String itemId) {
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        TbCollection tbCollection = new TbCollection();

        tbCollection.setCreateTime(new Date());
        tbCollection.setUserId(username);
        tbCollection.setBrand(tbItem.getBrand());
        tbCollection.setCategory(tbItem.getCategory());
        tbCollection.setImage(tbItem.getImage());
        tbCollection.setPrice(tbItem.getPrice());
        tbCollection.setItemId(tbItem.getId());
        tbCollection.setTitle(tbItem.getTitle());
        tbCollection.setSeller(tbItem.getSeller());
        tbCollection.setSellerId(tbItem.getSellerId());
        tbCollection.setStatus(tbItem.getStatus());

        collectionMapper.insertSelective(tbCollection);
    }

    @Override
    public void uncollect(String username, String itemId) {
        Example example = new Example(TbCollection.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",username);
        criteria.andEqualTo("itemId",itemId);
        collectionMapper.deleteByExample(example);
    }

    @Override
    public boolean checkCollect(String username, String itemId) {
        Example example = new Example(TbCollection.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",username);
        criteria.andEqualTo("itemId",itemId);
        List<TbCollection> tbCollections = collectionMapper.selectByExample(example);
        if(tbCollections != null && tbCollections.size() > 0){
            return true;
        }
        return false;
    }

    @Override
    public List<TbItem> checkCollectItemList(String username, List<TbItem> itemList) {
        for (TbItem tbItem : itemList) {
            if(this.checkCollect(username,tbItem.getId()+"")){
                //表示被收藏了
                tbItem.setStatus("2");
            }
        }
        return itemList;
    }


}
