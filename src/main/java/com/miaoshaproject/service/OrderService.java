package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.OrderModel;

/**
 * Created by Diesel on 2019/7/2
 */
public interface OrderService {
    //1.通过前端url上传过来的promoId，然后下单接口内校验对应商品是否属于对应id且活动已经开始
    OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer amount) throws BusinessException;

    String generateOrderNO();
}
