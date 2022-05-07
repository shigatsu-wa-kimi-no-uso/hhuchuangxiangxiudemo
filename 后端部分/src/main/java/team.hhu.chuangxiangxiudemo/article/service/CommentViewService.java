package team.hhu.chuangxiangxiudemo.article.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.hhu.chuangxiangxiudemo.article.dao.ICommentDao;
import team.hhu.chuangxiangxiudemo.article.pojo.CommentInfo;
import team.hhu.chuangxiangxiudemo.configuration.ExternalAPIConfig;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.utility.DataAccessExceptionHandler;

import java.sql.Timestamp;
import java.util.List;


@Service
public class CommentViewService
{
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ICommentDao commentInfoMapper;

    public OperationResult<Void> processCommentWordCloud(long tableId)
    {
        OperationResult<Void> result=new OperationResult<>();
        OperationResult<List<String>> results=new OperationResult<>();
        DataAccessExceptionHandler.invoke(results,()->commentInfoMapper.retrieveCommentContentBeforeTime(tableId, Timestamp.valueOf("2059-01-01 00:00:00"),1000),false);

        List<String> data=results.getReturnValue();
        JSONObject requestBody=new JSONObject();
        requestBody.put("id",tableId);
        requestBody.put("comment",data);
        if(results.getStatus()==ResultConstants.OPERATION_OK)
        {
            try
            {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                HttpEntity<JSONObject> entity = new HttpEntity<>(requestBody, httpHeaders);
                restTemplate.postForObject(ExternalAPIConfig.API_PROCESS_COMMENT_WORDCLOUD, entity, Object.class);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                result.setStatus(ResultConstants.API_UNEXPECTED_ERROR);
                return result;
            }
        }
        result.setStatus(ResultConstants.OPERATION_OK);
        return result;
    }

}
