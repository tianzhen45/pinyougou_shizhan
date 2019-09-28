package com.pinyougou.pay.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.BaseService;

import java.util.List;

public interface PayLogService extends BaseService<TbPayLog> {

    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param payLog 搜索条件
     * @return 分页信息
     */
    PageInfo<TbPayLog> search(Integer pageNum, Integer pageSize, TbPayLog payLog);

    List<TbPayLog> getAllPayLog();
}
