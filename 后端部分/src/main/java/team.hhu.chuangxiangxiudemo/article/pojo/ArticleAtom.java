package team.hhu.chuangxiangxiudemo.article.pojo;

import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;

import java.sql.Timestamp;

public class ArticleAtom
{
    private int articleId;
    private String title;
    private int posterId;
    private int categoryId;
    private int likes;
    private Timestamp postTime;
    private int comments;

    public int getComments()
    {
        return comments;
    }

    public void setComments(int comments)
    {
        this.comments = comments;
    }

    public int getPosterId()
    {
        return posterId;
    }

    public int getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(int categoryId)
    {
        this.categoryId = categoryId;
    }

    public void setPosterId(int posterId)
    {
        this.posterId = posterId;
    }

    public int getLikes()
    {
        return likes;
    }

    public void setLikes(int likes)
    {
        this.likes = likes;
    }

    public int getArticleId()
    {
        return articleId;
    }

    public void setArticleId(int articleId)
    {
        this.articleId = articleId;
    }

    public Timestamp getPostTime()
    {
        return postTime;
    }

    public void setPostTime(Timestamp postTime)
    {
        this.postTime = postTime;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public ArticleCoordinates getCoordinates()
    {
        return new ArticleCoordinates(this.posterId,this.articleId);
    }

    @Override
    public String toString()
    {
        return "ArticleAtom{" +
                "articleId=" + articleId +
                ", title='" + title + '\'' +
                ", posterId=" + posterId +
                ", categoryId=" + categoryId +
                ", likes=" + likes +
                ", postTime=" + postTime +
                ", comments=" + comments +
                '}';
    }
}
