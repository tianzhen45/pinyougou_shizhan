package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.mapper.AreasMapper;
import com.pinyougou.mapper.CitiesMapper;
import com.pinyougou.mapper.ProvincesMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl extends BaseServiceImpl<TbAddress> implements AddressService {

    @Autowired
    private AddressMapper addressMapper;



    @Autowired
    private CitiesMapper citiesMapper;

    @Autowired
    private AreasMapper areasMapper;
    @Autowired
    private ProvincesMapper provincesMapper;

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
    public void deleteAddress(Long id) {
        TbAddress tbAddress = new TbAddress();
        tbAddress.setId(id);
        addressMapper.delete(tbAddress);
    }

    @Override
    public List<TbProvinces> findProvinceList() {
        return provincesMapper.selectAll();
    }

    @Override
    public List<TbCities> findCityListByProvinceId(String provinceId) {
        TbCities tbCities = new TbCities();
        tbCities.setProvinceId(provinceId);
        return citiesMapper.select(tbCities);
    }

    @Override
    public List<TbAreas> findAreaListByCityId(String cityId) {
        TbAreas tbAreas = new TbAreas();
        tbAreas.setCityId(cityId);
        return areasMapper.select(tbAreas);
    }

    @Override
    public void updateByAddress(TbAddress address) {
        addressMapper.updateByPrimaryKeySelective(address);
    }

    @Override
    public Map<String, Object> showAddressName(String provinceId, String cityId, String townId) {
        return null;
    }

    @Override
    public void save(TbAddress address) {
        // 设置默认地址状态为“0”
        address.setIsDefault("0");
        // 设置创建时间
        address.setCreateDate(new Date());

        // 拼接省份城市地址
        // 创建拼接对象
        // 获取到地址拼接对象
//        String provinceIdStr = addressMapper.findAddressById1(address.getProvinceId());
//        String cityIdStr = addressMapper.findAddressById2(address.getCityId());
//        String townIdStr = addressMapper.findAddressById3(address.getTownId());
        // 调用数据访问接口添加地址
        addressMapper.insertSelective(address);
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
