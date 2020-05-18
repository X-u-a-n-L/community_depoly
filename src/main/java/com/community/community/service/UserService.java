package com.community.community.service;

import com.community.community.Mapper.UserMapper;
import com.community.community.model.User;
import com.community.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired(required = false)
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        UserExample userExample = new UserExample();
        userExample.createCriteria()
                .andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(userExample);
        //User dbUser = userMapper.findByAccountId(user.getAccountId());//get the user from database
        if (users.size() == 0) {
            //insert user
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }
        else {
            User dbUser = users.get(0);
            User updateUser = new User();
            //update no need to set create time
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());
            UserExample example = new UserExample();
            example.createCriteria()
                    .andIdEqualTo(dbUser.getId());
            userMapper.updateByExampleSelective(updateUser, example);
            //selective means null column will not be updated,
            // so we need create new updateUser instead of using the old dbUser


            // userMapper.update(dbUser);
        }

    }
}
