package com.hyman.springboot.normal;

import com.hyman.springboot.Entity.Article;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 传统方式
 */
@RestController
public class ArticleController {

    @GetMapping("/article/list")
    public List<Article> articleList(){

        List<Article> articleList = new ArrayList<>(10);

        for(int i=0;i<10;i++) {
            Article article = new Article();
            article.setId((long)i);
            article.setTitle("SpringBoot入门案例"+i);
            article.setGmtCreate(new Date());
            articleList.add(article);
        }
        return articleList;
    }
}
