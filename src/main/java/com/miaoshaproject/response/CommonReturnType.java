package com.miaoshaproject.response;

/**
 * Created by Diesel on 2019/6/25
 */
public class CommonReturnType {
    //表面对应的返回处理结果,成功为success，失败为fail
    private String status;

    //若status等于success，data内返回前端需要的json信息
    //若status等于fail，data内返回通用的错误信息
    private Object data;

    public static CommonReturnType Create(Object result){
        return CommonReturnType.Create(result,"success");
    }

    public static CommonReturnType Create(Object result, String status) {
        CommonReturnType commonReturnType = new CommonReturnType();
        commonReturnType.setData(result);
        commonReturnType.setStatus(status);
        return commonReturnType;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
