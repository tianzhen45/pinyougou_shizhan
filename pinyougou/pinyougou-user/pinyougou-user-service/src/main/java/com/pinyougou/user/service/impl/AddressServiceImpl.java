package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl extends BaseServiceImpl<TbAddress> implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public PageInfo<TbAddress> search(Integer pageNum, Integer pageSize, TbAddress address) {
        //设置分页
        PageHelper.startPage(pageNum, pageSize);
        //创建查询对象
        Example example = new Example(TbAddress.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        //模糊查询
        /**if (StringUtils.isNotBlank(address.getProperty())) {
            criteria.andLike("property", "%" + address.getProperty() + "%");
        }*/

        List<TbAddress> list = addressMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @Override
    public List<TbAddress> findAddressByUser(String username) {
        TbAddress tbAddress = new TbAddress();
        tbAddress.setUserId(username);
        return addressMapper.select(tbAddress);
    }

    @Override
    public boolean delete(Long id) {
        return addressMapper.deleteByPrimaryKey(id) == 1;
    }

    @Override
    public void setDefault(TbAddress address) {
        address.setIsDefault("1");
        List<TbAddress> tbAddresses = addressMapper.selectAll();
        for (TbAddress tbAddress : tbAddresses) {
            tbAddress.setIsDefault("0");
            addressMapper.updateByPrimaryKeySelective(tbAddress);
        }
        addressMapper.updateByPrimaryKeySelective(address);
    }


    @Override
    public void add(TbAddress tbAddress) {
        tbAddress.setIsDefault("0");
        tbAddress.setCreateDate(new Date());
        super.add(tbAddress);
    }
}
