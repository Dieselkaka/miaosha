package com.miaoshaproject.error;

/**
 * Created by Diesel on 2019/6/25
 */
public interface CommonError {
    int getErrCode();
    String getErrMsg();
    CommonError setErrMsg(String errMsg);
}
