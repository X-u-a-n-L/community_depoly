package com.community.community.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UploadMapper {

    @Insert({"insert into UPLOADEDFILES (name,path,url) values (#{name},#{path},#{url})"})
    public int insertUrl(@Param("name")String name, @Param("path")String path, @Param("url")String url);
}
