package com.community.community.model;

import lombok.Data;

@Data               //通过lombok自动生成getter setter hashcode等等方法
public class User {
    private Integer id;
    private String accountId;
    private String name;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;
    private String avatarUrl;
}
