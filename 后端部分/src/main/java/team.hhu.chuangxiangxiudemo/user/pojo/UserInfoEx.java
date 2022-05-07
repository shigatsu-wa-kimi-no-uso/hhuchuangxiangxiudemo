package team.hhu.chuangxiangxiudemo.user.pojo;



import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


public class UserInfoEx
{
    private String gender;
    private String alias;
    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date birthday;
    private int age;
    private String signature;
    private String avatar;

    @Override
    public String toString()
    {
        return "UserInfoEx{" +
                "gender='" + gender + '\'' +
                ", alias='" + alias + '\'' +
                ", birthday=" + birthday +
                ", age=" + age +
                ", signature='" + signature + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }


    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getAlias()
    {
        return alias;
    }

    public void setAlias(String alias)
    {
        this.alias = alias;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge(int age)
    {
        this.age = age;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }
}
