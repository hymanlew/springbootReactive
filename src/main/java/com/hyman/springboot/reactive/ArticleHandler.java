package com.hyman.springboot.reactive;

import com.hyman.springboot.Entity.Article;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class ArticleHandler {

    public Mono<ServerResponse> articleList(ServerRequest request) {
        List<Article> articleList = new ArrayList<>(11);

        for(int i=0;i<11;i++) {
            Article article = new Article();
            article.setId((long)i);
            article.setTitle("SpringBoot入门案例"+i);
            article.setGmtCreate(new Date());

            articleList.add(article);
        }
        return ok().contentType(APPLICATION_JSON_UTF8).body(BodyInserters.fromObject(articleList));
    }
}
