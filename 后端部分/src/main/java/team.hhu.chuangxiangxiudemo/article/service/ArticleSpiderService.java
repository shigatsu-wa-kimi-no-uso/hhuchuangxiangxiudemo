package team.hhu.chuangxiangxiudemo.article.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
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
import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;




/**
 * 新闻爬取代码
 */


@Service
@Slf4j
public class ArticleSpiderService
{

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ArticleCRUDService articleCRUDService;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private ArticleViewService articleViewService;


    /**
     * 文章爬取接口访问代码
     */


    private OperationResult<List<Map<String,Object>>> _getArticle(int number)
    {
        OperationResult<List<Map<String,Object>>> result=new OperationResult<>();
        HttpHeaders httpHeaders = new HttpHeaders();
        JSONObject jsonObject=new JSONObject();
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        jsonObject.put("number",number);
        HttpEntity<JSONObject> entity = new HttpEntity<>(jsonObject, httpHeaders);
        List<Map<String,Object>> apiResult;
        try
        {
           apiResult = restTemplate.postForObject(ExternalAPIConfig.API_ARTICLE_SPIDER,entity,List.class);
        }
        catch (Exception e)
        {
            result.setStatus(ResultConstants.API_UNEXPECTED_ERROR);
            return result;
        }
        result.setStatus(ResultConstants.OPERATION_OK);
        result.setReturnValue(apiResult);
        return result;
    }


    /**
     * 文章信息数据库写入
     */

    private @NotNull OperationResult<Integer> _createDBIndicesAndFile(List<Map<String,Object>> data) throws IOException
    {
        System.out.println("print");
        OperationResult<Integer> result=new OperationResult<>();
        System.out.println(data);
        System.out.println("printmap");
        System.out.println(data.get(0));
        Integer count=0;
        for (Map<String,Object> i : data)
        {
            String title=(String) i.get("title");
            String articleAbstract=(String) i.get("abstract");
            String firstImg=(String) i.get("img");
            String symbolWords=(String) i.get("word");
            String content=(String)  i.get("body");
            Timestamp postTime=Timestamp.valueOf((String) i.get("postTime"));
            String strCategoryId=(String) i.get("categoryId");
            int categoryId=Integer.parseInt(strCategoryId);

            if(symbolWords==null)
            {
                symbolWords="UNKNOWN";
            }
            if(articleAbstract==null)
            {
                articleAbstract="UNKNOWN";
            }
            if(firstImg==null)
            {
                firstImg="UNKNOWN";
            }

            System.out.println(title);
            System.out.println(postTime);
            OperationResult<Integer> servRes=articleCRUDService.addArticleInfoSp(0,categoryId,title,postTime);
            ArticleCoordinates coordinates=new ArticleCoordinates(0,servRes.getReturnValue());
            if(servRes.getStatus()== ResultConstants.OPERATION_OK)
            {
                if(articleAbstract.equals("UNKNOWN"))
                {
                    if (articleCRUDService.storeArticleContent(coordinates, content).getStatus() == ResultConstants.OPERATION_OK)
                    {
                        String url = articleViewService.generateArticleContentURL(coordinates);
                        articleViewService.processArticleContent(url, coordinates);
                        ++count;
                    }

                }
                else
                {
                    ArticleEx info = new ArticleEx(articleAbstract, symbolWords, firstImg);
                    articleCRUDService.updateArticleEx(coordinates, info);
                    if (articleCRUDService.storeArticleContent(coordinates, content).getStatus() == ResultConstants.OPERATION_OK)
                    {
                        String url = articleViewService.generateArticleContentURL(coordinates);
                        articleViewService.processArticleWordCloud(url, coordinates);
                        ++count;
                    }

                }
            }
        }
        result.setReturnValue(count);
        result.setStatus(ResultConstants.OPERATION_OK);
        return result;
    }


    /**
     * 文章爬取线程创建代码
     */
    private void work()
    {
        asyncTaskService.invoke(new Callable<OperationResult<Void>>()
        {
            @Override
            public OperationResult<Void> call() throws Exception
            {
                OperationResult<List<Map<String,Object>>> result;
                while(true)
                {
                   result= _getArticle(100);
                   if(result.getStatus()==ResultConstants.OPERATION_OK)
                   {
                       log.info(result.getStatus().getMessage());
                       OperationResult<Integer> result2=_createDBIndicesAndFile(result.getReturnValue());
                       log.info(result2.getReturnValue().toString());
                   }
                   log.info(result.getStatus().getMessage());
                   Thread.sleep(60*60*1000);
                }
            }
        });
    }



}
