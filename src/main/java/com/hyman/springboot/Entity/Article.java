package com.hyman.springboot.Entity;

import lombok.Data;

import java.util.Date;

@Data
public class Article {

    private Long id;
    private String title;
    private Date gmtCreate;
}
