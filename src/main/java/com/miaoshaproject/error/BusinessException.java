package com.miaoshaproject.error;

/**
 * Created by Diesel on 2019/6/25
 */
//包装类业务异常实现
public class BusinessException extends Exception implements CommonError {

    private CommonError commonError;

    //用于直接接收EmBusiness的传参，用于构建业务异常
    public BusinessException(CommonError commonError) {
        super();
        this.commonError = commonError;
    }

    //接收自定义errMsg的方式构造业务异常
    public BusinessException(CommonError commonError,String errMsg){
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }

    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
