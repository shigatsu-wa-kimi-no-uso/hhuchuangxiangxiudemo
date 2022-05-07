package team.hhu.chuangxiangxiudemo.user.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.constant.UserInfoSQLColumnName;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoBasic;
import team.hhu.chuangxiangxiudemo.user.service.UserAccountService;
import team.hhu.chuangxiangxiudemo.user.service.UserInfoService;
import team.hhu.chuangxiangxiudemo.user.service.UserVerificationService;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 注入service
 */

@Controller
@ComponentScan
@Slf4j
public class UserVisitController
{
    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private UserVerificationService userVerificationService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @GetMapping("/login")
    public String loginPageDirect()
    {
        return "/login";
    }


    @ResponseBody
    @RequestMapping(value = {"account/login"},method= RequestMethod.POST)
    public JSONObject login(@RequestBody @NotNull Map<String,Object> map,
            HttpServletRequest request)
            throws IOException
    {
        JSONObject result=new JSONObject();
        String username=(String) map.get("username");
        String password=(String) map.get("password");
        if(username==null ||password==null)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            LogUtil.info(ResultConstants.OPERATION_INVALID_PARAMETER.getMessage());
            return result;
        }
        LogUtil.debug("\n获得信息:\n"+ map);
        OperationResult serviceResult=userAccountService.login(username,password);
        if (serviceResult.getStatus()== ResultConstants.OPERATION_OK)
        {
            //登录成功

            UserInfoBasic info=(UserInfoBasic) serviceResult.getReturnValue();
            LogUtil.info("\n用户信息\n"+info);
            int userId=info.getUserId();
            serviceResult=userInfoService.getUserAliasById(userId);
            if(serviceResult.getStatus()==ResultConstants.OPERATION_OK)
            {
                result.put("alias",serviceResult.getReturnValue());
                serviceResult=userInfoService.getUserAvatarById(userId);

                if(serviceResult.getStatus()==ResultConstants.OPERATION_OK)
                {
                    System.out.println("头像"+serviceResult.getReturnValue());
                    if(serviceResult.getReturnValue()==null || serviceResult.getReturnValue().equals(""))
                    {
                        result.put("avatar","default.jpg");
                    }
                    else
                        result.put("avatar",serviceResult.getReturnValue());
                    serviceResult = userVerificationService.createToken(String.valueOf(info.getUserId()), info.getUserName());
                    if (serviceResult.getStatus() == ResultConstants.OPERATION_OK)
                    {
                        result.put("token", serviceResult.getReturnValue());
                        LogUtil.info("登录成功");
                    }
                }
            }
            request.getSession().setAttribute("session_user", info);     //将用户信息放入session
        }
        result.putAll(serviceResult.getStatus().getMap());
        LogUtil.info(serviceResult.getStatus().getMessage());
        return result;
    }


    @ResponseBody
    @RequestMapping(value = {"account/register"},method= RequestMethod.POST)
    public JSONObject register(@RequestBody @NotNull Map<String,Object> map,HttpSession httpSession)
            throws InterruptedException
    {
        JSONObject result = new JSONObject();
        Future[] futures=new Future[2];

        String username= (String) map.get("username");
        String password= (String) map.get("password");
        String email= (String) map.get("email");
        String verificationCode=(String)map.get("code");
        LogUtil.info(map.toString());
        if (username == null || password == null || email == null||verificationCode==null)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            LogUtil.info(ResultConstants.OPERATION_INVALID_PARAMETER.getMessage());
            return result;
        }
        futures[0]=asyncTaskService.invoke(()->userInfoService.getUserInfoBasic(UserInfoSQLColumnName.USER_NAME,username));
        futures[1]=asyncTaskService.invoke(()->userInfoService.getUserInfoBasic(UserInfoSQLColumnName.USER_EMAIL,email));
        LogUtil.info("\n/register获得信息:\n"+ map);
        OperationResult<Void> serviceResult=userVerificationService.verifyEmail(httpSession,verificationCode,email);
        if(serviceResult.getStatus()!=ResultConstants.OPERATION_OK)
        {
            result.putAll(serviceResult.getStatus().getMap());
            return result;
        }
        OperationResult<UserInfoBasic> info1=asyncTaskService.getResult(futures[0],1,TimeUnit.SECONDS);
        OperationResult<UserInfoBasic> info2= asyncTaskService.getResult(futures[1],1,TimeUnit.SECONDS);
        if(info1.getStatus()==ResultConstants.OPERATION_OK || info2.getStatus()==ResultConstants.OPERATION_OK)
        {
            result.putAll(ResultConstants.USER_USED_USERNAME_OR_EMAIL.getMap());
            return result;
        }
        serviceResult=userAccountService.register(username,email,password);
        result.putAll(serviceResult.getStatus().getMap());
        LogUtil.info(serviceResult.getStatus().getMessage());
        return result;
    }

    @ResponseBody
    @RequestMapping(value={"account/register/requestVerificationCode"},method= RequestMethod.POST)
    public JSONObject requestVerificationCode(@RequestBody Map<String,Object> map, HttpSession httpSession)
    {
        JSONObject result=new JSONObject();
        String email=(String) map.get("email");
        if(email==null)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        asyncTaskService.invoke(()->userVerificationService.sendVerificationEmail(httpSession,email));
        result.putAll(ResultConstants.OPERATION_OK.getMap());
        return result;
    }


    @ResponseBody
    @RequestMapping("account/logout")
    public JSONObject logout(HttpServletRequest request)
    {
        JSONObject result=new JSONObject();
        request.removeAttribute("userId");
        request.removeAttribute("username");
        result.putAll(ResultConstants.OPERATION_OK.getMap());
        return result;
    }
}

