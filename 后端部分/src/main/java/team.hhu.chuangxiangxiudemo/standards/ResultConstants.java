package team.hhu.chuangxiangxiudemo.standards;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


/**
 * 函数状态统一规范枚举
 */

public enum ResultConstants
{
    OPERATION_UNDEFINED_ERROR(-1,"发生未定义的错误"),
    OPERATION_INVALID_PARAMETER(0x0,"参数无效"),
    OPERATION_OK(0x1, "操作成功"),
    OPERATION_TIMEDOUT(0x2,"请求超时"),
    ARTICLE_NOT_FOUND(0x10, "未找到指定新闻"),
    ARTICLE_INSERT_FAILED(0x11, "添加新闻失败"),
    COMMENT_NOT_FOUND(0x12,"未找到指定评论"),
    COMMENT_INSERT_FAILED(0x13,"添加评论失败"),
    IO_WRITEFILE_FAILED(0x21, "文件写入失败"),
    IO_FILE_NOT_FOUND(0x22, "找不到此文件"),
    IO_FILE_TOO_LARGE(0x23, "文件太大"),
    IO_READFILE_FAILED(0x24, "文件读取失败"),
    IO_UNDEFINED_ERROR(0x25, "读/写时发生未定义的错误"),
    DATA_ACCESS_ERROR(0x26,"数据访问异常"),
    USER_LOGIN_WRONG_PASSWORD(0x31,"密码错误"),
    USER_ACCOUNT_NOT_EXISTS(0x32,"用户不存在"),
    USER_USED_USERNAME_OR_EMAIL(0x33,"用户名或邮箱已被注册"),
    USER_USED_USERNAME(0x33,"用户名已被注册"),
    USER_USED_EMAIL(0x33,"邮箱已被注册"),
    USER_VERIFICATION_INVALID_CODE(0x35,"验证码已经过期或错误"),
    USER_VERIFICATION_EMAIL_SEND_FAILED(0x36,"发送邮件失败"),
    USER_VERIFICATION_INVALID_EMAIL_ADDRESS(0x37,"验证邮箱无效"),
    USER_INVALID_USERNAME(0x38,"无效的用户名"),
    USER_VERIFICATION_INVALID_TOKEN(0x39,"无效的令牌"),
    USER_ACCESS_DENIED(0x40,"当前用户无权访问"),
    USER_STATE_BLOCKED_ACCOUNT(0x41,"用户已被封禁"),
    IMAGE_FILE_TOO_LARGE(0x42,"图片大小过大"),
    IMAGE_FORMAT_NOT_SUPPORTED(0x43,"不支持此图片格式"),
    API_UNEXPECTED_ERROR(0x50,"API错误");

    private final String message;
    private final int statusCode;

    ResultConstants(int statusCode, String message)
    {
        this.message = message;
        this.statusCode = statusCode;
    }

    public @NotNull Map<String,Object> getMap()
    {
        Map<String,Object> map=new HashMap<>();
        map.put("code",this.statusCode);
        map.put("message",this.message);
        map.put("success",this.statusCode==1 ? 1:0);
        return map;
    }

    public String getMessage()
    {
        return message;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

}
