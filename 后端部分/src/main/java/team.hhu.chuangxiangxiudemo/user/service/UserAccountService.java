package team.hhu.chuangxiangxiudemo.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoBasic;
import team.hhu.chuangxiangxiudemo.user.constant.UserInfoSQLColumnName;
import team.hhu.chuangxiangxiudemo.utility.DataAccessExceptionHandler;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;

import javax.annotation.Resource;
import java.io.IOException;

@Service
@Component
@ComponentScan
@Resource
public class UserAccountService
{
    @Autowired
    private UserInfoService userInfoService;

    public OperationResult<UserInfoBasic> login(String userName, String password) throws IOException
    {

        System.out.println("get:"+userName+" "+password);
        OperationResult<UserInfoBasic> result=userInfoService.getUserInfoBasic(UserInfoSQLColumnName.USER_NAME,userName);

        if(result.getStatus()== ResultConstants.USER_ACCOUNT_NOT_EXISTS)
        {
            LogUtil.info("登录失败"+ ResultConstants.USER_ACCOUNT_NOT_EXISTS.getMessage());
            return result;
        }
        UserInfoBasic userInfoBasic=result.getReturnValue();

        if(!password.equals(userInfoBasic.getUserPassword()))
        {
            LogUtil.info("登录失败"+ ResultConstants.USER_LOGIN_WRONG_PASSWORD.getMessage());
            result.setStatus(ResultConstants.USER_LOGIN_WRONG_PASSWORD);
            return result;
        }
        result.setStatus(ResultConstants.OPERATION_OK);
        result.setReturnValue(userInfoBasic);
        LogUtil.info("登录成功");
        return result;
    }

    @Transactional
    public OperationResult<Void> register(String userName,String email,String password)
    {
        UserInfoBasic userInfoBasic=new UserInfoBasic(userName,email,password);
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->userInfoService._createUserInfo(userInfoBasic),true);
        if(result.getStatus()==ResultConstants.OPERATION_OK)
        {
            DataAccessExceptionHandler.invoke(result,()->userInfoService._createUserArticleTable(userInfoBasic.getUserId()),true);
        }
        return result;
    }

    public OperationResult<Void> deleteAccount(int userId) throws IOException
    {
        return userInfoService.deleteUser(userId);
    }

    @Transactional
    public OperationResult<Void> updateUserName(int userId,String userName)
    {
        OperationResult<UserInfoBasic> serviceResult=userInfoService.getUserInfoBasic(UserInfoSQLColumnName.USER_NAME,userName);
        OperationResult<Void> result=new OperationResult<>();
        if(serviceResult.getStatus()==ResultConstants.USER_ACCOUNT_NOT_EXISTS)
        {
            DataAccessExceptionHandler.invoke(result,()->userInfoService._updateUserName(userId,userName),true);
            return result;
        }

        if(serviceResult.getReturnValue().getUserId()==userId)
        {
            result.setStatus(ResultConstants.OPERATION_OK);
        }
        else
        {
            if(serviceResult.getStatus()==ResultConstants.OPERATION_OK)
            {
                result.setStatus(ResultConstants.USER_USED_USERNAME);
            }
            else
                result.setStatus(serviceResult.getStatus());
        }
        return result;
    }

    @Transactional
    public OperationResult<Void> updatePassword(int userId,String password)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->userInfoService._updateUserPassword(userId,password),true);
        return result;
    }
}
