package com.hyman.springboot.normal;

import com.hyman.springboot.entity.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 传统方式
 */
@RestController
public class ArticleController {

    @GetMapping("/article/list")
    public List<Article> articleList() {

        List<Article> articleList = new ArrayList<>(10);
        setArticle(articleList);
        return articleList;
    }

    public static void setArticle(List<Article> articleList) {

        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.setId((long) i);
            article.setTitle("SpringBoot入门案例" + i);
            article.setGmtCreate(new Date());
            articleList.add(article);
        }
    }


    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    @GetMapping("/article/list")
    public Flux articleList2() {
        return redisTemplate.keys("*");
    }

    @GetMapping("/article/save")
    public Mono<Boolean> articleSave() {
        return redisTemplate.opsForValue().set("x1", "1235");
    }
}
