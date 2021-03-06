package com.hyman.springboot.reactive;

import com.hyman.springboot.normal.ArticleController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/**
 * Jetty和tomcat的比较
 * 相同点：Tomcat和Jetty都是一种Servlet引擎，他们都支持标准的servlet规范和JavaEE的规范。
 *
 * 不同点：
 * 1，架构比较
 * Jetty的架构比Tomcat的更为简单，Jetty的架构是基于Handler来实现的，主要的扩展功能都可以用Handler来实现，扩展简单。
 * Tomcat的架构是基于容器设计的，进行扩展是需要了解Tomcat的整体设计结构，不易扩展。
 *
 * 2，性能比较
 * Jetty和Tomcat性能方面差异不大。
 * Jetty可以同时处理大量连接而且可以长时间保持连接，适合于web聊天应用等等。Jetty的架构简单，因此作为服务器，Jetty可以按
 * 需加载组件，减少不需要的组件，减少了服务器内存开销，从而提高服务器性能。
 * Jetty默认采用NIO结束在处理I/O请求上更占优势，在处理静态资源时，性能较高
 *
 * Tomcat适合处理少数非常繁忙的链接，也就是说链接生命周期短的话，Tomcat的总体性能更高。
 * Tomcat默认采用BIO处理I/O请求，在处理静态资源时，性能较差。
 *
 * 3，其它比较
 * Jetty的应用更加快速，修改简单，对新的Servlet规范的支持较好。
 * Tomcat目前应用比较广泛，对JavaEE和Servlet的支持更加全面，很多特性会直接集成进来。
 */

/**
 * 单独测试 走 mock
 */
@RunWith(SpringRunner.class)
@WebFluxTest(ArticleController.class)
public class ArticleControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testArticleList() throws Exception {
        // mockMvc 模拟发起请求
        this.webClient.get().uri("/article/list")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                // 断言 (状态码)
                .expectStatus().isOk();


    }
}