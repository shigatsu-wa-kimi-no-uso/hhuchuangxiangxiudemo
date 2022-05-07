package team.hhu.chuangxiangxiudemo.article.controller;

import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.dto.CommentCoordinates;
import team.hhu.chuangxiangxiudemo.article.dto.LikesTableCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.CommentInfo;
import team.hhu.chuangxiangxiudemo.article.service.*;
import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.standards.APIParams;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.service.UserInfoService;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;
import team.hhu.chuangxiangxiudemo.utility.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@ResponseBody
@Controller
@RequestMapping("op/article/coordinates/{posterId}/{articleId}/comment")
public class CommentOpController
{
    @Autowired
    private CommentCRUDService commentCRUD;

    @Autowired
    private LikesService likesService;

    @Autowired
    private CommentViewService commentView;

    @Autowired
    private ArticleViewService articleView;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ArticleCRUDService articleCRUD;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @ResponseBody
    @RequestMapping("/post")
    public JSONObject postComment(@PathVariable int posterId, @PathVariable int articleId, HttpServletRequest request, @RequestBody @NotNull Map<String, Object> map) throws IOException
    {
        JSONObject result = new JSONObject();
        System.out.println(map);
        String content=(String) map.get("content");
        if(content==null)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        int userId= (int) request.getAttribute("userId");

        CommentCoordinates cCoordinates=new CommentCoordinates(posterId,articleId);
        ArticleCoordinates aCoordinates=new ArticleCoordinates(posterId,articleId);
        OperationResult serviceResult = commentCRUD.addCommentInfo(cCoordinates,content,userId);
        result.put("commentId",serviceResult.getReturnValue());
        result.put("posterId",userId);
        if(serviceResult.getStatus()==ResultConstants.OPERATION_OK)
        {
            serviceResult=commentCRUD.increaseCommentCount(aCoordinates);
        }
        asyncTaskService.invoke(()->commentView.processCommentWordCloud(cCoordinates.getTableId()));
        result.putAll(serviceResult.getStatus().getMap());
        return result;
    }

    @ResponseBody
    @RequestMapping("/delete")
    public JSONObject deleteComment(@PathVariable int posterId, @PathVariable int articleId, HttpServletRequest request,@RequestBody Map<String,Object> map) throws IllegalAccessException
    {
        JSONObject result=new JSONObject();
        System.out.println(map);
        String[] nParamKeys={"commentId"};
        Map<String,Integer> nParams=StringUtils.parseIntegers(map,nParamKeys);
        if(nParams.size()!=nParamKeys.length)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        int userId= (int) request.getAttribute("userId");
        int commentId=nParams.get("commentId");

        CommentCoordinates cCoordinates=new CommentCoordinates(posterId,articleId,commentId);
        ArticleCoordinates aCoordinates=new ArticleCoordinates(posterId,articleId);
        OperationResult serviceResult = commentCRUD.getTrustedCommentInfo(cCoordinates);
        if(serviceResult.getStatus()==ResultConstants.OPERATION_OK)
        {
            if(((CommentInfo)serviceResult.getReturnValue()).getPosterId()!=userId)
            {
                result.putAll(ResultConstants.USER_ACCESS_DENIED.getMap());
                return result;
            }
            serviceResult = commentCRUD.deleteCommentInfo(cCoordinates);
            if (serviceResult.getStatus() == ResultConstants.OPERATION_OK)
            {
                serviceResult = commentCRUD.decreaseCommentCount(aCoordinates);
            }
        }
        result.putAll(serviceResult.getStatus().getMap());
        return result;
    }



    @ResponseBody
    @RequestMapping("/like")
    public JSONObject onClickLikeButton(@PathVariable int posterId,@PathVariable int articleId, @NotNull HttpServletRequest request, @RequestBody @NotNull Map<String, Object> map)
    {

        int userId=(int) request.getAttribute("userId");
        JSONObject result=new JSONObject();
        System.out.println(map);
        String[] nParamKeys={"commentId","cPosterId"};
        Map<String,Integer> nParams=StringUtils.parseIntegers(map,nParamKeys);
        System.out.println(nParams.size()==nParamKeys.length);
        System.out.println(nParams);
        if(nParams.size()!=nParamKeys.length)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        OperationResult serviceResult;
        int commentId=nParams.get("commentId");
        int commentPosterId=nParams.get("cPosterId");
        CommentCoordinates cCoordinates=new CommentCoordinates(posterId,articleId,commentId);
        LikesTableCoordinates lCoordinates=new LikesTableCoordinates(cCoordinates,userId);
        serviceResult= likesService.checkLikeState(lCoordinates);
        if(serviceResult.getStatus()==ResultConstants.OPERATION_OK)
        {
            int likeState = (int) serviceResult.getReturnValue();
            LogUtil.info("用户id:" + userId + "对此评论的原始点赞状态为:" + likeState);
            switch (likeState)
            {
                case -1:
                    serviceResult = likesService.switchLikeStateToLiked(cCoordinates, commentPosterId, userId);
                    break;
                case 0:
                    serviceResult = likesService.giveLike(cCoordinates, commentPosterId, userId);
                    break;
                case 1:
                    serviceResult = likesService.undoGiveLike(cCoordinates, commentPosterId, userId);
            }
        }
        result.putAll(serviceResult.getStatus().getMap());
        return result;
    }

    @ResponseBody
    @RequestMapping("/bury")
    public JSONObject onClickDislikeButton(@PathVariable int posterId,@PathVariable int articleId, @NotNull HttpServletRequest request, @RequestBody @NotNull Map<String, Object> map)
    {

        int userId=(int) request.getAttribute("userId");
        JSONObject result=new JSONObject();
        System.out.println(map);
        String[] nParamKeys={"commentId","cPosterId"};
        Map<String,Integer> nParams=StringUtils.parseIntegers(map,nParamKeys);
        System.out.println(nParams.size()==nParamKeys.length);
        System.out.println(nParams);
        if(nParams.size()!=nParamKeys.length)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        OperationResult serviceResult;
        int commentId=nParams.get("commentId");
        int commentPosterId=nParams.get("cPosterId");
        CommentCoordinates cCoordinates=new CommentCoordinates(posterId,articleId,commentId);
        LikesTableCoordinates lCoordinates=new LikesTableCoordinates(cCoordinates,userId);
        serviceResult= likesService.checkLikeState(lCoordinates);
        if(serviceResult.getStatus()==ResultConstants.OPERATION_OK)
        {
            int likeState = (int)serviceResult.getReturnValue();
            LogUtil.info("用户id:" + userId + "对此评论的原始点赞状态为:" + likeState);
            switch(likeState)
            {
                case 1:
                    serviceResult = likesService.switchLikeStateToDisliked(cCoordinates, commentPosterId, userId);
                    break;
                case 0:
                    serviceResult = likesService.giveDislike(cCoordinates, userId);
                    break;
                case -1:
                    serviceResult=likesService.undoGiveDislike(cCoordinates,userId);
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
        String[] nParamKeys={APIParams.IN_OUT_ARTICLE_POSTER_ID,APIParams.IN_OUT_ARTICLE_ID,APIParams.IN_OUT_COMMENT_ID};
        Map<String,Integer> nParams=StringUtils.parseIntegers(map,nParamKeys);
        if(nParams.size()!=nParamKeys.length)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        int posterId=nParams.get("posterId");
        int articleId=nParams.get("articleId");
        int commentId=nParams.get("commentId");

        OperationResult<Integer> servRes= likesService.checkLikeState(new LikesTableCoordinates(new CommentCoordinates(posterId,articleId,commentId),userId));

        if(servRes.getStatus()==ResultConstants.OPERATION_OK)
        {
            result.put("liked",servRes.getReturnValue());
        }

        result.putAll(servRes.getStatus().getMap());
        return result;
    }


}
