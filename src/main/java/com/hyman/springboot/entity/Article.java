package com.hyman.springboot.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Article {

    private Long id;
    private String title;
    private Date gmtCreate;
}
