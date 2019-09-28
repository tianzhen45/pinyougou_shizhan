package com.pinyougou.user.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.service.BaseService;

import java.util.List;
import java.util.Map;

public interface AddressService extends BaseService<TbAddress> {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param address 搜索条件
     * @return 分页信息
     */
    PageInfo<TbAddress> search(Integer pageNum, Integer pageSize, TbAddress address);

    void deleteAddress(Long id);

    List<TbProvinces> findProvinceList();

    List<TbCities> findCityListByProvinceId(String provinceId);

    List<TbAreas> findAreaListByCityId(String cityId);

    void updateByAddress(TbAddress address);

    Map<String, Object> showAddressName(String provinceId, String cityId, String townId);

    void save(TbAddress address);
}
