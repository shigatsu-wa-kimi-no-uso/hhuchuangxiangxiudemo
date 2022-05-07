package team.hhu.chuangxiangxiudemo.article.pojo;

public class ArticleEx
{
    private String articleAbstract;
    private String articleSymbol;
    private String firstImg;

    public ArticleEx(){}

    public ArticleEx(String articleAbstract, String articleSymbol, String firstImg)
    {
        this.articleAbstract = articleAbstract;
        this.articleSymbol = articleSymbol;
        this.firstImg = firstImg;
    }

    public String getFirstImg()
    {
        return firstImg;
    }

    public void setFirstImg(String firstImg)
    {
        this.firstImg = firstImg;
    }

    public String getArticleAbstract()
    {
        return articleAbstract;
    }

    public void setArticleAbstract(String articleAbstract)
    {
        this.articleAbstract = articleAbstract;
    }



    public String getArticleSymbol()
    {
        return articleSymbol;
    }

    public void setArticleSymbol(String articleSymbol)
    {
        this.articleSymbol = articleSymbol;
    }
}
