package team.hhu.chuangxiangxiudemo.article.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import team.hhu.chuangxiangxiudemo.article.dto.LikesTableCoordinates;

@Component
@Mapper
public interface ILikesTableDao
{
    /**
     * CREATE
     */
    void createLikesTable(@Param("tableId")long tableId);

    void createUserLikeState(@Param("pos") LikesTableCoordinates coordinates);

    /**
     * RETRIEVE
     */
    Integer checkUserLikeState(@Param("pos")LikesTableCoordinates coordinates);

    /**
     * UPDATE
     */

    void switchUserLikeState(@Param("pos")LikesTableCoordinates coordinates);

    /**
     * DELETE
     */

    void deleteUserLikeState(@Param("pos")LikesTableCoordinates coordinates);

    void deleteLikesList(@Param("tableId")long tableId);

    void batchDeleteLikesList(@Param("tableIdPrefix")long tableIdPrefix);
}
