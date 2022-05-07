package team.hhu.chuangxiangxiudemo.article.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import team.hhu.chuangxiangxiudemo.article.dto.CommentCoordinates;
import team.hhu.chuangxiangxiudemo.article.dto.LikesTableCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.CommentInfo;
import team.hhu.chuangxiangxiudemo.article.service.CommentCRUDService;
import team.hhu.chuangxiangxiudemo.article.service.CommentViewService;
import team.hhu.chuangxiangxiudemo.article.service.LikesService;
import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.service.PathService;
import team.hhu.chuangxiangxiudemo.standards.APIParams;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.service.UserInfoService;
import team.hhu.chuangxiangxiudemo.utility.Converter;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;
import team.hhu.chuangxiangxiudemo.utility.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Controller
@ResponseBody
@Slf4j
@RequestMapping("article/coordinates/{posterId}/{articleId}")
public class CommentViewController
{

    @Autowired
    private CommentCRUDService commentCRUD;

    @Autowired
    private CommentViewService commentView;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private LikesService likesService;


    /**
     *测试获取100条评论，不使用多线程优化，平均用时805ms
     * 多线程优化后，最快用时208ms
     */
    @ResponseBody
    @RequestMapping("/getcomment")
    public JSONArray getCommentListOrderedByPostDate(@NotNull HttpServletRequest request, @PathVariable int posterId, @PathVariable int articleId, @RequestBody @NotNull Map<String, Object> map)
            throws IllegalAccessException
    {
        long start=System.currentTimeMillis();
        int userId=-1;
        if(request.getAttribute("userId")!=null)
        {
            userId=(int)request.getAttribute("userId");
        }
        JSONArray resultArr = new JSONArray();
        JSONObject result = new JSONObject();
        String[] nParamKeys={APIParams.IN_SELECTNUMBER};
        Map<String,Integer> nParam=StringUtils.parseIntegers(map,nParamKeys);
        String strTime=(String) map.get(APIParams.IN_SELECTTIME);
        if(nParam.size()!=nParamKeys.length|| strTime==null)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            resultArr.add(result);
            return resultArr;
        }
        Timestamp time= Timestamp.valueOf((String) map.get(APIParams.IN_SELECTTIME));
        int selectNumber=nParam.get(APIParams.IN_SELECTNUMBER);
        CommentCoordinates cCoordinates=new CommentCoordinates(posterId,articleId);
        OperationResult serviceResult = commentCRUD.getCommentInfoBeforeTime(cCoordinates,time,selectNumber);
        List<CommentInfo> list=(List<CommentInfo>) serviceResult.getReturnValue();
        LogUtil.info(list.toString());
        int listSize=list.size();
        Future<OperationResult<String>>[] futures=new Future[listSize];
        Future<OperationResult<Integer>>[] futures2=new Future[listSize];
        Future<OperationResult<String>>[] futures3=new Future[listSize];
        if(serviceResult.getStatus() == ResultConstants.OPERATION_OK)
        {
            result.putAll(serviceResult.getStatus().getMap());
            result.put("count",list.size());
            resultArr.add(result);
            int index=0;
            if(userId==-1)
            {
                for (CommentInfo i : list)
                {
                    futures[index] = asyncTaskService.invoke(() -> userInfoService.getUserAliasById(i.getPosterId()));
                    futures3[index++]=asyncTaskService.invoke(()->userInfoService.getUserAvatarById(i.getPosterId()));
                }
            }
            else for (CommentInfo i:list)
            {
                futures[index] = asyncTaskService.invoke(() -> userInfoService.getUserAliasById(i.getPosterId()));
                cCoordinates.setCommentId(i.getCommentId());
                LikesTableCoordinates lCoordinates=new LikesTableCoordinates(cCoordinates,userId);
                futures2[index]=asyncTaskService.invoke(()->likesService.checkLikeState(lCoordinates));
                futures3[index++]=asyncTaskService.invoke(()->userInfoService.getUserAvatarById(i.getPosterId()));
            }
            index=0;
            OperationResult<String> serviceResult3;
            PathService pathService=new PathService();
            String defaultAvatarUrl=pathService.generateAvatarPath("default.jpg");
            if(userId!=-1)
            {
                OperationResult<Integer> serviceResult2;
                for (CommentInfo i : list)
                {
                    result = new JSONObject();
                    result.putAll(Converter.objectToMap(i));
                    serviceResult = asyncTaskService.getResult(futures[index], 1, TimeUnit.SECONDS);
                    if (serviceResult.getStatus() == ResultConstants.OPERATION_OK)
                    {
                        result.put("posterAlias", serviceResult.getReturnValue());
                    }
                    else
                        result.put("posterAlias", "获取用户名失败");

                    if (i.getPosterId() == userId)
                    {
                        result.put("isMyComment", 1);
                    }
                    else
                    {
                        result.put("isMyComment",0);
                    }

                    serviceResult2=asyncTaskService.getResult(futures2[index],1,TimeUnit.SECONDS);
                    if(serviceResult2.getStatus()==ResultConstants.OPERATION_OK)
                    {
                        result.put("likeState",serviceResult2.getReturnValue());
                        System.out.println(serviceResult2.getReturnValue());
                    }
                    else
                        result.put("likeState",0);
                    serviceResult3=asyncTaskService.getResult(futures3[index++],1,TimeUnit.SECONDS );
                    if(serviceResult3.getStatus()==ResultConstants.OPERATION_OK)
                    {
                        if(serviceResult3.getReturnValue()!=null)
                        {
                            result.put("avatarUrl", pathService.generateAvatarPath(serviceResult3.getReturnValue()));
                        }
                        else
                        {
                            result.put("avatarUrl",defaultAvatarUrl);
                        }
                    }

                    else result.put("avatarUrl",defaultAvatarUrl);
                    resultArr.add(result);
                }
            }
            else
            {
                for (CommentInfo i : list)
                {
                    result = new JSONObject();
                    result.putAll(Converter.objectToMap(i));
                    serviceResult = asyncTaskService.getResult(futures[index], 1, TimeUnit.SECONDS);
                    if (serviceResult.getStatus() == ResultConstants.OPERATION_OK)
                    {
                        result.put("posterAlias", serviceResult.getReturnValue());
                    }
                    else
                        result.put("posterAlias", "获取用户名失败");
                    serviceResult3=asyncTaskService.getResult(futures3[index++],1,TimeUnit.SECONDS );
                    if(serviceResult3.getStatus()==ResultConstants.OPERATION_OK)
                    {
                        if(serviceResult3.getReturnValue()!=null)
                        {
                            result.put("avatarUrl", pathService.generateAvatarPath(serviceResult3.getReturnValue()));
                        }
                        else result.put("avatarUrl",defaultAvatarUrl);
                    }
                    else result.put("avatarUrl",defaultAvatarUrl);
                    result.put("isMyComment", 0);
                    result.put("liked",false);
                    resultArr.add(result);
                }
            }
        }
        else
        {
            result.put("count",0);
            resultArr.add(result);
        }
        long end=System.currentTimeMillis();
        log.info("用时："+(end-start));
        /*
        特别注意： JSONObject和JSONArray需要引入同一个包
        否则JSONArray加入JSONObject后为空
        */
        return resultArr;
    }
}
