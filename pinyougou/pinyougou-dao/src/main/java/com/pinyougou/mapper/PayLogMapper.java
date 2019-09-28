package com.pinyougou.mapper;

import com.pinyougou.pojo.TbPayLog;

import java.util.List;

public interface PayLogMapper extends BaseMapper<TbPayLog> {

    List<TbPayLog> getAllPayLog();

}
