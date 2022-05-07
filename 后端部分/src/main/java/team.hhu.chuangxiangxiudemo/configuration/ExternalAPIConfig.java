package team.hhu.chuangxiangxiudemo.configuration;

public final class ExternalAPIConfig
{
    public final static  String API_HOST ="http://172.20.10.2:5000";

    // FIXME: 2022/4/25
    public final static String API_GET_CLASSIFICATION= API_HOST +"/article/getclassification";

    public final static String API_PROCESS_ARTICLE_CONTENT = API_HOST +"/article/processContent";

    public final static String API_ARTICLE_SPIDER=API_HOST+"/article/getnews";

    public final static String API_PROCESS_COMMENT_WORDCLOUD=API_HOST+"/comment/processWordCloud";

    public final static String API_PROCESS_ARTICLE_WORDCLOUD =API_HOST+"/article/processWordCloud";


    ExternalAPIConfig(){}


}
