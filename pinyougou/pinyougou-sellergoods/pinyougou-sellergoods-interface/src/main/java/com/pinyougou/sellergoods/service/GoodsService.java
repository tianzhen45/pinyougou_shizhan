package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.service.BaseService;
import com.pinyougou.vo.Goods;

import java.util.List;

public interface GoodsService extends BaseService<TbGoods> {
    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param goods 搜索条件
     * @return 分页信息
     */
    PageInfo<TbGoods> search(Integer pageNum, Integer pageSize, TbGoods goods);

    /**
     * 新增
     * @param goods 商品vo（基本、描述、sku列表）
     */
    void addGoods(Goods goods);
    /**
     * 根据主键查询商品vo
     * @param id 商品spu id
     * @return 商品vo
     */
    Goods findGoodsById(Long id);
    /**
     * 修改商品
     * @param goods 商品vo（基本、描述、sku）
     */
    void updateGoods(Goods goods);

    /**
     * 根据商品spu id数组更新商品spu 的审核状态
     * @param status 商品spu状态
     * @param ids 商品spu id数组
     */
    void updateStatus(Long[] ids, String status);

    /**
     * 批量更新商品的删除状态为已删除（1）
     * @param ids 商品spu id数组
     */
    void deleteGoodsByIds(Long[] ids);

    /**
     * 审核通过：根据spu id数组和已启用状态（1）查询商品sku列表
     * @param goodsIds 商品spu id数组
     * @param itemStatus 商品sku的状态
     * @return 商品sku 列表
     */
    List<TbItem> findItemListByGoodsIdsAndItemStatus(Long[] goodsIds, String itemStatus);

    /**
     * 在根据商品spu id查询商品vo的时候；里面的商品sku列表需要根据默认值进行降序排序。
     * @param goodsId 商品spu id
     * @param itemStatus 商品sku的状态
     * @return 商品vo
     */
    Goods findGoodsByIdAndStatus(Long goodsId, String itemStatus);
}
