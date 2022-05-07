package team.hhu.chuangxiangxiudemo.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoBasic;
import team.hhu.chuangxiangxiudemo.user.pojo.UserInfoEx;


@Component
@Mapper
public interface IUserInfoDao
{

    /**
     *CREATE
     */
    <T> void createUserInfo(@Param("type")String type, @Param("info")T info,
                            @Param("encryptKey")String encryptKey);
    void createUserArticleTable(@Param("userId")int userId);


    /**
     *RETRIEVE
     */
    UserInfoBasic retrieveUserInfoBasic(@Param("keyColumn")String byWhichColumn,
                                        @Param("keyVal")String keyVal, @Param("encryptKey")String encryptKey);

    UserInfoEx retrieveUserInfoEx(@Param("userId")int userId);

    String retrieveUserAliasById(@Param("userId")int userId);

    String retrieveUserAvatarById(@Param("userId")int userId);


    /**
     *UPDATE
     */

    <T> void updateUserInfo(@Param("type")String type, @Param("userId")int userId, @Param("info")T info,
                            @Param("encryptKey")String encryptKey);

    void increaseUserLikesCount(@Param("userId")int userId);

    void decreaseUserLikesCount(@Param("userId")int userId);

    void increaseUserArticleCount(@Param("userId")int userId);

    void decreaseUserArticleCount(@Param("userId")int userId);

    void updateUserPassword(@Param("userId")int userId,@Param("password")String password,@Param("encryptKey")String encryptKey);

    void updateUserName(@Param("userId")int userId, @Param("username")String username, @Param("encryptKey")String encryptKey);

    void updateUserAvatar(@Param("userId")int userId,@Param("avatar")String avatar);

    /**
     *DELETE
     */

    void deleteUserInfo(@Param("userId")int userId);

}
