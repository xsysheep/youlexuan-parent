package com.youlexuan.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.youlexuan.entity.PageResult;
import com.youlexuan.entity.Result;
import com.youlexuan.pojo.TbBrand;
import com.youlexuan.sellergoods.service.IBrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private IBrandService iBrandService;

    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        return iBrandService.selectOptionList();
    }

    @RequestMapping("findAll")
    public List<TbBrand> findAll(){
        return iBrandService.findAll();
    }

    @RequestMapping("findPage")
    public PageResult findPage( int page, int rows){
        return iBrandService.findPage(null,page,rows);
    }

    @RequestMapping("search")
    public PageResult search(@RequestBody TbBrand tbBrand, int page, int rows){
        return iBrandService.findPage(tbBrand,page,rows);
    }

    @RequestMapping("add")
    public Result add(@RequestBody TbBrand tbBrand){
        try {
            iBrandService.add(tbBrand);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"增加失败");
        }
    }

    @RequestMapping("update")
    public Result update(@RequestBody TbBrand tbBrand){
        try {
            iBrandService.update(tbBrand);
            return new Result(true,"修改成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"修改失败");
        }
    }

    @RequestMapping("findOne")
    public TbBrand findOne(long id){
        return iBrandService.findOne(id);
    }

    @RequestMapping("delete")
    public Result delet(long[] ids){
        try {
            iBrandService.delet(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }
}
