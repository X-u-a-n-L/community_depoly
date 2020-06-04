package com.community.community.controller;

import com.community.community.dto.PaginationDTO;
import com.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    @Autowired(required = false)
    private QuestionService questionService;

    @GetMapping("/")        //因为在AuthorizeController里最后redirect的是“/”，所以mapping的是“/”
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "5") Integer size,
                        @RequestParam(name = "search", required = false) String search) {

        PaginationDTO pagination = questionService.list(search, page, size);//原先是全部展示，input的page和size来控制分页
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);
        return "index";
    }
}
