package com.community.community.service;

import com.community.community.Mapper.UploadMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public class UploadedFileService {
    @Autowired
    private UploadMapper uploadMapper;

    //插入
    public int insertUrl(String name,String path,String url){
        System.out.print("开始插入=name=="+name+"\n");
        System.out.print("开始插入=lujing=="+path+"\n");
        System.out.print("开始插入=url=="+url+"\n");
        int result=uploadMapper.insertUrl(name,path,url);
        System.out.print("插入结果==="+result+"\n");
        return result;
    }
}
