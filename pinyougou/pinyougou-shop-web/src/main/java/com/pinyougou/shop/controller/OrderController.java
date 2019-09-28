package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * @author: Lewis
 * @date: 2019/9/27
 * @description:
 */
@RequestMapping("/order")
@RestController
public class OrderController {

    @Reference
    private OrderService orderService;

    /**
     * 修改普通订单价格
     * @param orderId
     * @return
     */
    @PostMapping("/updatePayment")
    public Result updatePayment(TbOrder order) {
        if (order == null || order.getPayment() == null || "".equals(order.getPayment())) {
            return Result.fail("修改失败");
        }
        try {
            orderService.updatePayment(order);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 修改订单状态（主要是付款的改为发货）
     * @param order
     * @return
     */
    @GetMapping("/changeStatus")
    public Result changeStatus(TbOrder order) {
        try {
            orderService.updateStatus(order);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 查询商家普通订单
     * @param pageNum
     * @param pageSize
     * @param order
     * @return
     */
    @PostMapping("/search")
    public PageInfo<TbOrder> search(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                                    @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                    @RequestBody TbOrder order) {
        //设置商家id
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setSellerId(sellerId);
        return orderService.findOrderList(pageNum, pageSize, order);
    }
}
