package com.community.community.controller;

import com.community.community.dto.AccessTokenDTO;
import com.community.community.dto.GithubUser;
import com.community.community.model.User;
import com.community.community.provider.GithubProvider;
import com.community.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j      //print log
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")       //在property中赋值的一种方法
    private String client_id;

    @Value("${github.client.secret}")
    private String client_secret;

    @Value("${github.redirect.uri}")
    private String redirect_uri;

    @Autowired(required = false)
    private UserService userService;

    @GetMapping("/callback")  //操作后缀有callback的位置
    public String callback(@RequestParam(name="code") String code,  //得到/callback后面变量名为code的值赋给string code
                           @RequestParam(name="state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        //输入用户名密码成功后，github会redirect到index写里的/authorize地址，并携带者一个code（会在设置的callback的uri后面？code=...&state=...）
        //社区需要再携带这个github传回来的code，来获取access_token
        //github会返回access_token
        //社区调用github的user api并携带access_token来获取user信息
        //如果access_token正确，github会返回给社区user的信息
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setState(state);
        accessTokenDTO.setRedirect_uri(redirect_uri);
        accessTokenDTO.setClient_id(client_id);
        accessTokenDTO.setClient_secret(client_secret);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getUser(accessToken);
        //System.out.println(user.getName());
        if (githubUser != null) {
            //已经连接成功github，创建数据库model user加入到数据库中，为了保持登陆状态
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(githubUser.getName());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setAvatarUrl(githubUser.getAvatarUrl());
            userService.createOrUpdate(user);
            response.addCookie(new Cookie("token", token));
            //登陆成功，写cookie和session
            //request.getSession().setAttribute("user", githubUser);
            //return "redirect:/";
        }
        else {
            //登陆失败，重新登陆
            log.error("callback get github error, {}", githubUser);
            return "redirect:/";
        }
        return "redirect:/"; //这个redirect可以改变url为“/”，但是页面还是当前页面 **还需再查一下具体
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request,//session set by request, cookie set by response
                         HttpServletResponse response) {

        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token", null);//delete cookie
        cookie.setMaxAge(0);//delete right now
        response.addCookie(cookie);
        return "redirect:/";
    }

}
