package com.miaoshaproject.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by Diesel on 2019/6/28
 * 参数校验类，实现的javax的类，只需在对应bean上加注解就可以了
 */
@Component
public class ValidatorImp implements InitializingBean {

    private Validator validator;

    //实现校验
    public ValidationResult validate(Object bean){
        final ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(bean);
        if (constraintViolationSet.size() >0){
            //有错误
            result.setHasErrors(true);
            constraintViolationSet.forEach(constraintViolation ->{
                String errMsg = constraintViolation.getMessage();
                String propertyName = constraintViolation.getPropertyPath().toString();
                result.getErrorMsgMap().put(propertyName,errMsg);
            });
        }
        return result;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //将hibernate的validator通过工厂的初始化方法实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
}
