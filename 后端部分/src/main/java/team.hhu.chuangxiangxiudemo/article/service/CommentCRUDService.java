package team.hhu.chuangxiangxiudemo.article.service;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.hhu.chuangxiangxiudemo.article.dao.IArticleDao;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.dto.CommentCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.CommentInfo;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.article.dao.ICommentDao;
import team.hhu.chuangxiangxiudemo.utility.DataAccessExceptionHandler;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CommentCRUDService
{
    @Autowired
    private ICommentDao commentInfoMapper;

    @Autowired
    private IArticleDao articleAtomMapper;

    @Autowired
    private LikesService likesService;


    /**
     * CREATE
     */

    /**
     *创建成功时，评论坐标中的commentId会被自动设置为创建时添加的Id
     */
    @Contract(pure = false)
    private Void _createCommentInfo(CommentCoordinates coordinates, String content,int posterId)
    {
        commentInfoMapper.createCommentInfo(coordinates,content,posterId);
        return null;
    }

    protected Void _createCommentTable(long tableId)
    {
        commentInfoMapper.createCommentTable(tableId);
        return null;
    }


    /**
     *评论表应在发布文章时就已经创建
     */

    @Transactional
    public @NotNull OperationResult<Integer> addCommentInfo(CommentCoordinates coordinates,String content, int posterId)
    {
        OperationResult<Integer> result=new OperationResult<>();
        OperationResult<Void> servRes=new OperationResult<>();
        DataAccessExceptionHandler.invoke(servRes,()-> _createCommentInfo(coordinates,content,posterId),true);
        if(servRes.getStatus()==ResultConstants.OPERATION_OK)
        {
            DataAccessExceptionHandler.invoke(servRes,()->likesService._createLikesTable(coordinates.getLikesTableId()),true);
        }
        result.setStatus(servRes.getStatus());
        result.setReturnValue(coordinates.getCommentId());
        return result;
    }


    /**
     * RETRIEVE
     */

    private List<CommentInfo> _retrieveCommentInfoBeforeTime(long tableId, Timestamp time, int selectNumber)
    {
        return commentInfoMapper.retrieveCommentInfoBeforeTime(tableId,time,selectNumber);
    }


    /**
     *get的trusted版本，去除了返回值检测，加快速度
     * 在确保被检索数据一定存在时使用
     */
    public OperationResult<CommentInfo> getTrustedCommentInfo(CommentCoordinates coordinates)
    {
        OperationResult<CommentInfo> result=new OperationResult<>();
        CommentInfo commentInfo= commentInfoMapper.retrieveSingleCommentInfo(coordinates);
        result.setStatus(ResultConstants.OPERATION_OK);
        result.setReturnValue(commentInfo);
        return result;
    }


    public OperationResult<CommentInfo> getCommentInfo(CommentCoordinates coordinates)
    {
        OperationResult<CommentInfo> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->commentInfoMapper.retrieveSingleCommentInfo(coordinates),false);
        if(result.getStatus()==ResultConstants.OPERATION_OK)
        {
            if(result.getReturnValue()==null)
                result.setStatus(ResultConstants.COMMENT_NOT_FOUND);
            return result;
        }
        return result;
    }


    public OperationResult<List<CommentInfo>> getCommentInfoBeforeTime(CommentCoordinates coordinates,Timestamp time, int selectNumber)
    {
        OperationResult<List<CommentInfo>> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _retrieveCommentInfoBeforeTime(coordinates.getTableId(),time,selectNumber),false);
        return result;
    }


    /**
     *UPDATE
     */


    private Void _increaseCommentCount(ArticleCoordinates coordinates)
    {
        articleAtomMapper.increaseArticleComments(coordinates);
        return null;
    }

    private Void _decreaseCommentCount(ArticleCoordinates coordinates)
    {
        articleAtomMapper.decreaseArticleComments(coordinates);
        return null;
    }

    @Transactional
    public OperationResult<Void> increaseCommentCount(ArticleCoordinates coordinates)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_increaseCommentCount(coordinates),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> decreaseCommentCount(ArticleCoordinates coordinates)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_decreaseCommentCount(coordinates),true);
        return result;
    }




    /**
     * DELETE
     */

    private Void _deleteCommentInfo(CommentCoordinates coordinates)
    {
        commentInfoMapper.deleteCommentInfo(coordinates);
        return null;
    }

    protected Void _deleteCommentList(long tableId) throws Exception
    {
        commentInfoMapper.deleteCommentList(tableId);
        throw new Exception();
      //  return null;
    }

    @Transactional
    public OperationResult<Void> deleteCommentInfo(CommentCoordinates coordinates)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _deleteCommentInfo(coordinates),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> deleteCommentList(long tableId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_deleteCommentList(tableId),true);
        return result;
    }

}
