package com.community.community.dto;

import lombok.Data;

@Data
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
    private String avatarUrl;  //因为github返回的json里就是avatar_url,要一样的对应
                                //但是fastjson可以自动下划线驼峰转换所以也可以写成更符合java编程规范的驼峰形式
}
