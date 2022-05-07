package team.hhu.chuangxiangxiudemo.article.dto;

import team.hhu.chuangxiangxiudemo.configuration.SqlParametersConfig;

public class CommentCoordinates
{
    private final long tableId;

    private int commentId;

    public CommentCoordinates(int articlePosterId, int articleId, int commentId)
    {
        this.tableId=articleId* SqlParametersConfig.ARTICLEID_OFFSET
                +articlePosterId* SqlParametersConfig.ARTICLE_POSTERID_OFFSET;
        this.commentId=commentId;
    }

    public void setCommentId(int commentId)
    {
        this.commentId = commentId;
    }

    public CommentCoordinates(ArticleCoordinates coordinates, int commentId)
    {
        this.tableId=coordinates.getCommentTableId();
        this.commentId=commentId;
    }


    public CommentCoordinates(int articlePosterId,int articleId)
    {
        this.tableId=articleId* SqlParametersConfig.ARTICLEID_OFFSET
                +articlePosterId*SqlParametersConfig.ARTICLE_POSTERID_OFFSET;
        this.commentId=-1;
    }

    public CommentCoordinates(CommentCoordinates coordinates,int commentId)
    {
        this.tableId=coordinates.tableId;
        this.commentId=commentId;
    }

    public long getTableId()
    {
        return this.tableId;
    }

    public int getCommentId()
    {
        return this.commentId;
    }

    public long getLikesTableId()
    {
        if(commentId==-1)
        {
            return -1;
        }
        else
        {
            return tableId+commentId*SqlParametersConfig.COMMENTID_OFFSET;
        }
    }

}
