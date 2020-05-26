package com.community.community.Mapper;

import com.community.community.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionMapper {

    @Insert("insert into question (title, description, gmt_create, gmt_modified, creator, tag) values (#{title}, #{description}, #{gmtCreate}, #{gmtModified}, #{creator}, #{tag})")
    void create(Question question);

    @Select("select * from Question limit #{offset}, #{size}")
    List<Question> list(@Param(value = "offset") Integer offset, @Param(value = "size") Integer size);

    @Select("select count(1) from question") //count(1)比count(*)更快。不过现在经过优化二者没什么区别了
    Integer count();

    @Select("select * from Question where creator = #{userId} limit #{offset}, #{size}")
    List<Question> listByUserId(@Param("userId") Long userId, @Param(value = "offset") Integer offset, @Param(value = "size") Integer size);

    @Select("select count(1) from question where creator = #{userId}")
    Integer countByUserId(Long userId);

    @Select("select * from Question where id = #{id}")
    Question getById(Long id);

    @Update("update question set title = #{title}, description = #{description}, tag = #{tag}, gmt_modified = #{gmtModified}, view_count = #{viewCount} where id = #{id}")
    void update(Question question);

    @Update("update question set view_count = (view_count + 1) where id = #{id}")
    void updateViewCount(Question updatedQuestion);

    @Update("update question set comment_count = (comment_count + 1) where id = #{id}")
    void updateCommentCount(Question question);

    @Select("select * from question where id != #{id} and tag regexp #{tag}")
    List<Question> listRelatedQuestion(Question question);
}
