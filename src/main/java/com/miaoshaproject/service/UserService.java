package com.miaoshaproject.service;

import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.service.model.UserModel;

public interface UserService {

    //通过用户ID返回用户对象的方法
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;

    /**
     *
     * @param telphone 手机号码
     * @param encrptpassword 加密后的密码
     * @throws BusinessException
     */
    UserModel validateLogin(String telphone,String encrptpassword) throws BusinessException;
}
