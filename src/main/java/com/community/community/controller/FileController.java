package com.community.community.controller;

import com.community.community.dto.FileDTO;
import com.community.community.service.UploadedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class FileController {

    @Autowired(required = false)
    private UploadedFileService uploadedFileService;

    private String  url;

    @RequestMapping(value = "/file/upload" ,produces="application/json;charset=UTF-8")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request) throws Exception {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");

        System.out.print("上传文件==="+"\n");
        //判断文件是否为空
        if (file.isEmpty()) {
            throw new Exception("上传文件不可为空");
        }


        // 获取文件名
        String fileName = file.getOriginalFilename();
//        System.out.print("上传的文件名为: "+fileName+"\n");

        fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + fileName;
        System.out.print("（加个时间戳，尽量避免文件名称重复）保存的文件名为: "+fileName+"\n");


        //加个时间戳，尽量避免文件名称重复
        String path = "/root/photo" +fileName;
        //String path = "E:/fileUpload/" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + "_" + fileName;
        //文件绝对路径
        System.out.print("保存文件绝对路径"+path+"\n");

        //创建文件路径
        File dest = new File(path);

        //判断文件是否已经存在
        if (dest.exists()) {
            throw new Exception( "文件已经存在");
        }

        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }

        try {
            //上传文件
            file.transferTo(dest); //保存文件
            System.out.print("保存文件路径"+path+"\n");
            url="http://139.9.114.118/image/"+fileName;
            int result= uploadedFileService.insertUrl(fileName,path,url);
            System.out.print("插入结果"+result+"\n");
            System.out.print("保存的完整url===="+url+"\n");
            FileDTO fileDTO = new FileDTO();
            fileDTO.setSuccess(1);
            fileDTO.setUrl(url);
            //fileDTO.setMessage();
            return fileDTO;

        } catch (IOException e) {
            throw new Exception("上传失败");
        }
    }
}
