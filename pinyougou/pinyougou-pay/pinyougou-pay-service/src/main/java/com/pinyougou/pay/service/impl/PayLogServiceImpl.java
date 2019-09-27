package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import com.pinyougou.common.util.PoiUtils;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pay.service.PayLogService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class PayLogServiceImpl extends BaseServiceImpl<TbPayLog> implements PayLogService {

    @Autowired
    private PayLogMapper payLogMapper;

    @Override
    public PageInfo<TbPayLog> search(Integer pageNum, Integer pageSize, TbPayLog payLog) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);
        // 创建查询对象
        Example example = new Example(TbPayLog.class);

        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();

        // 支付状态
        if (StringUtils.isNotBlank(payLog.getTradeState())) {
            criteria.andEqualTo("tradeState", payLog.getTradeState());
        }

        // 支付时间
        if (payLog.getPayTime() != null) {
            criteria.andEqualTo("payTime",   new SimpleDateFormat("yyyy-MM-dd").format(payLog.getPayTime()));
        }

        // 订单号
        if (StringUtils.isNotBlank(payLog.getOrderList())) {
            criteria.andLike("orderList", "%" + payLog.getOrderList() + "%");
        }

        List<TbPayLog> list = payLogMapper.selectByExample(example);
        return new PageInfo<>(list);
    }

    @GetMapping("/export")
    //ResponseEntity里面装了所有响应的数据
    public ResponseEntity<byte[]> exportExcel() throws IOException {
        return PoiUtils.exportPayLogExcel(payLogMapper.selectAll());
    }
}
