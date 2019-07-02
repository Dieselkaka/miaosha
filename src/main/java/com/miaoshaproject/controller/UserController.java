package com.miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobject.UserVO;
import com.miaoshaproject.error.BusinessException;
import com.miaoshaproject.error.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*",origins = "*")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    //用户登录接口
    @RequestMapping(value = "/login",method = RequestMethod.POST,consumes = CONTENT_TYPE_FORMED)
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                  @RequestParam(name = "password")String password) throws UnsupportedEncodingException, NoSuchAlgorithmException, BusinessException {

        //入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telphone)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        //用户登录服务，用来校验用户登录是否合法
        UserModel userModel = userService.validateLogin(telphone,EncodeByMD5(password));
        UserVO userVO = convertFromUserModel(userModel);

        //将登录凭证加入到用户登录成功的session内
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userVO);

        return CommonReturnType.Create(userVO);

    }

    //用户注册接口
    @RequestMapping(value = "/register",method = RequestMethod.POST,consumes = CONTENT_TYPE_FORMED)
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone")String telphone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender") Byte gender,
                                     @RequestParam(name ="age") Integer age,
                                     @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //验证手机号和对应otp验证码是否相符
        String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        if (!StringUtils.equals(otpCode,inSessionOtpCode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不正确");
        }
        //用户的注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(age);
        userModel.setGender(gender);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byPhone");
        userModel.setEncrptPassword(this.EncodeByMD5(password));
        userService.register(userModel);
        return CommonReturnType.Create(null);

    }

    public String EncodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        //加密字符串
        String newStr = base64Encoder.encode(md5.digest(str.getBytes("UTF-8")));
        return newStr;
    }

    //用户获得otp短信接口
    @RequestMapping(value = "/getotp",method = RequestMethod.POST,consumes = CONTENT_TYPE_FORMED)
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone")String telphone){

        //需要按照一定规则生成otp验证码
        Random random = new Random();
        int randomInt = random.nextInt(90000);
        randomInt += 10000;
        String otpCodes = String.valueOf(randomInt);



        //将otp验证码同对应手机关联,使用HTTPSession方式绑定telphone和otpcode
        httpServletRequest.getSession().setAttribute(telphone,otpCodes);


        //将otp验证码通过手机短信通道发送给用户，省略
        System.out.println("Telphone = " + telphone + "& OtcCodes = " + otpCodes);


        return CommonReturnType.Create(null);


    }

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUserById(@RequestParam(name = "id")Integer id) throws BusinessException {
        //调用service服务获取对应id的用户对象返回给前端
        UserModel userModel = userService.getUserById(id);

        //若获取对应用户信息不存在
        if (userModel ==null){
            throw  new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }

        //将核心领域模型转化为可控UI使用的VO对象
        UserVO userVO = convertFromUserModel(userModel);
        //返回通用对象
        return CommonReturnType.Create(userVO);
    }

    private UserVO convertFromUserModel(UserModel userModel){
        if (userModel ==null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);

        return userVO;
    }


}
