package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/seckillGoods")
@RestController
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询符合条件的秒杀商品
     * @return 秒杀商品列表
     */
    @GetMapping("/findList")
    public List<TbSeckillGoods> findList(){
        return seckillGoodsService.findList();
    }

    /**
     * 新增
     * @param seckillGoods 实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbSeckillGoods seckillGoods){
        try {
            seckillGoodsService.add(seckillGoods);

            return Result.ok("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("新增失败");
    }

    /**
     * 根据主键查询
     * @param id 主键
     * @return 实体
     */
    @GetMapping("/findOne/{id}")
    public TbSeckillGoods findOne(@PathVariable Long id){
        //return seckillGoodsService.findOne(id);
        return seckillGoodsService.findOneInRedis(id);
    }

    /**
     * 修改
     * @param seckillGoods 实体
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbSeckillGoods seckillGoods){
        try {
            seckillGoodsService.update(seckillGoods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 根据主键数组批量删除
     * @param ids 主键数组
     * @return 实体
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            seckillGoodsService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 根据条件搜索
     * @param pageNum 页号
     * @param pageSize 页面大小
     * @param seckillGoods 搜索条件
     * @return 分页信息
     */
    @PostMapping("/search")
    public PageInfo<TbSeckillGoods> search(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                           @RequestBody TbSeckillGoods seckillGoods) {
        return seckillGoodsService.search(pageNum, pageSize, seckillGoods);
    }
    /**
     * 获取当前登录的用户名
     * 如果是匿名登录则返回的用户名为anonymousUser
     * @return 用户信息
     */
    @GetMapping("/getUsername")
    public Map<String, Object> getUsername(){
        Map<String, Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username", username);
        return map;
    }

}
