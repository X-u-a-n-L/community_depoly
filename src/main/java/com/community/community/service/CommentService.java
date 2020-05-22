package com.community.community.service;

import com.community.community.Mapper.CommentMapper;
import com.community.community.Mapper.QuestionMapper;
import com.community.community.enums.CommentTypeEnum;
import com.community.community.model.Comment;
import com.community.community.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

    @Autowired(required = false)
    private CommentMapper commentMapper;

    @Autowired(required = false)
    private QuestionMapper questionMapper;

    @Autowired(required = false)
    private QuestionService questionService;

    @Transactional  //事务化，如果comment insert成功，increase comment number 没成功就会回滚comment insert， 因为是事务
    public void insert(Comment comment) throws Exception {
        if (comment.getParentId() == null || comment.getParentId() == 0 || comment.getType() == null) {
            throw new Exception("parent comment not found");
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //comment on parent comment
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new Exception("null");
            }
            commentMapper.insert(comment);
        } else {
            //no parent, comment on question
            Question question = questionMapper.getById(comment.getParentId());
            if (question == null) {
                throw new Exception("parent question not found");
            }
            commentMapper.insert(comment);
            questionService.incCommentCount(question.getId());
        }
    }

}
