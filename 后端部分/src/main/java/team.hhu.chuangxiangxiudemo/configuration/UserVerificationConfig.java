package team.hhu.chuangxiangxiudemo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public final class UserVerificationConfig
{

    public static String EMAIL_FROM;
    public final static String EMAIL_SUBJECT ="邮箱验证";
    public final static String EMAIL_TEXT1="您的验证码为:";
    public final static long EMAIL_CODE_DURATION_MILLIS =1*60*1000;
    public final static String EMAIL_TEXT2 ="有效时间为"+ EMAIL_CODE_DURATION_MILLIS /(60*1000)+"分钟,请尽快使用";
    public final static long TOKEN_DURATION_MILLIS=7*24*60*60*1000;        //一星期的有效期
    public final static String TOKEN_KEY ="kiminouso030221";

    @Autowired
    UserVerificationConfig(@Value("${spring.mail.username}")String from)
    {
        this.EMAIL_FROM =from;
    }

}
