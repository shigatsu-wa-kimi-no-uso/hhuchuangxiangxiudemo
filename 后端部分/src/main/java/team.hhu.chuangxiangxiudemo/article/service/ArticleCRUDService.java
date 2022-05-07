package team.hhu.chuangxiangxiudemo.article.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.hhu.chuangxiangxiudemo.article.dao.IArticleDao;
import team.hhu.chuangxiangxiudemo.article.dao.IArticleExDao;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.ArticleAtom;
import team.hhu.chuangxiangxiudemo.article.pojo.ArticleEx;
import team.hhu.chuangxiangxiudemo.configuration.ArticleConfig;
import team.hhu.chuangxiangxiudemo.configuration.SourcePathConfig;
import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.dao.IUserInfoDao;
import team.hhu.chuangxiangxiudemo.utility.DataAccessExceptionHandler;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

/**
 * 文章增删改查功能服务层
 * @author XuJinchen
 */

@Service
@Slf4j
public class ArticleCRUDService
{
    @Autowired
    private IArticleDao articleAtomMapper;

    @Autowired
    private IUserInfoDao userInfoMapper;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private CommentCRUDService commentCRUD;

    @Autowired
    private IArticleExDao articleExMapper;

    @Autowired
    private LikesService likesService;

    /**
     * Others
     */

     private @NotNull String _generateArticleFilePath(@NotNull ArticleCoordinates coordinates)
     {
         return SourcePathConfig.SRC_ARTICLE_DISKPATH +"/"+coordinates.getPosterId();
     }

    /**
     * CREATE
     */


    @Contract(value="null,_->fail")
    public OperationResult<Void> storeArticleContent(ArticleCoordinates coordinates, String content) throws IOException
    {
        IOService io=new IOService();
        byte[] binaryData=content.getBytes(ArticleConfig.ARTICLE_ENCODE);
        String path= _generateArticleFilePath(coordinates);
        return io.writeFile(path, coordinates.getArticleId() +"."+ SourcePathConfig.SRC_ARTICLE_TYPE,binaryData);
    }


    private Integer _addArticleInfo(int posterId, int categoryId, String title)
    {
        ArticleAtom info=new ArticleAtom();
        info.setPosterId(posterId);
        info.setTitle(title);
        info.setCategoryId(categoryId);
        articleAtomMapper.initArticleInfo(info);
        userInfoMapper.increaseUserArticleCount(posterId);
        return info.getArticleId();
    }

    private Integer _addArticleInfoSp(int posterId, int categoryId, String title,Timestamp postTime)
    {
        ArticleAtom info=new ArticleAtom();
        info.setPosterId(posterId);
        info.setTitle(title);
        info.setCategoryId(categoryId);
        info.setPostTime(postTime);
        articleAtomMapper.initArticleInfoSp(info);
        userInfoMapper.increaseUserArticleCount(posterId);
        return info.getArticleId();
    }



    /**
     *已经创建了评论表
     */
    @Transactional
    public @NotNull OperationResult<Integer> addArticleInfo(int posterId, int categoryId, String title)
    {
        OperationResult<Integer> result=new OperationResult<>();
        OperationResult<Void> servRes=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _addArticleInfo(posterId,categoryId,title),true);
        if(result.getStatus()==ResultConstants.OPERATION_OK)
        {
            ArticleCoordinates coordinates=new ArticleCoordinates(posterId,result.getReturnValue());
            DataAccessExceptionHandler.invoke(servRes,()->commentCRUD._createCommentTable(coordinates.getCommentTableId()),true);
            if(servRes.getStatus()==ResultConstants.OPERATION_OK)
            {
                DataAccessExceptionHandler.invoke(servRes, () -> likesService._createLikesTable(coordinates.getLikesTableId()), true);
            }

            result.setStatus(servRes.getStatus());
        }
        return result;
    }

    @Transactional
    public @NotNull OperationResult<Integer> addArticleInfoSp(int posterId, int categoryId, String title,Timestamp postTime)
    {
        OperationResult<Integer> result=new OperationResult<>();
        OperationResult<Void> servRes=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _addArticleInfoSp(posterId,categoryId,title,postTime),true);
        if(result.getStatus()==ResultConstants.OPERATION_OK)
        {
            ArticleCoordinates coordinates=new ArticleCoordinates(posterId,result.getReturnValue());
            DataAccessExceptionHandler.invoke(servRes,()->commentCRUD._createCommentTable(coordinates.getCommentTableId()),true);
            if(servRes.getStatus()==ResultConstants.OPERATION_OK)
            {
                DataAccessExceptionHandler.invoke(servRes, () -> likesService._createLikesTable(coordinates.getLikesTableId()), true);
            }
            result.setStatus(servRes.getStatus());
        }
        return result;
    }


    /**
     * RETRIEVE
     */


    /**
     *    未找到时:返回ResultConstants.ARTICLE_NOT_FOUND
     */

    /**
     *    与getSingleArticleAtom函数的区别：不进行返回值检测，加快速度
     */
    public OperationResult<ArticleAtom> getTrustedSingleArticleAtom(ArticleCoordinates coordinates)
    {
        OperationResult<ArticleAtom> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->articleAtomMapper.retrieveSingleArticleAtom(coordinates),false);
        return result;
    }

    public OperationResult<String> getArticleTitle(ArticleCoordinates coordinates)
    {
        OperationResult<String> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->articleAtomMapper.retrieveArticleTitle(coordinates),false);
        return result;
    }

    public OperationResult<ArticleAtom> getSingleArticleAtom(ArticleCoordinates coordinates)
    {
        OperationResult<ArticleAtom> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> articleAtomMapper.retrieveSingleArticleAtom(coordinates),false);
        ArticleAtom info=result.getReturnValue();
        if(result.getStatus()==ResultConstants.OPERATION_OK)
        {
            if (info == null)
            {
                result.setStatus(ResultConstants.ARTICLE_NOT_FOUND);
                return result;
            }
            result.setStatus(ResultConstants.OPERATION_OK);
            info.setPosterId(coordinates.getPosterId());
            result.setReturnValue(info);
        }
        return result;
    }

    public OperationResult<ArticleEx> getSingleArticleEx(ArticleCoordinates coordinates)
    {
        OperationResult<ArticleEx> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->articleExMapper.retrieveSingleArticleEx(coordinates),false);
        return result;
    }

    public OperationResult<List<ArticleCoordinates>> getArticleCoordinatesBeforeTimeByCategory(int categoryId, Timestamp time, int selectNumber)
    {
        OperationResult<List<ArticleCoordinates>> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->articleAtomMapper.retrieveArticleCoordinatesBeforeTimeByCategory(categoryId,time,selectNumber),false);
        return result;
    }


    public OperationResult<List<ArticleAtom>> getLastPostedArticleAtomsByPoster(int posterId,Timestamp time,int selectNumber)
    {
        LogUtil.info("请求获取在用户 "+posterId+"下的 ArticleAtom数目:"+selectNumber);
        OperationResult<List<ArticleAtom>> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> articleAtomMapper.retrieveArticleAtomsBeforeTimeByPoster(posterId,time,selectNumber),false);
        return result;
    }

    /**
     * UPDATE
     */

    private Void _updateArticleEx(ArticleCoordinates coordinates, ArticleEx info)
    {
        articleExMapper.updateArticleEx(coordinates,info);
        return null;
    }

    private Void _updateArticleTitle(ArticleCoordinates coordinates,String title)
    {
        articleAtomMapper.updateArticleTitle(coordinates,title);
        return null;
    }

    private Void _updateArticleCategoryId(ArticleCoordinates coordinates, int newCategoryId)
    {
        articleAtomMapper.updateArticleCategory(coordinates,newCategoryId);
        return null;
    }

    private Void _updateCategoryIndex(ArticleCoordinates coordinates, int oldCategoryId, int newCategoryId)
    {
        articleAtomMapper.updateCategoryIndex(coordinates,oldCategoryId,newCategoryId);
        return null;
    }

    @Transactional
    public OperationResult<Void> updateArticleEx(ArticleCoordinates coordinates,ArticleEx info)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _updateArticleEx(coordinates,info),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> updateArticleTitle(ArticleCoordinates coordinates,String newTitle)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result, () -> _updateArticleTitle(coordinates,newTitle ),true);
        return result;
    }

    private Void _updateArticleCategory(ArticleCoordinates coordinates,int newCategoryId)
    {
        ArticleAtom info=articleAtomMapper.retrieveSingleArticleAtom(coordinates);
        int oldCategoryId=info.getCategoryId();
        articleAtomMapper.updateCategoryIndex(coordinates,oldCategoryId,newCategoryId);
        articleAtomMapper.updateArticleCategory(coordinates,newCategoryId);
        return null;
    }

    @Transactional
    public OperationResult<Void> updateArticleCategory(ArticleCoordinates coordinates,int newCategoryId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_updateArticleCategory(coordinates,newCategoryId),true);
        return result;
    }

    @Contract(value="null,_->fail")
    public OperationResult<Void> updateArticleContent(ArticleCoordinates coordinates,String content) throws IOException
    {
        return storeArticleContent(coordinates,content);
    }

    /**
     *DELETE
     */

    /**
     *删除文章基本信息
     */
    private Void _deleteArticle(ArticleCoordinates coordinates)
    {
        ArticleAtom info=articleAtomMapper.retrieveSingleArticleAtom(coordinates);
        articleAtomMapper.deleteArticleAtom(coordinates);
        articleExMapper.deleteArticleEx(coordinates);
        articleAtomMapper.deleteCategoryIndex(coordinates,info.getCategoryId());
        userInfoMapper.decreaseUserArticleCount((int) coordinates.getPosterId());
        return null;
    }

    @Transactional
    public OperationResult<Void> deleteArticle(ArticleCoordinates coordinates) throws Exception
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_deleteArticle(coordinates),true);
        return result;
    }

}
