package team.hhu.chuangxiangxiudemo.article.dto;


import team.hhu.chuangxiangxiudemo.configuration.SqlParametersConfig;


/**
 * 文章坐标 信息
 */

public class ArticleCoordinates
{
    private int posterId;

    private int articleId;


    public ArticleCoordinates(int posterId, int articleId)
    {
        this.posterId=posterId;
        this.articleId=articleId;
    }

    public void setPosterId(int posterId)
    {
        this.posterId = posterId;
    }

    public void setArticleId(int articleId)
    {
        this.articleId = articleId;
    }

    public int getPosterId()
    {
        return posterId;
    }

    public int getArticleId()
    {
        return articleId;
    }

    public long getCommentTableId()
    {
        return articleId * SqlParametersConfig.ARTICLEID_OFFSET
                + posterId * SqlParametersConfig.ARTICLE_POSTERID_OFFSET;
    }

    public long getLikesTableIdPrefix()
    {
        return (articleId * SqlParametersConfig.ARTICLEID_OFFSET
                + posterId * SqlParametersConfig.ARTICLE_POSTERID_OFFSET)/SqlParametersConfig.ARTICLEID_OFFSET;
    }

    public long getLikesTableId()
    {
        return articleId * SqlParametersConfig.ARTICLEID_OFFSET + posterId * SqlParametersConfig.ARTICLE_POSTERID_OFFSET;
    }

}
