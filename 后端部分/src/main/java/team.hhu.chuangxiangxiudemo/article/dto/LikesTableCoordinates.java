package team.hhu.chuangxiangxiudemo.article.dto;

import team.hhu.chuangxiangxiudemo.configuration.ArticleConfig;
import team.hhu.chuangxiangxiudemo.configuration.SqlParametersConfig;

public class LikesTableCoordinates
{
    private final long tableId;
    private final int userId;
    public LikesTableCoordinates(int posterId, int articleId, int userId)
    {
        this.tableId=articleId* SqlParametersConfig.ARTICLEID_OFFSET
                +posterId* SqlParametersConfig.ARTICLE_POSTERID_OFFSET;
        this.userId=userId;
    }

    public LikesTableCoordinates(CommentCoordinates coordinates, int userId)
    {
        this.tableId=coordinates.getTableId()+coordinates.getCommentId()* SqlParametersConfig.COMMENTID_OFFSET;
        this.userId=userId;
    }

    public LikesTableCoordinates(ArticleCoordinates coordinates,int userId)
    {
        this.tableId=coordinates.getCommentTableId();
        this.userId=userId;
    }

    public long getTableId()
    {
        return tableId;
    }

    public int getCurrentUserId()
    {
        return userId;
    }
}
