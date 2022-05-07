package team.hhu.chuangxiangxiudemo.user.pojo;


import java.sql.Date;


public class UserInfoBasic
{
    private int userId;
    private String userName;
    private String userPassword;
    private String userEmail;
    private Date regDate;
    private int userStateCode;

    public UserInfoBasic(){}

    public UserInfoBasic(String userName, String userEmail, String userPassword)
    {
        this.userName=userName;
        this.userEmail=userEmail;
        this.userPassword=userPassword;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserPassword()
    {
        return userPassword;
    }

    public void setUserPassword(String userPassword)
    {
        this.userPassword = userPassword;
    }

    public String getUserEmail()
    {
        return userEmail;
    }

    public void setUserEmail(String userEmail)
    {
        this.userEmail = userEmail;
    }

    public Date getRegDate()
    {
        return regDate;
    }

    public void setRegDate(Date regDate)
    {
        this.regDate = regDate;
    }

    public int getUserStateCode()
    {
        return userStateCode;
    }

    public void setUserStateCode(int userStateCode)
    {
        this.userStateCode=userStateCode;
    }

    @Override
    public String toString()
    {
        return "UserInfoBasic{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", regDate=" + regDate +
                ", userStateCode=" + userStateCode +
                '}';
    }
}
