package team.hhu.chuangxiangxiudemo.article.controller;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.dto.LikesTableCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.ArticleAtom;
import team.hhu.chuangxiangxiudemo.article.service.ArticleCRUDService;
import team.hhu.chuangxiangxiudemo.article.service.ArticleViewService;
import team.hhu.chuangxiangxiudemo.article.service.CommentCRUDService;
import team.hhu.chuangxiangxiudemo.article.service.LikesService;
import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.user.service.UserInfoService;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;
import team.hhu.chuangxiangxiudemo.utility.StringUtils;

/*
特别注意： JSONObject和JSONArray需要引入同一个包
*/
import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@ResponseBody
@Controller
@Slf4j
@RequestMapping("op/article")
public class ArticleOpController
{
    @Autowired
    private ArticleViewService articleView;

    @Autowired
    private LikesService likesService;

    @Autowired
    private ArticleCRUDService articleCRUD;

    @Autowired
    private CommentCRUDService commentCRUD;

    @Autowired
    private AsyncTaskService asyncTaskService;




    @ResponseBody
    @RequestMapping("/like")
    @Contract(value="_,null->fail")
    public JSONObject onClickLikeButton(@NotNull HttpServletRequest request,@RequestBody Map<String, Object> map)
    {
        int userId=(int) request.getAttribute("userId");
        JSONObject result=new JSONObject();
        System.out.println(map);
        String[] nParamKeys={"posterId","articleId"};
        Map<String,Integer> nParams=StringUtils.parseIntegers(map,nParamKeys);
        if(nParams.size()!=nParamKeys.length)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        OperationResult<?> serviceResult;
        int posterId=nParams.get("posterId");
        int articleId=nParams.get("articleId");


        ArticleCoordinates aCoordinates=new ArticleCoordinates(posterId,articleId);
        LikesTableCoordinates lCoordinates=new LikesTableCoordinates(posterId,articleId,userId);
        serviceResult= likesService.checkLikeState(lCoordinates);
        if(serviceResult.getStatus()==ResultConstants.OPERATION_OK)
        {
            boolean liked = (boolean) serviceResult.getReturnValue();
            LogUtil.info("用户id:" + userId + "对此文章的原始点赞状态为:" + liked);
            if (!liked)
            {
                serviceResult=likesService.giveLike(aCoordinates,userId);
            }
            else
            {
                serviceResult=likesService.undoGiveLike(aCoordinates,userId);
            }
        }
        result.putAll(serviceResult.getStatus().getMap());
        return result;

    }

    @ResponseBody
    @RequestMapping("/checklike")
    @Contract(value="_,null->fail")
    public JSONObject checkLikeState(@NotNull HttpServletRequest request, @RequestBody Map<String, Object> map)
    {
        int userId=(int) request.getAttribute("userId");
        JSONObject result=new JSONObject();
        System.out.println(map);
        String[] nParamKeys={"posterId","articleId"};
        Map<String,Integer> nParams=StringUtils.parseIntegers(map,nParamKeys);
        if(nParams.size()!=nParamKeys.length)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        int posterId=nParams.get("posterId");
        int articleId=nParams.get("articleId");

        OperationResult<Integer> servRes= likesService.checkLikeState(new LikesTableCoordinates(posterId,articleId,userId));
        if(servRes.getStatus()==ResultConstants.OPERATION_OK)
        {
            result.put("likeState",servRes.getReturnValue());
        }
        result.putAll(servRes.getStatus().getMap());
        return result;
    }

    @ResponseBody
    @RequestMapping("/delete")
    public JSONObject deleteArticle(HttpServletRequest request,@RequestBody Map<String,Object> map)
            throws Exception
    {
        int userId=(int)request.getAttribute("userId");
        JSONObject result=new JSONObject();
        String[] nParamKeys={"articleId"};
        Map<String,Integer> nParams=StringUtils.parseIntegers(map,nParamKeys);
        if(nParams.size()!=nParamKeys.length)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        int articleId=nParams.get("articleId");
        ArticleCoordinates coordinates=new ArticleCoordinates(userId,articleId);
        OperationResult<ArticleAtom> servRes=articleCRUD.getSingleArticleAtom(coordinates);
        if(servRes.getStatus()==ResultConstants.OPERATION_OK)
        {
            if(servRes.getReturnValue().getPosterId()!=userId)
            {
                result.putAll(ResultConstants.USER_ACCESS_DENIED.getMap());
                return result;
            }
        }
        OperationResult<Void> result1=articleCRUD.deleteArticle(coordinates);
        if(result1.getStatus()==ResultConstants.OPERATION_OK)
        {
            asyncTaskService.invoke(()->commentCRUD.deleteCommentList(coordinates.getCommentTableId()));
            asyncTaskService.invoke(()->likesService.batchDeleteLikesList(coordinates.getLikesTableIdPrefix()));
        }
        result.putAll(result1.getStatus().getMap());
        return result;
    }

}
