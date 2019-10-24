package com.youlexuan.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.StringUtil;
import com.youlexuan.entity.PageResult;
import com.youlexuan.entity.Result;
import com.youlexuan.mapper.TbBrandMapper;
import com.youlexuan.pojo.TbBrand;
import com.youlexuan.pojo.TbBrandExample;
import com.youlexuan.sellergoods.service.IBrandService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements IBrandService {

    @Autowired
    private TbBrandMapper brandMapper;

    @Override
    public List<TbBrand> findAll() {
        TbBrandExample exam = new TbBrandExample();
        return brandMapper.selectByExample(exam);
    }

    @Override
    public PageResult findPage(TbBrand tbBrand,int pageNum, int pageSize) {
        TbBrandExample exam = new TbBrandExample();
        if(tbBrand!=null){
            if(!StringUtil.isEmpty(tbBrand.getName())){
                exam.createCriteria().andNameLike("%"+tbBrand.getName()+"%");
            }
        }

        PageHelper.startPage(pageNum,pageSize);
        Page<TbBrand> page = (Page<TbBrand>) brandMapper.selectByExample(exam);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void add(TbBrand tbBrand) {
        brandMapper.insert(tbBrand);
    }

    @Override
    public void update(TbBrand tbBrand) {
        brandMapper.updateByPrimaryKeySelective(tbBrand);
    }

    @Override
    public TbBrand findOne(long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delet(long[] ids) {
        for(long id:ids){
            brandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public List<Map> selectOptionList() {
        return brandMapper.selectOptionList();
    }
}
