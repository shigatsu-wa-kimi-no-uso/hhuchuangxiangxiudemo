package team.hhu.chuangxiangxiudemo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource(value={"classpath:source-file.properties"})
@Component
@ConfigurationProperties(prefix="source-file")
public final class SourcePathConfig
{
    public static String SRC_FILE_DISKPATH="D:/chuangxiangxiudemo/src";

    public static String SRC_ARTICLE_TYPE="md";

    public static String SRC_URL_HOST="http://172.20.10.9:80";

    public static String SRC_URL_ROOT=SRC_URL_HOST+"/src";

    public static String SRC_ARTICLE_ROOT=SRC_URL_ROOT+"/article";

    public static String SRC_ARTICLE_DISKPATH=SRC_FILE_DISKPATH+"/article";

    public static String SERVER_HOST="http://172.20.10.9:8080";

    public static String SRC_COMMENT_ROOT;

    public static String SRC_COMMENT_TYPE;

    public static String SRC_IMAGE_DISKPATH =SRC_FILE_DISKPATH+"/img";

    public static String SRC_AVATAR_DISKPATH =SRC_FILE_DISKPATH+"/avatar";

    public static String SRC_IMAGE_URL=SRC_URL_ROOT+"/img";

    public static String SRC_AVATAR_URL=SRC_URL_ROOT+"/avatar";

    public static String SRC_COMMENT_URL;

    public static String SRC_HOST_FORAPI="http://172.20.10.9:80";

    public static String SRC_ARTICLE_ROOT_FORAPI=SRC_HOST_FORAPI+"/src/article";


}
