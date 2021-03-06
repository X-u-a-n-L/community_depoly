package com.community.community.service;

import com.community.community.Mapper.*;
import com.community.community.dto.CommentDTO;
import com.community.community.enums.CommentTypeEnum;
import com.community.community.enums.NotificationStatusEnum;
import com.community.community.enums.NotificationTypeEnum;
import com.community.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired(required = false)
    private CommentMapper commentMapper;

    @Autowired(required = false)
    private QuestionMapper questionMapper;

    @Autowired(required = false)
    private QuestionService questionService;

    @Autowired(required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private CommentExtMapper commentExtMapper;

    @Autowired(required = false)
    private NotificationMapper notificationMapper;

    @Transactional  //事务化，如果comment insert成功，increase comment number 没成功就会回滚comment insert， 因为是事务
    public void insert(Comment comment, User commentator) throws Exception {
        if (comment.getParentId() == null || comment.getParentId() == 0 || comment.getType() == null) {
            throw new Exception("parent comment not found");
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            //comment on parent comment
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if (dbComment == null) {
                throw new Exception("null");
            }

            Question question = questionMapper.getById(dbComment.getParentId());
            if (question == null) {
                throw new Exception("parent question not found");
            }

            commentMapper.insert(comment);

            //增加评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);
            //创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_COMMENT, question.getId());
        } else {
            //no parent, comment on question
            Question question = questionMapper.getById(comment.getParentId());
            if (question == null) {
                throw new Exception("parent question not found");
            }
            comment.setCommentCount(0);
            commentMapper.insert(comment);
            questionService.incCommentCount(question.getId());

            //创建通知
            createNotify(comment, question.getCreator(), commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION, question.getId());
        }
    }
                            //参数：【创建者，收到通知的人，发出通知的人（回复的人），通知的标题，类型（回复问题还是回复评论），问题的id（这个id用来点击返回到问题页面）】
    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType, Long outerId) {
        if (receiver == comment.getCommentator()) {
            return;                         //自己不收自己的通知
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationType.getType());
        notification.setOuterid(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver);
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByQuestionIdOrCommentId(Long id, CommentTypeEnum type) {
        CommentExample example = new CommentExample();
        example.createCriteria()
                .andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        example.setOrderByClause("like_count desc");  //set comment order by like_count descending
        List<Comment> comments = commentMapper.selectByExample(example);

        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        //find all commentators without duplicate
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds = new ArrayList<>();
        userIds.addAll(commentators); //switch set to list

        //get commentator and store the results in Map (reduce the time complexity)
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));//put user and his id in a map

        //switch comment to commentDTO
        List<CommentDTO> commentDTOS = comments.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment, commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
