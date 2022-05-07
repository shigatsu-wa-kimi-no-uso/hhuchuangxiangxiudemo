package team.hhu.chuangxiangxiudemo.article.controller;

import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.service.ArticleCRUDService;
import team.hhu.chuangxiangxiudemo.article.service.ArticleViewService;
import team.hhu.chuangxiangxiudemo.article.service.IOService;
import team.hhu.chuangxiangxiudemo.configuration.SourcePathConfig;
import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.service.PathService;
import team.hhu.chuangxiangxiudemo.standards.APIParams;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.service.UserInfoService;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;
import team.hhu.chuangxiangxiudemo.utility.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@Controller
public class MarkdownEditorController
{

    /*
    * 有跳转到templates下的页面时,类上不能有@ResponseBody
     */

    @Autowired
    ArticleCRUDService articleCRUD;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    ArticleViewService articleView;

    @Autowired
    AsyncTaskService asyncTaskService;


    @GetMapping("article/edit")
    public String editor()
    {
        return "/article";
    }


    @ResponseBody
    @RequestMapping("article/uploadimg")
    public JSONObject uploadImg(@RequestParam(value="editormd-image-file",required=false) @NotNull MultipartFile imgFile)
            throws IOException
    {
        byte[] data=imgFile.getBytes();
        IOService service=new IOService();
        OperationResult<String> serviceResult=service.storeIMG(data, SourcePathConfig.SRC_IMAGE_DISKPATH);
        JSONObject result=new JSONObject();

        if(serviceResult.getStatus()== ResultConstants.OPERATION_OK)
        {
            PathService serv=new PathService();
            String url=serv.generateImgPath(serviceResult.getReturnValue());
            result.put("url",url);
        }
        result.putAll(serviceResult.getStatus().getMap());
        return result;
    }



    @ResponseBody
    @RequestMapping("op/article/post")
    public JSONObject postArticle(HttpServletRequest request, @RequestBody Map<String, Object> map)
            throws IOException
    {
        JSONObject result=new JSONObject();
        String title=(String) map.get(APIParams.IN_OUT_ARTICLE_TITLE);
        String nParamKeys[]={APIParams.IN_OUT_ARTICLE_CATEGORY_ID};
        String content= (String) map.get(APIParams.IN_OUT_ARTICLE_CONTENT);
        System.out.println(map);
        Map<String,Integer> nParams=StringUtils.parseIntegers(map,nParamKeys);
        if(nParams.size()!=nParamKeys.length || title==null||content==null)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
            return result;
        }
        LogUtil.info("获得信息:"+map);
        int userId=(int)request.getAttribute("userId");
        int categoryId=nParams.get(APIParams.IN_OUT_ARTICLE_CATEGORY_ID);
        OperationResult servRes;
        servRes=articleCRUD.addArticleInfo(userId,categoryId,title);
        if(servRes.getStatus()==ResultConstants.OPERATION_OK)
        {
            if(servRes.getReturnValue()!=null)
            {
                int articleId= (int) servRes.getReturnValue();
                result.put("posterId",userId);
                result.put("articleId",articleId);
                ArticleCoordinates coordinates=new ArticleCoordinates(userId,articleId);
                servRes=articleCRUD.storeArticleContent(new ArticleCoordinates(userId,articleId),content);
                if(servRes.getStatus()==ResultConstants.OPERATION_OK)
                {
                    String url=articleView.generateArticleContentURLForAPI(coordinates);
                    asyncTaskService.invoke(() -> articleView.processArticleContent(url, coordinates));
                }
            }
        }
        result.putAll(servRes.getStatus().getMap());
        return result;
    }

    @ResponseBody
    @RequestMapping("article/getclassification")
    public JSONObject getClassification(@RequestBody Map<String, Object> map)
    {
        String title=(String) map.get(APIParams.IN_OUT_ARTICLE_TITLE);
        JSONObject result=new JSONObject();
        if(title==null)
        {
            result.putAll(ResultConstants.OPERATION_INVALID_PARAMETER.getMap());
        }
        OperationResult<Integer> servRes=articleView.getClassificationId(title);
        if(servRes.getStatus()==ResultConstants.OPERATION_OK)
        {
            result.put("categoryId",servRes.getReturnValue());
        }
        result.putAll(servRes.getStatus().getMap());
        return result;
    }

}
