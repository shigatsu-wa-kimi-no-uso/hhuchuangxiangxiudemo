package team.hhu.chuangxiangxiudemo.article.service;

import com.alibaba.fastjson.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.ArticleEx;
import team.hhu.chuangxiangxiudemo.configuration.ExternalAPIConfig;
import team.hhu.chuangxiangxiudemo.configuration.RemoteSrcPathConfig;
import team.hhu.chuangxiangxiudemo.configuration.SourcePathConfig;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.utility.StringUtils;


@Service
public class ArticleViewService
{
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ArticleCRUDService articleCRUD;


    /**
     *
     */

    public OperationResult<Integer> getClassificationId(String title)
    {
        HttpHeaders httpHeaders = new HttpHeaders();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("title",title);
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, httpHeaders);
        String apiResult = restTemplate.postForObject(ExternalAPIConfig.API_GET_CLASSIFICATION,entity,String.class);
        System.out.println(apiResult);
        OperationResult<Integer> result=new OperationResult<>();
        if(StringUtils.isNumeric(apiResult))
        {
            result.setStatus(ResultConstants.OPERATION_OK);
            assert apiResult != null;
            result.setReturnValue(Integer.parseInt(apiResult));
        }
        else
        {
            result.setStatus(ResultConstants.OPERATION_UNDEFINED_ERROR);
        }
        return result;
    }

    /**
     * 生成文章词云图,摘要,高频词
     */
    public OperationResult<Void> processArticleContent(String url, @NotNull ArticleCoordinates coordinates)
    {
        OperationResult<Void> result=new OperationResult<>();
        HttpHeaders httpHeaders = new HttpHeaders();
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("url",url);
        jsonObject.put("articleId",coordinates.getArticleId());
        jsonObject.put("posterId",coordinates.getPosterId());
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, httpHeaders);
        JSONObject apiResult=null;
        try
        {
            apiResult = restTemplate.postForObject(ExternalAPIConfig.API_PROCESS_ARTICLE_CONTENT, entity, JSONObject.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setStatus(ResultConstants.API_UNEXPECTED_ERROR);
            return result;
        }

        String symbol=apiResult.getString("word");
        String articleAbstract=apiResult.getString("abstract");
        String firstImg=apiResult.getString("firstImg");
        if(symbol==null)
        {
            symbol="UNKNOWN";
        }
        if(articleAbstract==null)
        {
            articleAbstract="UNKNOWN";
        }
        if(firstImg==null)
        {
            firstImg="UNKNOWN";
        }

        result=articleCRUD.updateArticleEx(coordinates,new ArticleEx(articleAbstract,symbol,firstImg));
        return result;
    }


    public OperationResult<Void> processArticleWordCloud(String url, ArticleCoordinates coordinates)
    {
        OperationResult<Void> result=new OperationResult<>();
        try
        {
            HttpHeaders httpHeaders = new HttpHeaders();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("url", url);
            jsonObject.put("articleId", coordinates.getArticleId());
            jsonObject.put("posterId", coordinates.getPosterId());
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, httpHeaders);
            restTemplate.postForObject(ExternalAPIConfig.API_PROCESS_ARTICLE_WORDCLOUD, entity, Object.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setStatus(ResultConstants.API_UNEXPECTED_ERROR);
            return result;
        }
        result.setStatus(ResultConstants.OPERATION_OK);
        return result;
    }


    public String generateWordCloudUrl(@NotNull ArticleCoordinates coordinates)
    {
        return RemoteSrcPathConfig.SRC_WORDCLOUD_URL+"/"+coordinates.getPosterId()+"/"+coordinates.getArticleId()+"." + RemoteSrcPathConfig.SRC_WORDCLOUD_TYPE;
    }


    public String generateArticleContentURL(@NotNull ArticleCoordinates coordinates)
    {
        return SourcePathConfig.SRC_ARTICLE_ROOT+"/"+coordinates.getPosterId()+"/"+coordinates.getArticleId()+"."+ SourcePathConfig.SRC_ARTICLE_TYPE;
    }

    public String generateArticleContentURLForAPI(@NotNull ArticleCoordinates coordinates)
    {
        return SourcePathConfig.SRC_ARTICLE_ROOT_FORAPI+"/"+coordinates.getPosterId()+"/"+coordinates.getArticleId()+".md";
    }


    public String generateArticleRequestURL(@NotNull ArticleCoordinates coordinates)
    {
        return SourcePathConfig.SERVER_HOST +"/article/coordinates/"+coordinates.getPosterId()+"/"+coordinates.getArticleId();
    }


}
