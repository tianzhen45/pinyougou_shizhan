package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbCollection;
import com.pinyougou.user.service.CollectionService;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/collection")
@RestController
public class CollectionController {

    @Reference
    private CollectionService collectionService;

    /**
     * 新增
     * @param collection 实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbCollection collection){
        try {

            collectionService.add(collection);

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
    public TbCollection findOne(@PathVariable Long id){
        return collectionService.findOne(id);
    }

    /**
     * 修改
     * @param collection 实体
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbCollection collection){
        try {
            collectionService.update(collection);
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
            collectionService.deleteByIds(ids);
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
     * @param collection 搜索条件
     * @return 分页信息
     */
    @PostMapping("/search")
    public PageInfo<TbCollection> search(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                           @RequestBody TbCollection collection) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        collection.setUserId(username);

        return collectionService.search(pageNum, pageSize, collection);
    }


    @GetMapping("/collect")
    @CrossOrigin(origins = "*")
    public Result collect(String itemId){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if("anonymous".equals(username)){
                return Result.fail("未登录无法收藏");
            }
            collectionService.collect(username,itemId);
            return Result.ok("收藏成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("收藏失败");
    }


    @GetMapping("/uncollect")
    @CrossOrigin(origins = "*")
    public Result uncollect(String itemId){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if("anonymous".equals(username)){
                return Result.fail("未登录无法取消收藏");
            }
            collectionService.uncollect(username,itemId);
            return Result.ok("取消收藏成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("取消收藏失败");
    }
}
