package team.hhu.chuangxiangxiudemo.article.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.ArticleAtom;
import team.hhu.chuangxiangxiudemo.article.pojo.ArticleEx;
import team.hhu.chuangxiangxiudemo.article.service.ArticleCRUDService;
import team.hhu.chuangxiangxiudemo.article.service.ArticleViewService;
import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.standards.APIParams;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.utility.Converter;
import team.hhu.chuangxiangxiudemo.utility.StringUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("article")
@Slf4j
public class ArticleViewController
{
    @Autowired
    private ArticleViewService articleView;

    @Autowired
    private ArticleCRUDService articleCRUD;

    @Autowired
    private AsyncTaskService asyncTaskService;



    @GetMapping("/category/{categoryId}")
    public String newsPageDirect()
    {
        return "/category";
    }


    /**
     * service层查询未使用多线程优化时，检索100条信息，平均用时400ms
     * 多线程优化后，最快用时100ms
     */
    @ResponseBody
    @RequestMapping("/category/{categoryId}/get")
    @Contract(value = "null,_->fail")
    public JSONArray getArticlesByCategory(@RequestBody Map<String, Object> map, @PathVariable int categoryId)
            throws IllegalAccessException
    {
        long start = System.currentTimeMillis();
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

        OperationResult serviceResult = articleCRUD.getArticleCoordinatesBeforeTimeByCategory(categoryId, time, selectNumber);

        List<ArticleCoordinates> list = (List<ArticleCoordinates>) serviceResult.getReturnValue();

        int listSize=list.size();

        if (serviceResult.getStatus() == ResultConstants.OPERATION_OK)
        {
            result.putAll(ResultConstants.OPERATION_OK.getMap());
            result.put("count", list.size());
            resultArr.add(result);
            Future<OperationResult<ArticleAtom>>[] futures=new Future[listSize];
            Future<OperationResult<ArticleEx>>[] futures2=new Future[listSize];
            int index=0;
            for(ArticleCoordinates i : list)
            {
                futures[index]=asyncTaskService.invoke(()->articleCRUD.getSingleArticleAtom(i));
                futures2[index++]=asyncTaskService.invoke(()->articleCRUD.getSingleArticleEx(i));
            }
            index=0;
            for (ArticleCoordinates i: list)
            {
                result = new JSONObject();//避免重复引用对象，使用新对象存储，防止出现$ref":"$[0]的问题
                serviceResult=asyncTaskService.getResult(futures[index],1, TimeUnit.SECONDS);
                if (serviceResult.getStatus() == ResultConstants.OPERATION_OK)
                {
                    String url = articleView.generateArticleRequestURL(i);
                    if(serviceResult.getReturnValue()!=null)
                    {
                        result.putAll(Converter.objectToMap(serviceResult.getReturnValue()));
                    }
                    result.put("url", url);
                    serviceResult=asyncTaskService.getResult(futures2[index++],1,TimeUnit.SECONDS);
                    ArticleEx articleEx=(ArticleEx) serviceResult.getReturnValue();

                    if(serviceResult.getStatus()==ResultConstants.OPERATION_OK && serviceResult.getReturnValue()!=null)
                    {
                        System.out.println(articleEx.getFirstImg());
                        result.putAll(Converter.objectToMap(serviceResult.getReturnValue()));
                    }
                    System.out.println(serviceResult.getStatus());
                    resultArr.add(result);
                }
                else
                {
                    result.put("url", "null");
                    resultArr.add(result);
                }
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


    @GetMapping("/coordinates/{posterId}/{articleId}")
    public String articlePageRedirect() throws IllegalAccessException
    {
        return "/single_news";
    }

    @ResponseBody
    @RequestMapping("/coordinates/{posterId}/{articleId}/get")
    public JSONObject getSingleArticle(@PathVariable int posterId, @PathVariable int articleId)
            throws IllegalAccessException
    {
        JSONObject result = new JSONObject();
        ArticleCoordinates coordinates = new ArticleCoordinates(posterId, articleId);
        OperationResult<ArticleAtom> servRes = articleCRUD.getSingleArticleAtom(coordinates);
        if (servRes.getStatus() == ResultConstants.OPERATION_OK)
        {
            ArticleAtom atom = servRes.getReturnValue();
            String url = articleView.generateArticleContentURL(coordinates);
            result.putAll(Converter.objectToMap(atom));
            result.put("url", url);
        }
        result.putAll(servRes.getStatus().getMap());
        return result;
    }


    @ResponseBody
    @RequestMapping("/coordinates/{posterId}/{articleId}/getTitle")
    public JSONObject getArticleTitle(@PathVariable int posterId, @PathVariable int articleId)
    {
        JSONObject result = new JSONObject();
        ArticleCoordinates coordinates = new ArticleCoordinates(posterId, articleId);
        OperationResult<String> servRes = articleCRUD.getArticleTitle(coordinates);
        if (servRes.getStatus() == ResultConstants.OPERATION_OK)
        {
            result.put("title", servRes.getReturnValue());
        }
        result.putAll(servRes.getStatus().getMap());
        return result;
    }


    @ResponseBody
    @RequestMapping("/get")
    public JSONObject getClassification(@RequestBody Map<String, Object> map)
    {
        JSONObject jsonObject=new JSONObject();
        String title= (String) map.get("title");
        OperationResult<Integer> result=articleView.getClassificationId(title);
        jsonObject.putAll(result.getStatus().getMap());
        jsonObject.put("categoryId",result.getReturnValue());
        return jsonObject;
    }

}
