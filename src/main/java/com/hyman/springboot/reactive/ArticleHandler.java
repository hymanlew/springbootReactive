package com.hyman.springboot.reactive;

import com.hyman.springboot.entity.Article;
import com.hyman.springboot.normal.ArticleController;
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
        ArticleController.setArticle(articleList);
        return ok().contentType(APPLICATION_JSON_UTF8).body(BodyInserters.fromObject(articleList));
    }
}
