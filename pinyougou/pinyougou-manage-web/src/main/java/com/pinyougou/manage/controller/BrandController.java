package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/brand")
@RestController //将本类中的所有方法的返回结果都当做输出内容输出到浏览器
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有品牌列表；格式为：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     * @return 品牌列表；格式为：[{id:'1',text:'联想'},{id:'2',text:'华为'}]
     */
    @GetMapping("/selectOptionList")
    public List<Map<String, String>> selectOptionList(){
        return brandService.selectOptionList();
    }

    /**
     * 根据主键查询
     * @param id 品牌id
     * @return 品牌
     */
    @GetMapping("/findOne/{id}")
    public TbBrand findOne(@PathVariable Long id){
        return brandService.findOne(id);
    }

    /**
     * 更新品牌数据
     * @param brand 品牌数据
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return Result.ok("更新品牌成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新品牌失败！");
    }

    /**
     * 新增品牌数据
     * @param brand 品牌数据
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return Result.ok("新增品牌成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("新增品牌失败！");
    }

    /**
     * 分页查询品牌数据
     * @param pageNum 页号
     * @param pageSize 页大小
     * @return 分页信息对象
     */
    @GetMapping("/findPage")
    public PageInfo<TbBrand> findPage(@RequestParam(defaultValue = "1")Integer pageNum,
                             @RequestParam(defaultValue = "10")Integer pageSize){
        //return brandService.testPage(pageNum, pageSize);
        return brandService.findPage(pageNum, pageSize);
    }

    /**
     * 分页查询品牌数据
     * @param pageNum 页号
     * @param pageSize 页大小
     * @return 品牌列表
     */
    @GetMapping("/testPage")
    public List<TbBrand> testPage(@RequestParam(defaultValue = "1")Integer pageNum,
                                  @RequestParam(defaultValue = "10")Integer pageSize){
        //return brandService.testPage(pageNum, pageSize);
        return brandService.findPage(pageNum, pageSize).getList();
    }

    /**
     * 查询所有品牌数据
     * @return 品牌列表
     */
    @GetMapping("/findAll")
    public List<TbBrand> findAll(){
        //return brandService.queryAll();
        return brandService.findAll();
    }

    /**
     * 批量删除品牌
     * @param ids 品牌id数组
     * @return 操作结果
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.deleteByIds(ids);
            return Result.ok("删除品牌成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除品牌失败！");
    }

    /**
     * 根据分页信息和查询条件分页模糊查询品牌数据
     * @param pageNum 页号
     * @param pageSize 页大小
     * @param brand 查询条件
     * @return 分页信息对象
     */
    @PostMapping("/search")
    public PageInfo<TbBrand> search(@RequestParam(defaultValue = "1")Integer pageNum,
                                    @RequestParam(defaultValue = "10")Integer pageSize,
                                    @RequestBody TbBrand brand){
        return brandService.search(pageNum, pageSize, brand);
    }
}
