package team.hhu.chuangxiangxiudemo.user.service;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import team.hhu.chuangxiangxiudemo.configuration.UserVerificationConfig;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.constant.UserInfoSQLColumnName;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoBasic;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Service
@Slf4j
public class UserVerificationService
{
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private UserInfoService userInfoService;

    public OperationResult<Void> sendVerificationEmail(HttpSession httpSession,String userEmail)
    {
        OperationResult<Void> result=new OperationResult<>();
        MimeMessage mime=emailSender.createMimeMessage();
        MimeMessageHelper message=new MimeMessageHelper(mime,"UTF-8");
        int verificationCode;
        try
        {
            message.setFrom(UserVerificationConfig.EMAIL_FROM);
            message.setTo(userEmail);
            message.setSubject(UserVerificationConfig.EMAIL_SUBJECT);
            verificationCode = new Random(new Random(System.nanoTime()).nextLong()).nextInt(900000) + 100000;
            String text = UserVerificationConfig.EMAIL_TEXT1 + verificationCode + UserVerificationConfig.EMAIL_TEXT2;
            message.setText(text);
            emailSender.send(mime);
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            result.setStatus(ResultConstants.USER_VERIFICATION_EMAIL_SEND_FAILED);
            return result;
        }
        httpSession.setAttribute("verificationCodeSetTime",System.currentTimeMillis());
        httpSession.setAttribute("verificationCode",String.valueOf(verificationCode));
        httpSession.setAttribute("email",userEmail);
        result.setStatus(ResultConstants.OPERATION_OK);
        return result;
    }

    /**
     * token认证
     * 代码来自https://blog.csdn.net/weixin_42408447/article/details/117815293
     */

    public OperationResult<String> createToken(String userId,String userName)
    {
        OperationResult<String> result=new OperationResult<>();
        Map<String,Object> header = new HashMap<>();
        header.put("typ","JWT");
        header.put("alg","HS256");
        //setID:用户ID
        //setExpiration:token过期时间  当前时间+有效时间
        //setSubject:用户名
        //setIssuedAt:token创建时间
        //signWith:加密方式
        JwtBuilder builder = Jwts.builder().setHeader(header)
                .setId(userId)
                .setExpiration(new Date(System.currentTimeMillis()+UserVerificationConfig.TOKEN_DURATION_MILLIS))
                .setSubject(userName)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256,UserVerificationConfig.TOKEN_KEY);
        result.setStatus(ResultConstants.OPERATION_OK);
        result.setReturnValue(builder.compact());
        return result;
    }

    public OperationResult<UserInfoBasic> verifyToken(String token)
    {
        OperationResult<UserInfoBasic> result=new OperationResult<>();
        Claims claims;
        try
        {
            //token过期后，会抛出ExpiredJwtException 异常，通过这个来判定token过期，
            //假的token 抛出SignatureException
            claims = Jwts.parser().setSigningKey(UserVerificationConfig.TOKEN_KEY).parseClaimsJws(token).getBody();
        }
        catch (SignatureException e)
        {
            LogUtil.info("检测到假令牌");
            result.setStatus(ResultConstants.USER_VERIFICATION_INVALID_TOKEN);
            return result;
        }
        catch (ExpiredJwtException e)
        {
            LogUtil.info("检测到失效令牌");
            result.setStatus(ResultConstants.USER_VERIFICATION_INVALID_TOKEN);
            return result;
        }

        //从token中获取用户id，查询该Id的用户是否存在，存在则token验证通过
        String targetId = claims.getId();

        OperationResult<UserInfoBasic> serviceResult=userInfoService.getUserInfoBasic(UserInfoSQLColumnName.USER_ID,targetId);

        if(serviceResult.getStatus()==ResultConstants.OPERATION_OK)
        {
            UserInfoBasic info=serviceResult.getReturnValue();
            if(info!=null)
            {
                result.setStatus(ResultConstants.OPERATION_OK);
                LogUtil.info("token匹配成功");
                result.setReturnValue(info);
                return result;

            }
            result.setStatus(ResultConstants.USER_VERIFICATION_INVALID_TOKEN);
            LogUtil.info("令牌匹配失败");
            return result;
        }
        LogUtil.info("拥有令牌,但数据库中无此用户或出现数据访问错误");
        result.setStatus(serviceResult.getStatus());
        return result;
    }


    public OperationResult<Void> verifyEmail(@NotNull HttpSession httpSession, String verificationCode,String email)
    {
        OperationResult<Void> result=new OperationResult<>();
        if(httpSession.getAttribute("verificationCodeSetTime")==null || httpSession.getAttribute("verificationCode")==null||httpSession.getAttribute("email")==null)
        {
            result.setStatus(ResultConstants.USER_VERIFICATION_INVALID_CODE);
            LogUtil.info("未申请验证码");
            return result;
        }

        if(System.currentTimeMillis()-(Long)httpSession.getAttribute("verificationCodeSetTime") > UserVerificationConfig.EMAIL_CODE_DURATION_MILLIS)
        {
            result.setStatus(ResultConstants.USER_VERIFICATION_INVALID_CODE);
            LogUtil.info("验证码已经失效");
            return result;
        }
        if(!verificationCode.equals(httpSession.getAttribute("verificationCode")))
        {
            LogUtil.info("验证码错误");
            result.setStatus(ResultConstants.USER_VERIFICATION_INVALID_CODE);
            return result;
        }
        if(!email.equals(httpSession.getAttribute("email")))
        {
            log.info("验证邮箱与验证码接收邮箱不匹配");
            result.setStatus(ResultConstants.USER_VERIFICATION_INVALID_EMAIL_ADDRESS);
            return result;
        }
        LogUtil.info("验证码验证通过");
        httpSession.removeAttribute("verificationCode");
        httpSession.removeAttribute("verificationCodeSetTime");
        httpSession.removeAttribute("email");
        result.setStatus(ResultConstants.OPERATION_OK);
        return result;
    }

}
