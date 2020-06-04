package com.community.community.service;

import com.community.community.Mapper.QuestionMapper;
import com.community.community.Mapper.UserMapper;
import com.community.community.dto.PaginationDTO;
import com.community.community.dto.QuestionDTO;
import com.community.community.dto.QuestionQueryDTO;
import com.community.community.model.Question;
import com.community.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service        //项目中需要组装user和question时，需要中间层做这个事，service就是这个作用
public class QuestionService {

    @Autowired(required = false)
    private QuestionMapper questionMapper;

    @Autowired(required = false)
    private UserMapper userMapper;

    public PaginationDTO list(String search, Integer page, Integer size) {

        if (StringUtils.isNotBlank(search)) {
            String[] tags = StringUtils.split(search, " ");
            search = Arrays.stream(tags).collect(Collectors.joining("|"));
        }

        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        Integer totalCount;
        if (search == null) {
            totalCount = questionMapper.count();
        }
        else {
            questionQueryDTO.setSearch(search);
            totalCount = (int) questionMapper.countBySearch(questionQueryDTO);
        }

        Integer totalPage;
        if (totalCount % size == 0) {   //totalCount是数据库有多少条数据，totalPage是数据库里的数据应该分多少页
            totalPage = totalCount/size;
        }
        else {
            totalPage = (totalCount/size) + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);
        //size*(page - 1)
        Integer offset = size * (page - 1);
        List<Question> questions;
        if (search == null) {
            questions = questionMapper.list(offset, size);
        }
        else {
            questionQueryDTO.setSize(size);
            questionQueryDTO.setPage(offset);
            questions = questionMapper.selectBySearch(questionQueryDTO);
        }
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO); //快速的把question的所有属性拷贝到questionDTO
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return paginationDTO;
    }

    public PaginationDTO list(Long userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        Integer totalCount = questionMapper.countByUserId(userId);
        Integer totalPage;
        if (totalCount % size == 0) {   //totalCount是数据库有多少条数据，totalPage是数据库里的数据应该分多少页
            totalPage = totalCount/size;
        }
        else {
            totalPage = (totalCount/size) + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        //size*(page - 1)
        Integer offset = size * (page - 1);
        List<Question> questions = questionMapper.listByUserId(userId, offset, size);
        List<QuestionDTO> questionDTOList = new ArrayList<>();

        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO); //快速的把question的所有属性拷贝到questionDTO
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);

        return paginationDTO;
    }

    public QuestionDTO getById(Long id) {
        Question question = questionMapper.getById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question, questionDTO); //快速的把question的所有属性拷贝到questionDTO
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            //create
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setViewCount(0);
            question.setCommentCount(0);
            question.setLikeCount(0);
            questionMapper.create(question);
        }
        else {
            //update
            question.setGmtModified(question.getGmtCreate());
            questionMapper.update(question);
        }
    }

    public void incViews(Long id) {
        Question question = questionMapper.getById(id);
        questionMapper.updateViewCount(question);//viewCount + 1
    }
    public void incCommentCount (Long id) {
        Question question = questionMapper.getById(id);
        questionMapper.updateCommentCount(question);//comment count + 1

    }

    public List<QuestionDTO> listRelatedQuestions(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        //先把tag改为以 | 隔开
        String[] tags = StringUtils.split(queryDTO.getTag(), ",");
        String regexpTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(regexpTag);
        //从db中查找有关各个tag的问题放入list
        List<Question> relatedQuestions = questionMapper.listRelatedQuestion(question);
        //把每个查到的question转换为questionDTO
        List<QuestionDTO> questionDTOS = relatedQuestions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
