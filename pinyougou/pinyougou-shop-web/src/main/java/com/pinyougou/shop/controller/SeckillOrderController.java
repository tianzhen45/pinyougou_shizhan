package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.vo.Result;
import com.pinyougou.vo.SellerSeckillOrderVo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author: Lewis
 * @date: 2019/9/27
 * @description:
 */
@RequestMapping("/seckillOrder")
@RestController
public class SeckillOrderController {

    @Reference(timeout = 999999, retries = 0)
    private OrderService orderService;

    /**
     * 批量删除用户秒杀订单
     * @param ids
     * @return
     */
    @GetMapping("/deleteSeckillOrder")
    public Result deleteSeckillOrder(Long[] ids){
        try {
            orderService.deleteSeckillOrder(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 查询商家秒杀订单
     * @param pageNum
     * @param pageSize
     * @param order
     * @return
     */
    @PostMapping("/search")
    public Map<String,Object> search(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                                  @RequestBody TbSeckillOrder order) {
        //设置商家id
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setSellerId(sellerId);
        return orderService.findSeckillOrderList(pageNum, pageSize, order);
    }
}
