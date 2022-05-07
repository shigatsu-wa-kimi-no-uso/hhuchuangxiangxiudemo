package team.hhu.chuangxiangxiudemo.user.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.ArticleAtom;
import team.hhu.chuangxiangxiudemo.article.service.ArticleCRUDService;
import team.hhu.chuangxiangxiudemo.article.service.ArticleViewService;
import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.standards.APIParams;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.constant.UserInfoSQLColumnName;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoBasic;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoEx;
import team.hhu.chuangxiangxiudemo.user.service.UserInfoService;
import team.hhu.chuangxiangxiudemo.user.service.UserVerificationService;
import team.hhu.chuangxiangxiudemo.utility.Converter;
import team.hhu.chuangxiangxiudemo.utility.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class UserHomePageController
{
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private UserVerificationService userVerificationService;

    @Autowired
    private ArticleCRUDService articleCRUD;

    @Autowired
    private ArticleViewService articleViewService;

    @ResponseBody
    @GetMapping(value = {"user/{userId}/get"})
    public JSONObject getUserProfile(@PathVariable int userId) throws IllegalAccessException
    {
        JSONObject result=new JSONObject();
        OperationResult<UserInfoEx> servRes=userInfoService.getUserInfoExById(userId);
        if(servRes.getStatus()== ResultConstants.OPERATION_OK)
        {
            UserInfoEx info=servRes.getReturnValue();
            result.putAll(Converter.objectToMap(info));
        }
        result.putAll(servRes.getStatus().getMap());
        return result;
    }

    /**
     * 用户信息获取
     */

    @ResponseBody
    @RequestMapping(value={"op/account"})
    public JSONObject userAccountManager(HttpServletRequest request) throws IllegalAccessException
    {
        long start=System.currentTimeMillis();
        JSONObject result=new JSONObject();
        int userId=(int)request.getAttribute("userId");
        Future<OperationResult<UserInfoBasic>> servRes1;
        Future<OperationResult<UserInfoEx>> servRes2;

        servRes1=asyncTaskService.invoke(()->userInfoService.getUserInfoBasic(UserInfoSQLColumnName.USER_ID, String.valueOf(userId)));
        servRes2=asyncTaskService.invoke(()->userInfoService.getUserInfoExById(userId));
        OperationResult<UserInfoBasic> res1=asyncTaskService.getResult(servRes1,1, TimeUnit.SECONDS);
        OperationResult<UserInfoEx> res2=asyncTaskService.getResult(servRes2,1,TimeUnit.SECONDS);
        /*
        OperationResult<UserInfoBasic> res1=userInfoService.getUserInfoBasic(UserInfoSQLColumnName.USER_ID,String.valueOf(userId));
        OperationResult<UserInfoEx> res2=userInfoService.getUserInfoExById(userId);*/
        if(res1.getStatus()==ResultConstants.OPERATION_OK)
        {
            result.putAll(Converter.objectToMap(res1.getReturnValue()));
            result.remove("userPassword");
            result.remove("userEmail");
        }
        result.putAll(res1.getStatus().getMap());
        if(res2.getStatus()==ResultConstants.OPERATION_OK)
        {
            result.putAll(Converter.objectToMap(res2.getReturnValue()));
        }
        result.putAll(res2.getStatus().getMap());
        long end=System.currentTimeMillis();
        log.info("用时："+(end-start));
        return result;

    }




    @ResponseBody
    @RequestMapping("op/getArticleList")
    public JSONArray getArticleList(HttpServletRequest request,@RequestBody Map<String,Object> map)
            throws IllegalAccessException
    {
        long start = System.currentTimeMillis();
        int userId=(int)request.getAttribute("userId");
        JSONObject result = new JSONObject();
        JSONArray resultArr = new JSONArray();
        String nParamKeys[] = {APIParams.IN_SELECTNUMBER};
        Map<String, Integer> nParams = StringUtils.parseIntegers(map, nParamKeys);
        String timestr = (String) map.get(APIParams.IN_SELECTTIME);
        System.out.println(timestr);
        if (nParams.size() != nParamKeys.length || timestr == null)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            resultArr.add(result);
            return resultArr;
        }
        Timestamp time = Timestamp.valueOf(timestr);
        int selectNumber = nParams.get("number");

        OperationResult<List<ArticleAtom>> serviceResult = articleCRUD.getLastPostedArticleAtomsByPoster(userId, time, selectNumber);

        List<ArticleAtom> list = serviceResult.getReturnValue();


        if (serviceResult.getStatus() == ResultConstants.OPERATION_OK)
        {
            result.putAll(ResultConstants.OPERATION_OK.getMap());
            result.put("count", list.size());
            resultArr.add(result);
            for (ArticleAtom i: list)
            {
                result = new JSONObject();//避免重复引用对象，使用新对象存储，防止出现$ref":"$[0]的问题
                String url = articleViewService.generateArticleRequestURL(new ArticleCoordinates(userId,i.getArticleId()));
                result.putAll(Converter.objectToMap(i));
                result.put("url", url);
                resultArr.add(result);
            }
        }
        else
        {
            result.putAll(serviceResult.getStatus().getMap());
            result.put("count", 0);
            resultArr.add(result);
            result.clear();
        }

        long end = System.currentTimeMillis();
        log.info("用时:" + (end - start));
        /*
        特别注意： JSONObject和JSONArray需要引入同一个包
        否则JSONArray加入JSONObject后为空
        */
        return resultArr;
    }




    @ResponseBody
    @RequestMapping(value={"op/account/requestVerificationCode"},method= RequestMethod.POST)
    public JSONObject requestVerificationCode(HttpServletRequest request, HttpSession httpSession)
    {
        JSONObject result=new JSONObject();
        int userId=(int)request.getAttribute("userId");
        OperationResult<UserInfoBasic> servRes=userInfoService.getUserInfoBasic(UserInfoSQLColumnName.USER_ID, String.valueOf(userId));
        if(servRes.getStatus()==ResultConstants.OPERATION_OK)
        {
            asyncTaskService.invoke(() -> userVerificationService.sendVerificationEmail(httpSession, servRes.getReturnValue().getUserEmail()));
        }
        result.putAll(ResultConstants.OPERATION_OK.getMap());
        return result;
    }


}
