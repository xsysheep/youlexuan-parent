package com.youlexuan.page.service;


/**
 * 商品详细页接口
 * @author Administrator
 */
public interface IItemPageService {
    /**
     * 生成商品详细页
     * @param goodsId
     */
    public boolean genItemHtml(Long goodsId);
}
