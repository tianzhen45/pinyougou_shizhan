package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/address")
@RestController
public class AddressController {

    @Reference
    private AddressService addressService;
    @GetMapping("/findAreaListByCityId")
    public List<TbAreas> findAreaListByCityId(String cityId){
        return addressService.findAreaListByCityId(cityId);
    }

    /**
     * 查询所有省份
     * @return 省份集合
     * */

    @GetMapping("/findProvinceList")
    public List<TbProvinces> findProvinceList(){
        return addressService.findProvinceList();
    }

    /**
     * 根据省份id查询所属城市
     * @param provinceId
     * @return 所属城市集合
     */

    @GetMapping("/findCityListByProvinceId")
    public List<TbCities> findCityListByProvinceId(String provinceId){
        return addressService.findCityListByProvinceId(provinceId);
    }

    /**
     * 获取当前登录用户的地址列表
     * @return 地址列表
     */
    @GetMapping("/findAddressList")
    public List<TbAddress> findAddressList(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //--查询当前登录用户的地址列表
        //select * from tb_address where user_id=?
        TbAddress param = new TbAddress();
        param.setUserId(userId);
        return addressService.findByWhere(param);
    }

    /**
     * 新增
     * @param address 实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result add(@RequestBody TbAddress address){
        try {

            addressService.add(address);

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
    public TbAddress findOne(@PathVariable Long id){
        return addressService.findOne(id);
    }

    /**
     * 修改
     * @param address 实体
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody TbAddress address){
        try {
            addressService.update(address);
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
            addressService.deleteByIds(ids);
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
     * @param address 搜索条件
     * @return 分页信息
     */
    @PostMapping("/search")
    public PageInfo<TbAddress> search(@RequestParam(value = "pageNum", required = false, defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                           @RequestBody TbAddress address) {
        return addressService.search(pageNum, pageSize, address);
    }
    @GetMapping("/deleteAddress")
    public Result deleteAddress(Long id){
        Result result = Result.fail("删除失败");
        try{

            addressService.deleteAddress(id);
            result = Result.ok("删除成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    /** 添加地址 */
    @PostMapping("/save")
    public boolean saveOrUpdate(@RequestBody TbAddress address, HttpServletRequest request) {
        // 获取用户名
        String name = request.getRemoteUser();
        // 调用服务方法添加地址
        try {
            address.setUserId(name);
            addressService.save(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    /** 省份城市地区数据回显名称*/
    //ttp://user.pinyougou.com/address/showAddressName?provinceId=120000&cityId=120100&areaId=120102
    @GetMapping("/showAddressName")
    public Map<String,Object> showAddressName (String provinceId, String cityId, String townId) {
        Map<String,Object> data = addressService.showAddressName(provinceId,cityId,townId);
        return data;
    }
    @GetMapping("/updateStatus")
    public Result updateStatus(Long id){
        Result result = Result.fail("设置失败");
        try {
            TbAddress tbAddress = addressService.findOne(id);
            tbAddress.setIsDefault("1");
            addressService.update(tbAddress);
            addressService.updateStatus(id);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
