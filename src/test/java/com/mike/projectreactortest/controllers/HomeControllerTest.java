package com.mike.projectreactortest.controllers;

import com.mike.projectreactortest.services.HomeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = HomeController.class)
@Import(HomeService.class)
public class HomeControllerTest {

    @MockBean
    private HomeService homeService;

    @Autowired
    private WebTestClient webClient;

    @Test
    void testGetMessage() {
        final String response = "some_response";
        Mono<String> responseMono = Mono.just(response);

        Mockito.when(homeService.getHomeMessage()).thenReturn(responseMono);

        this.webClient.get().uri("/home/message", "Test")
                .header(HttpHeaders.USER_AGENT, "some_user_agent")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class);

        Mockito.verify(homeService, Mockito.atLeastOnce()).getHomeMessage();
    }
}
