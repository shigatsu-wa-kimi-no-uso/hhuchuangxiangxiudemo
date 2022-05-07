package team.hhu.chuangxiangxiudemo.user.controller;

import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.hhu.chuangxiangxiudemo.article.service.IOService;
import team.hhu.chuangxiangxiudemo.configuration.SourcePathConfig;
import team.hhu.chuangxiangxiudemo.user.constant.UserInfoSQLColumnName;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoBasic;
import team.hhu.chuangxiangxiudemo.user.service.UserVerificationService;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoEx;
import team.hhu.chuangxiangxiudemo.user.service.UserAccountService;
import team.hhu.chuangxiangxiudemo.user.service.UserInfoService;
import team.hhu.chuangxiangxiudemo.utility.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping(value="/op/account")
public class UserProfileController
{
    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private  UserInfoService userInfoService;

    @Autowired
    private UserVerificationService userVerificationService;

    @ResponseBody
    @RequestMapping(value={"/updateusername"},method= RequestMethod.POST)
    public JSONObject updateUserName(HttpServletRequest request,@RequestBody Map<String,Object> map)
    {
        JSONObject result=new JSONObject();
        String username=(String) map.get("username");
        String userId=(String) request.getAttribute("userId");

        if(username==null)
        {
            result.putAll(ResultConstants.USER_INVALID_USERNAME.getMap());
            return result;
        }
        if(userId==null)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        OperationResult<Void> serviceResult=userAccountService.updateUserName(Integer.parseInt(userId),username);
        result.putAll(serviceResult.getStatus().getMap());
        return result;
    }

    @ResponseBody
    @RequestMapping(value = {"/updatepassword"})
    public JSONObject updatePassword(HttpServletRequest request, HttpSession session, @RequestBody Map<String,Object> map)
    {
        JSONObject result=new JSONObject();
        String password=(String) map.get("password");
        String newPassword=(String) map.get("newPassword");
        int userId=(int)request.getAttribute("userId");
        String[] nParamKey={"code"};
        Map<String,Integer> nParam=StringUtils.parseIntegers(map,nParamKey);
        if(nParam.size()!=nParamKey.length)
        {
            result.putAll(ResultConstants.USER_VERIFICATION_INVALID_CODE.getMap());
            return result;
        }
        int code = nParam.get("code");

        OperationResult servRes=userInfoService.getUserInfoBasic(UserInfoSQLColumnName.USER_ID, String.valueOf(userId));
        if(servRes.getStatus()==ResultConstants.OPERATION_OK)
        {
            if(!password.equals(((UserInfoBasic)servRes.getReturnValue()).getUserPassword()))
            {
                result.putAll(ResultConstants.USER_LOGIN_WRONG_PASSWORD.getMap());
                return result;
            }
            servRes = userVerificationService.verifyEmail(session, String.valueOf(code), ((UserInfoBasic)servRes.getReturnValue()).getUserEmail());
            if(servRes.getStatus()==ResultConstants.OPERATION_OK)
            {
                servRes=userAccountService.updatePassword(userId,newPassword);
            }
        }
        result.putAll(servRes.getStatus().getMap());
        return result;
    }

    /**
     *用户扩展信息更新
     */

    @ResponseBody
    @RequestMapping(value = {"/updateEx"},method=RequestMethod.POST)
    public JSONObject updateEx(HttpServletRequest request, @RequestBody Map<String,Object> map)
    {
        int userId=(int)request.getAttribute("userId");
        JSONObject result=new JSONObject();
        UserInfoEx newInfo=new UserInfoEx();
        newInfo.setAlias((String)map.get("alias"));
        String[] nParamKey={"age"};
        Map<String,Integer> nParam=StringUtils.parseIntegers(map,nParamKey);
        if(nParam.size()!=nParamKey.length)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        newInfo.setAge(nParam.get("age"));

        newInfo.setGender((String) map.get("gender"));
        System.out.println(map.get("signature"));
        newInfo.setSignature((String) map.get("signature"));
        System.out.println(newInfo);
        OperationResult<Void> servRes=userInfoService.updateUserInfoEx(userId,newInfo);
        result.putAll(servRes.getStatus().getMap());
        return result;
    }


    /**
     *头像上传接口
     */

    @ResponseBody
    @RequestMapping(value = "/uploadAvatar",method = RequestMethod.POST)
    public JSONObject uploadAvatar(HttpServletRequest request,@RequestParam(value="img",required=false) @NotNull MultipartFile imgFile)
            throws IOException
    {
        int userId=(int)request.getAttribute("userId");
        byte[] data=imgFile.getBytes();
        IOService service=new IOService();
        System.out.println(imgFile.getBytes());
        System.out.println(imgFile.getName());
        System.out.println(imgFile.getBytes().length);
        System.out.println(imgFile.getSize());
        System.out.println(imgFile);
        OperationResult serviceResult=service.storeIMG(data, SourcePathConfig.SRC_AVATAR_DISKPATH);     //返回值：以md5命名的文件名（含后缀名）
        JSONObject result=new JSONObject();
        String name=(String) serviceResult.getReturnValue();
        if(serviceResult.getStatus()== ResultConstants.OPERATION_OK)
        {
            serviceResult=userInfoService.updateUserAvatar(userId,(String) serviceResult.getReturnValue());
            if(serviceResult.getStatus()==ResultConstants.OPERATION_OK)
            {
                result.put("avatar",name);
            }
        }
        result.putAll(serviceResult.getStatus().getMap());
        return result;
    }

}
