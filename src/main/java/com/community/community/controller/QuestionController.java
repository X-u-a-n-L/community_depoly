package com.community.community.controller;


import com.community.community.dto.CommentDTO;
import com.community.community.dto.QuestionDTO;
import com.community.community.service.CommentService;
import com.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired(required = false)
    private QuestionService questionService;

    @Autowired(required = false)
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model) {

        List<CommentDTO> comments = commentService.listByQuestionId(id);
        //increase the views
        questionService.incViews(id);
        QuestionDTO questionDTO = questionService.getById(id);
        model.addAttribute("question", questionDTO);
        model.addAttribute("comments", comments);//throw it in front end page
        return "question";
    }
}
