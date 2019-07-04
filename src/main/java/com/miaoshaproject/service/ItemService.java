package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.ItemModel;

import java.util.List;

/**
 * Created by Diesel on 2019/7/1
 */
public interface ItemService {
    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //返回商品列表
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer item_id);

    //订单减库存
    boolean decreaseStock(Integer itemId,Integer amount) throws BusinessException;

    void increaseSales(Integer id,Integer amount) throws BusinessException;
}
