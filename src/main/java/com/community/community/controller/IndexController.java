package com.community.community.controller;

import com.community.community.Mapper.UserMapper;
import com.community.community.dto.PaginationDTO;
import com.community.community.model.User;
import com.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private QuestionService questionService;

    @GetMapping("/")        //因为在AuthorizeController里最后redirect的是“/”，所以mapping的是“/”
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "2") Integer size) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();   //先从cookie里得到token
                    User user = userMapper.findByToken(token);//再从后台数据库看有没有该token值的user
                    if (user != null) {
                        request.getSession().setAttribute("user", user);//数据库中有从cookie里得到的token所对应的user，再把它存到session里
                    }
                    break;
                }
            }
        }

        PaginationDTO pagination = questionService.list(page, size);//原先是全部展示，input的page和size来控制分页
        model.addAttribute("pagination", pagination);
        return "index";
    }
}
