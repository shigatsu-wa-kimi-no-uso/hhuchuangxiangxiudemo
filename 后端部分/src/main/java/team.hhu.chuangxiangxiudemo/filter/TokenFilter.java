package team.hhu.chuangxiangxiudemo.filter;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import team.hhu.chuangxiangxiudemo.standards.OperationResult;
import team.hhu.chuangxiangxiudemo.standards.ResultConstants;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoBasic;
import team.hhu.chuangxiangxiudemo.user.service.UserVerificationService;
import team.hhu.chuangxiangxiudemo.utility.LogUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class TokenFilter implements Filter
{
    @Autowired
    UserVerificationService userVerificationService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, @NotNull FilterChain filterChain) throws IOException, ServletException
    {
        String url=((HttpServletRequest)servletRequest).getRequestURI();
        LogUtil.info("filter获得url:"+url);
        if(url!=null)
        {
            JSONObject result = new JSONObject();
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.setContentType("application/json;charset=utf-8");
            String token = ((HttpServletRequest) servletRequest).getHeader("token");
            if (token != null)
            {
                OperationResult<UserInfoBasic> serviceResult = userVerificationService.verifyToken(token);
                if (serviceResult.getStatus() == ResultConstants.OPERATION_OK && serviceResult.getReturnValue() != null)
                {
                    String username = serviceResult.getReturnValue().getUserName();
                    int userId = serviceResult.getReturnValue().getUserId();
                    LogUtil.info("token验证通过, 用户信息为:" + username + " " + userId);
                    servletRequest.setAttribute("username", username);
                    servletRequest.setAttribute("userId", userId);
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
                else
                {
                    if (!url.startsWith("/op"))
                    {
                        filterChain.doFilter(servletRequest, servletResponse);
                        return;
                    }
                    result.putAll(serviceResult.getStatus().getMap());
                    LogUtil.info("token验证失败");
                }
            }
            else
            {
                if (!url.startsWith("/op"))
                {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
                LogUtil.info("缺少令牌");
                result.putAll(ResultConstants.USER_VERIFICATION_INVALID_TOKEN.getMap());
            }
            System.out.println(result);
            servletResponse.getWriter().append(result.toString());
        }

    }
}
