package team.hhu.chuangxiangxiudemo.article.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.ArticleEx;

@Component
@Mapper
public interface IArticleExDao
{
    /**
     * CREATE
     */
    /**
     * 创建操作已在发表文章时进行
     */

    /**
     * RETRIEVE
     */

    ArticleEx retrieveSingleArticleEx(@Param("pos")ArticleCoordinates coordinates);

    /**
     * UPDATE
     */

    void updateArticleEx(@Param("pos")ArticleCoordinates coordinates,@Param("info") ArticleEx info);

    /**
     * DELETE
     */

    void deleteArticleEx(@Param("pos")ArticleCoordinates coordinates);
}
