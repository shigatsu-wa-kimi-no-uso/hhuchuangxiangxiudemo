package team.hhu.chuangxiangxiudemo.article.service;

import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.hhu.chuangxiangxiudemo.article.dao.IArticleDao;
import team.hhu.chuangxiangxiudemo.article.dao.ICommentDao;
import team.hhu.chuangxiangxiudemo.article.dao.ILikesTableDao;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.dto.CommentCoordinates;
import team.hhu.chuangxiangxiudemo.article.dto.LikesTableCoordinates;
import team.hhu.chuangxiangxiudemo.service.AsyncTaskService;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.dao.IUserInfoDao;
import team.hhu.chuangxiangxiudemo.user.service.UserInfoService;
import team.hhu.chuangxiangxiudemo.utility.DataAccessExceptionHandler;

@Service
public class LikesService
{
    @Autowired
    private ILikesTableDao likesTableMapper;

    @Autowired
    private IArticleDao articleAtomMapper;

    @Autowired
    private ICommentDao commentInfoMapper;

    @Autowired
    private AsyncTaskService asyncTaskService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private IUserInfoDao userInfoMapper;


    /**
     *CREATE
     */

    /**
     *点赞表应当在对应评论/文章创建时就创建好！
     */
    protected Void _createLikesTable(long tableId)
    {
        likesTableMapper.createLikesTable(tableId);
        return null;
    }


    /**
     * RETRIEVE
     */

    private Integer _checkUserLikeState(LikesTableCoordinates coordinates)
    {
        return likesTableMapper.checkUserLikeState(coordinates);
    }

    public OperationResult<Integer> checkLikeState(LikesTableCoordinates coordinates)
    {
        OperationResult<Integer> servRes=new OperationResult<>();
        DataAccessExceptionHandler.invoke(servRes,()-> _checkUserLikeState(coordinates),false);
        OperationResult<Integer> result=new OperationResult<>();
        if(servRes.getStatus()==ResultConstants.OPERATION_OK)
        {
            if(servRes.getReturnValue()!=null)
            {
                if(servRes.getReturnValue()>0)
                    result.setReturnValue(1);
                else
                    result.setReturnValue(-1);
            }
            else
                result.setReturnValue(0);
        }
        result.setStatus(servRes.getStatus());
        return result;
    }


    /**
     * 建表删表与数据查询更新要分开！！
     *详见 https://blog.csdn.net/sunshixingh/article/details/50920119
     * 部分SQL语句会有一个隐式提交操作，导致之前的事务无法正常回滚！！
     */


    private Void _giveLike(ArticleCoordinates coordinates, int userId) throws Exception
    {
        /*添加表语句放在最上面,其中包含了DDL语句,如果放在后面,将导致回滚失败*/
        likesTableMapper.createUserLikeState(new LikesTableCoordinates(coordinates,userId));
        articleAtomMapper.increaseArticleLikes(coordinates);
        userInfoMapper.increaseUserLikesCount((int) coordinates.getPosterId());
        throw new Exception();
      //  return null;
    }

    private Void _giveLike(CommentCoordinates coordinates, int commentPosterId, int userId)
    {
        likesTableMapper.createUserLikeState(new LikesTableCoordinates(coordinates,userId));
        commentInfoMapper.increaseCommentLikes(coordinates);
        userInfoMapper.increaseUserLikesCount(commentPosterId);
        return null;
    }

    private Void _giveDislike(CommentCoordinates coordinates,int userId)
    {
        likesTableMapper.createUserLikeState(new LikesTableCoordinates(coordinates,-userId));
        return null;
    }

    private Void _giveDislike(ArticleCoordinates coordinates,int userId)
    {
        likesTableMapper.createUserLikeState(new LikesTableCoordinates(coordinates,-userId));
        return null;
    }

    private Void _switchLikeStateToDisliked(CommentCoordinates coordinates,int commentPosterId,int userId)
    {
        commentInfoMapper.decreaseCommentLikes(coordinates);
        userInfoMapper.decreaseUserLikesCount(commentPosterId);
        likesTableMapper.switchUserLikeState(new LikesTableCoordinates(coordinates,userId));
        return null;
    }

    private Void _switchLikeStateToLiked(CommentCoordinates coordinates,int commentPosterId,int userId)
    {
        commentInfoMapper.increaseCommentLikes(coordinates);
        userInfoMapper.increaseUserLikesCount(commentPosterId);
        likesTableMapper.switchUserLikeState(new LikesTableCoordinates(coordinates,userId));
        return null;
    }

    private Void _switchLikeStateToLiked(ArticleCoordinates coordinates,int userId)
    {
        articleAtomMapper.increaseArticleComments(coordinates);
        userInfoMapper.increaseUserLikesCount(coordinates.getPosterId());
        likesTableMapper.switchUserLikeState(new LikesTableCoordinates(coordinates,userId));
        return null;
    }

    private Void _switchLikeStateToDisliked(ArticleCoordinates coordinates,int userId)
    {
        articleAtomMapper.decreaseArticleComments(coordinates);
        userInfoMapper.decreaseUserLikesCount(coordinates.getPosterId());
        likesTableMapper.switchUserLikeState(new LikesTableCoordinates(coordinates,userId));
        return null;
    }


    @Transactional
    public OperationResult<Void> giveLike(ArticleCoordinates coordinates, int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _giveLike(coordinates,userId),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> giveLike(CommentCoordinates coordinates, int commentPosterId, int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _giveLike(coordinates,commentPosterId,userId),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> giveDislike(CommentCoordinates coordinates,int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_giveDislike(coordinates,userId),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> giveDislike(ArticleCoordinates coordinates,int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_giveDislike(coordinates,userId),true);
        return result;
    }



    @Transactional
    public OperationResult<Void> switchLikeStateToLiked(CommentCoordinates coordinates,int commentPosterId,int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_switchLikeStateToLiked(coordinates,commentPosterId,userId),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> switchLikeStateToDisliked(CommentCoordinates coordinates,int commentPosterId,int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_switchLikeStateToDisliked(coordinates,commentPosterId,userId),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> switchLikeStateToDisliked(ArticleCoordinates coordinates,int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_switchLikeStateToDisliked(coordinates,userId),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> switchLikeStateToLiked(ArticleCoordinates coordinates,int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_switchLikeStateToLiked(coordinates,userId),true);
        return result;
    }

    /**
     * DELETE
     */


    private Void _undoGiveLike(ArticleCoordinates coordinates, int userId)
    {
        articleAtomMapper.decreaseArticleLikes(coordinates);
        userInfoMapper.decreaseUserLikesCount(coordinates.getPosterId());
        likesTableMapper.deleteUserLikeState(new LikesTableCoordinates(coordinates,userId));
        return null;
    }

    private Void _undoGiveLike(CommentCoordinates coordinates, int commentPosterId, int userId)
    {
        commentInfoMapper.decreaseCommentLikes(coordinates);
        userInfoMapper.decreaseUserLikesCount(commentPosterId);
        likesTableMapper.deleteUserLikeState(new LikesTableCoordinates(coordinates,userId));
        return null;
    }

    private Void _undoGiveDislike(CommentCoordinates coordinates,int userId)
    {
        likesTableMapper.deleteUserLikeState(new LikesTableCoordinates(coordinates,userId));
        return null;
    }

    private Void _undoGiveDislike(ArticleCoordinates coordinates,int userId)
    {
        likesTableMapper.deleteUserLikeState(new LikesTableCoordinates(coordinates,userId));
        return null;
    }


    @Transactional
    public OperationResult<Void> undoGiveLike(ArticleCoordinates coordinates, int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _undoGiveLike(coordinates,userId),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> undoGiveLike(CommentCoordinates coordinates, int commentPosterId, int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _undoGiveLike(coordinates,commentPosterId,userId),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> undoGiveDislike(ArticleCoordinates coordinates,int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _undoGiveDislike(coordinates,userId),true);
        return result;
    }

    @Transactional
    public OperationResult<Void> undoGiveDislike(CommentCoordinates coordinates,int userId)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()-> _undoGiveDislike(coordinates,userId),true);
        return result;
    }




    protected Void _batchDeleteLikesList(long tableIdPrefix)
    {
        likesTableMapper.batchDeleteLikesList(tableIdPrefix);
        return null;
    }

    @Transactional
    public OperationResult<Void> batchDeleteLikesList(long tableIdPrefix)
    {
        OperationResult<Void> result=new OperationResult<>();
        DataAccessExceptionHandler.invoke(result,()->_batchDeleteLikesList(tableIdPrefix),true);
        return result;
    }

}
