package team.hhu.chuangxiangxiudemo.article.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import team.hhu.chuangxiangxiudemo.article.dto.CommentCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.CommentInfo;

import java.sql.Timestamp;
import java.util.List;


@Component
@Mapper
public interface ICommentDao
{
    /**
     *CREATE
     */

    void createCommentTable(@Param("tableId")long tableId);

    void createCommentInfo(@Param("pos")CommentCoordinates coordinates,@Param("content")String content, @Param("posterId")int posterId);

    /**
     *RETRIEVE
     */
    /**
     * 查询结果为空时返回null
     */
    CommentInfo retrieveSingleCommentInfo(@Param("pos") CommentCoordinates coordinate);

    List<String> retrieveCommentContentBeforeTime(@Param("tableId")long tableId, @Param("time")Timestamp time, @Param("selectNumber")int selectNumber);

    /**
     * 查询结果为空时返回null
     */
    List<CommentInfo> retrieveCommentInfoBeforeTime(@Param("tableId")long tableId, @Param("time")Timestamp time, @Param("selectNumber")int selectNumber);

    /**
     *UPDATE
     */

    void increaseCommentLikes(@Param("pos") CommentCoordinates coordinate);

    void decreaseCommentLikes(@Param("pos") CommentCoordinates coordinate);

    /**
     *DELETE
     */

    void deleteCommentInfo(@Param("pos") CommentCoordinates coordinate);

    void deleteCommentList(@Param("tableId")long tableId);



}
