package com.hyman.springboot.reactive;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyman.springboot.entity.Article;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.List;


/**
 * 单独测试 走端口 server
 *
 * 如何使用WebClient，http://www.leftso.com/blog/404.html。
 *
 * SpringBootTest单元测试及日志，https://blog.csdn.net/liubenlong007/article/details/85398181。
 *
 * SpringBoot 教程，https://blog.csdn.net/liubenlong007/article/list/6?。https://blog.csdn.net/Message_lx/article/list/3?
 *
 */
@AutoConfigureWebTestClient
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArticleControllerTestX {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testArticleList()  {
        this.webTestClient.get().uri("/article/list").exchange().expectStatus().isOk();
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testArticleList2() throws IOException {
        String body = this.restTemplate.getForObject("/article/list", String.class);
        List<Article> articleList = new ObjectMapper().readValue(body,new TypeReference<List<Article>>() {});
        Assert.assertEquals(java.util.Optional.of(articleList.get(0).getId()), 0L);
    }
}