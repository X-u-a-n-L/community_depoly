package com.community.community.interceptor;

import com.community.community.Mapper.UserMapper;
import com.community.community.model.User;
import com.community.community.model.UserExample;
import com.community.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service //给一个标识注解表明这是个spring托管的，要不然userMapper注入不进来
public class SessionInterceptor implements HandlerInterceptor {
    @Autowired (required = false)
    private UserMapper userMapper;

    @Autowired(required = false)
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();   //先从cookie里得到token
                    UserExample userExample = new UserExample();
                    userExample.createCriteria()
                            .andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    //User user = userMapper.findByToken(token);//再从后台数据库看有没有该token值的user
                    if (users.size() != 0) {
                        request.getSession().setAttribute("user", users.get(0));//数据库中有从cookie里得到的token所对应的user，再把它存到session里

                        Long unreadCount = notificationService.unreadCount(users.get(0).getId());//通过用户id找未读通知数量
                        request.getSession().setAttribute("unreadCount", unreadCount);
                    }
                    break;
                }
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
