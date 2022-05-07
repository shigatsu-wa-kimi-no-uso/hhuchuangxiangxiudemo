package team.hhu.chuangxiangxiudemo.article.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import team.hhu.chuangxiangxiudemo.article.dto.ArticleCoordinates;
import team.hhu.chuangxiangxiudemo.article.pojo.ArticleAtom;

import java.sql.Timestamp;
import java.util.List;

@Component
@Mapper
public interface IArticleDao
{

    /**
     * CREATE
     */
    void initArticleInfo(@Param("info") ArticleAtom info);

    void initArticleInfoSp(@Param("info")ArticleAtom info);

    /**
     *RETRIEVE
     */

    List<ArticleCoordinates> retrieveArticleCoordinatesBeforeTimeByCategory(@Param("categoryId")int categoryId, @Param("time") Timestamp time, @Param("selectNumber")int selectNumber);

    List<ArticleAtom> retrieveArticleAtomsBeforeTimeByPoster(@Param("posterId")int posterId,@Param("time")Timestamp time, @Param("selectNumber")int selectNumber);

    ArticleAtom retrieveSingleArticleAtom(@Param("pos") ArticleCoordinates coordinates);

    int getArticleAtomCountByCategory(@Param("categoryId")int categoryId);

    String retrieveArticleTitle(@Param("pos") ArticleCoordinates coordinates);


    /**
     *UPDATE
     */
    void updateArticleTitle(@Param("pos") ArticleCoordinates coordinates, @Param("newTitle") String newTitle);

    void increaseArticleLikes(@Param("pos") ArticleCoordinates coordinates);

    void decreaseArticleLikes(@Param("pos") ArticleCoordinates coordinates);

    void increaseArticleComments(@Param("pos") ArticleCoordinates coordinates);

    void decreaseArticleComments(@Param("pos") ArticleCoordinates coordinates);

    void updateArticleCategory(@Param("pos")ArticleCoordinates coordinates,@Param("newCategoryId")int newCategoryId);

    void updateCategoryIndex(@Param("pos")ArticleCoordinates coordinates,@Param("oldCategoryId")int oldCategoryId,@Param("newCategoryId")int newCategoryId);

    /**
     *DELETE
     */

    void deleteArticleAtom(@Param("pos") ArticleCoordinates coordinates);

    void deleteCategoryIndex(@Param("pos")ArticleCoordinates coordinates,@Param("categoryId")int categoryId);


}
