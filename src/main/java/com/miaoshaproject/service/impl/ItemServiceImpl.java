package com.miaoshaproject.service.impl;

import com.miaoshaproject.dao.ItemDOMapper;
import com.miaoshaproject.dao.ItemStockDOMapper;
import com.miaoshaproject.dataobject.ItemDO;
import com.miaoshaproject.dataobject.ItemStockDO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.service.ItemService;
import com.miaoshaproject.service.PromoService;
import com.miaoshaproject.service.model.ItemModel;
import com.miaoshaproject.service.model.PromoModel;
import com.miaoshaproject.validator.ValidationResult;
import com.miaoshaproject.validator.ValidatorImp;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Diesel on 2019/7/1
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImp validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private PromoService promoService;


    @Override
    @Transactional
    public void increaseSales(Integer id, Integer amount) throws BusinessException {
        itemDOMapper.increaseSales(id,amount);

    }

    private ItemDO convertItemDOFromItemModel(ItemModel itemModel) throws BusinessException {
        ItemDO itemDO = new ItemDO();
        if (itemModel ==null){
            return null;
        }
        BeanUtils.copyProperties(itemModel,itemDO);
        return itemDO;
    }

    private ItemStockDO convertItemStockFromItemModel(ItemModel itemModel) throws BusinessException {
        ItemStockDO itemStockDO = new ItemStockDO();
        if (itemModel ==null){
            return null;
        }
        itemStockDO.setStock(itemModel.getStock());
        itemStockDO.setItemId(itemModel.getId());
        return itemStockDO;
    }

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        if (itemModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
        //效验入参
        ValidationResult validationResult = validator.validate(itemModel);
        if (validationResult.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,validationResult.getErrMsg());
        }

        //将model转化成dataobject
        ItemDO itemDO = convertItemDOFromItemModel(itemModel);


        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = convertItemStockFromItemModel(itemModel);

        itemStockDOMapper.insertSelective(itemStockDO);


        //返回对象
        return this.getItemById(itemModel.getId());
    }

    //商品列表浏览
    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();
        //利用streamAPI将model类型转化为VO类型List
        List<ItemModel> itemModelList = itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = convertFromDataObject(itemDO,itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    //商品详情浏览
    @Override
    public ItemModel getItemById(Integer item_id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(item_id);
        if (itemDO ==null){
            return null;
        }
        //获得操作库存的方法
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(item_id);

        //将dataobject转化成model
        ItemModel itemModel = convertFromDataObject(itemDO,itemStockDO);

        //获得活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(item_id);
        if (promoModel != null && promoModel.getStatus() != 3){
            itemModel.setPromoModel(promoModel);
        }

        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        int affectedRow = itemStockDOMapper.decreaseStock(itemId,amount);
        //更新库存成功
        if (affectedRow >0){
            return true;
        }else {
            //跟新库存失败
            return false;
        }
    }

    private ItemModel convertFromDataObject(ItemDO itemDO,ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
