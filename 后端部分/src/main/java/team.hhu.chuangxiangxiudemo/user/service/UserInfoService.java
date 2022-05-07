package team.hhu.chuangxiangxiudemo.user.service;

import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.hhu.chuangxiangxiudemo.configuration.SqlParametersConfig;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.dao.IUserInfoDao;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoBasic;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoEx;
import team.hhu.chuangxiangxiudemo.utility.DataAccessExceptionHandler;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;

import javax.annotation.Resource;


@Service
@ComponentScan
@Resource
public class UserInfoService
{
    @Autowired
    private IUserInfoDao userMapper;

    /**
     *CREATE
     */
    @Contract(pure=false)
    protected <T> Void _createUserInfo(T info) throws DataAccessException
    {
        userMapper.createUserInfo(info.getClass().getSimpleName(), info, SqlParametersConfig.SQLPARAMETER_ENCRYPTKEY);
        return null;
    }

    protected Void _createUserArticleTable(int userId)
    {
        userMapper.createUserArticleTable(userId);
        return null;
    }

    /**
     *RETRIEVE
     */


    private UserInfoEx _retrieveUserInfoEx(int userId) throws DataAccessException
    {
        return userMapper.retrieveUserInfoEx(userId);
    }

    private UserInfoBasic _retrieveUserInfoBasic(String byWhat, String keyVal) throws DataAccessException
    {
        return userMapper.retrieveUserInfoBasic(byWhat, keyVal, SqlParametersConfig.SQLPARAMETER_ENCRYPTKEY);
    }


    public OperationResult<UserInfoBasic> getUserInfoBasic(String byWhat,String keyVal)
    {
        OperationResult<UserInfoBasic> result=new OperationResult<>();

        DataAccessExceptionHandler.invoke(result,()-> _retrieveUserInfoBasic(byWhat, keyVal),false);

        UserInfoBasic userInfoBasic=result.getReturnValue();
        if(result.getStatus()!=ResultConstants.OPERATION_OK)
        {
            LogUtil.info("getUserInfoBasic数据访问异常 通过"+byWhat+":"+keyVal);
            return result;
        }

        if(userInfoBasic==null)
        {
            result.setStatus(ResultConstants.USER_ACCOUNT_NOT_EXISTS);
            LogUtil.info("获取UserInfoBasic信息失败 通过"+byWhat+":"+keyVal);
            LogUtil.info("原因:userInfoBasic为null");
        }
        else
        {
            LogUtil.info("获取UserInfoBasic信息成功 通过"+byWhat+":"+keyVal);
            LogUtil.info("\nUserInfoBasic信息:\n"+userInfoBasic);
            result.setStatus(ResultConstants.OPERATION_OK);
            result.setReturnValue(userInfoBasic);
        }
        return result;
    }

    /**
     *查询的用户不存在时，返回ResultConstants.USER_ACCOUNT_NOT_EXISTS
     */
    public OperationResult<UserInfoEx> getUserInfoExById(int userId)
    {
        OperationResult<UserInfoEx> result=new OperationResult<>();

        DataAccessExceptionHandler.invoke(result,()-> _retrieveUserInfoEx(userId),false);
        if(result.getStatus()!=ResultConstants.OPERATION_OK)
        {
            LogUtil.error("数据访问异常");
            return result;
        }

        UserInfoEx userInfoEx=result.getReturnValue();

        if(userInfoEx==null)
        {
            result.setStatus(ResultConstants.USER_ACCOUNT_NOT_EXISTS);
            LogUtil.info("获取UserInfoEx信息失败 通过userId:"+userId);
        }
        else
        {
            result.setStatus(ResultConstants.OPERATION_OK);
            LogUtil.info("获取UserInfoEx信息成功 通过userId:"+userId);
            LogUtil.info("\nUserInfoEx信息:\n"+userInfoEx);
            result.setReturnValue(userInfoEx);
        }
        return result;
    }



    public OperationResult<String> getUserAliasById(int userId)
    {
        OperationResult<String> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->userMapper.retrieveUserAliasById(userId),false);
        return result;
    }

    public OperationResult<String> getUserAvatarById(int userId)
    {
        OperationResult<String> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->userMapper.retrieveUserAvatarById(userId),false);
        return result;
    }


    /**
     *UPDATE
     */
    private <T> Void _update(int userId, T info) throws DataAccessException
    {
        userMapper.updateUserInfo(info.getClass().getSimpleName(), userId, info,SqlParametersConfig.SQLPARAMETER_ENCRYPTKEY);
        return null;
    }

    protected Void _updateUserPassword(int userId,String password)
    {
        userMapper.updateUserPassword(userId,password,SqlParametersConfig.SQLPARAMETER_ENCRYPTKEY);
        return null;
    }

    protected Void _updateUserName(int userId,String username)
    {
        userMapper.updateUserName(userId,username,SqlParametersConfig.SQLPARAMETER_ENCRYPTKEY);
        return null;
    }


    protected Void _updateUserAvatar(int userId,String avatar)
    {
        userMapper.updateUserAvatar(userId,avatar);
        return null;
    }

    @Transactional
    public OperationResult<Void> updateUserAvatar(int userId, String avatar)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_updateUserAvatar(userId,avatar),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> updateUserPassword(int userId,String password)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_updateUserPassword(userId,password),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> updateUserInfoBasic(int userId,UserInfoBasic info)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _update(userId, info),true);
        if(result.getStatus()==ResultConstants.OPERATION_OK)
        {
            LogUtil.info("更新UserInfoBasic信息成功 通过userId:" + userId);
            LogUtil.info("\nUserInfoBasic信息:\n" + info);
        }
        return result;

    }

    public OperationResult<Void> updateUserInfoEx(int userId,UserInfoEx info)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _update(userId, info),true);
        if(result.getStatus()==ResultConstants.OPERATION_OK)
        {
            LogUtil.info("更新UserInfoEx信息成功 通过userId:" + userId);
            LogUtil.info("\nUserInfoEx信息:\n" + info);
        }
        return result;
    }




    /**
     * DELETE
     */

    protected Void _delete(int userId) throws DataAccessException
    {
        userMapper.deleteUserInfo(userId);
        return null;
    }

    public OperationResult<Void> deleteUser(int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _delete(userId),true);
        return result;
    }


}
