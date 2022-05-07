package team.hhu.chuangxiangxiudemo.article.pojo;


import java.sql.Timestamp;

public class CommentInfo
{
    private int commentId;
    private int posterId;
    private int likes;
    private Timestamp postTime;
    private String content;

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getCommentId()
    {
        return commentId;
    }

    public void setCommentId(int commentId)
    {
        this.commentId = commentId;
    }

    public int getPosterId()
    {
        return posterId;
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

    public Timestamp getPostTime()
    {
        return postTime;
    }

    public void setPostTime(Timestamp postTime)
    {
        this.postTime = postTime;
    }
}
