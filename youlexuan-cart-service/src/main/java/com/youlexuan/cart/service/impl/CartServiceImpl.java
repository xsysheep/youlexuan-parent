package com.youlexuan.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.youlexuan.cart.service.CartService;
import com.youlexuan.mapper.TbItemMapper;
import com.youlexuan.pojo.TbItem;
import com.youlexuan.pojo.TbOrderItem;
import com.youlexuan.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper tbItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {

        //根据ID查询商品sku
        TbItem item = tbItemMapper.selectByPrimaryKey(itemId);
        //获取商家ID
        String sellerId = item.getSellerId();
        //判断商家ID是否存在于购物车内
        Cart  cart = searchCartBySellerId(cartList,sellerId);
        //判断购物车是否为空
        if(null==cart){
            //为空 ,创建购物车
            cart = new Cart();
            //购物车添加信息
            cart.setSellerId(sellerId);
            cart.setSellerName(item.getSeller());
            TbOrderItem orderItem = createOrderItem(item,num);
            ArrayList<TbOrderItem> orderItems = new ArrayList<>();
            orderItems.add(orderItem);
            cart.setOrderItemList(orderItems);
            //将购物车对象添加到购物车列表
            cartList.add(cart);

        }else{
            //如果购物车列表中存在该商家的购物车
            // 判断购物车明细列表中是否存在该商品
            TbOrderItem tbOrderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
            if(null==tbOrderItem){
                //不存在创建明显
                tbOrderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(tbOrderItem);
            }else{
                //如果有，在原购物车明细上添加数量，更改金额
                tbOrderItem.setNum(tbOrderItem.getNum()+num);
                tbOrderItem.setTotalFee(new BigDecimal(tbOrderItem.getNum()*tbOrderItem.getPrice().doubleValue()));
                //如果数量操作后小于等于0，则移除
                if(tbOrderItem.getNum()<=0){
                    cart.getOrderItemList().remove(tbOrderItem);
                }
                //如果移除后cart的明细数量为0 则移除
                if(cart.getOrderItemList().size()<=0){
                    cartList.remove(cart);
                }
            }
        }

        return cartList;
    }


    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车数据....."+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList==null){
            cartList=new ArrayList();
        }
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis存入购物车数据....."+username);
        redisTemplate.boundHashOps("cartList").put(username, cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
        System.out.println("合并购物车");
        for(Cart cart: cartList2){
            for(TbOrderItem orderItem:cart.getOrderItemList()){
                cartList1= addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList1;
    }

    /**
     * 判断判断购物车明细列表中是否存在该商品
     * @param orderItemList
     * @param itemId
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for(TbOrderItem tbOrderItem:orderItemList){
            if(tbOrderItem.getItemId().equals(itemId)){
                return tbOrderItem;
            }
        }
        return null;

    }

    /**
     * 创建订单明细
     * @param itemId
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item, Integer num) {

        if(num <= 0){
            throw new RuntimeException("数量不能<=0");
        }

        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setNum(num);
        orderItem.setId(item.getId());
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
        return orderItem;




    }

    /**
     * 判断商家ID是否存在于购物车内
     * @param sellerId
     * @return Cart
     */
    private Cart searchCartBySellerId(List<Cart> carList,String sellerId) {
        for(Cart cart : carList){
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
}
