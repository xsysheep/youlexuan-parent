package com.youlexuan.sellergoods.service;


import com.youlexuan.entity.PageResult;
import com.youlexuan.entity.Result;
import com.youlexuan.pojo.TbBrand;

import java.util.List;
import java.util.Map;

/**
 * 品牌服务接口
 */
public interface IBrandService {

    /**
     * 返回全部
     * @return
     */
    public List<TbBrand> findAll();

    /**
     * 分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult findPage(TbBrand tbBrand, int pageNum,int pageSize);


    /**
     * 增加
     * @param tbBrand
     */
    public void add(TbBrand tbBrand);

    /**
     * 修改
     * @param tbBrand
     */
    public void update(TbBrand tbBrand);

    /**
     * 回显
     * @param id
     * @return
     */
    public TbBrand findOne(long id);

    /**
     * 根据id删除批量
     * @param ids
     * @return
     */
    public void delet(long[] ids);

    /**
     * 回显列表
     * @return
     */
    public List<Map> selectOptionList();
}
