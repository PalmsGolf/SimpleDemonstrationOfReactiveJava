package com.mike.projectreactortest.controllers;

import com.mike.projectreactortest.dto.MessageRequest;
import com.mike.projectreactortest.services.MessagesService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
public class MessagesControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private MessagesService messagesService;

    @Test
    void testGetMessage() {
        Mono<String> response = Mono.just("bla");
        Mockito.when(this.messagesService.getHomeMessage()).thenReturn(response);

        Flux<String> result = this.webClient
                .get()
                .uri("/message/{id}", 5)
                .exchange()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNext("bla")
                .verifyComplete();
    }

    @Test
    void testGetMessagesStream() {
        Flux<String> response = Flux.fromIterable(List.of("12 message", "2 message", "3 message", "4 message", "5 message"));

        Mockito.when(this.messagesService.getHomeMessages()).thenReturn(response);

        Flux<String> result = this.webClient
                .get()
                .uri("/stream/messages")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNext("12 message")
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    void testPutMessage() {
        this.webClient
                .put()
                .uri("/message")
                .accept(MediaType.APPLICATION_JSON)
                .headers(h -> h.set(HttpHeaders.USER_AGENT, "some_user_agent"))
                .bodyValue(new MessageRequest())
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void GetMessagesStreamBackpressure() {
        Flux<String> response = Flux.fromIterable(List.of("12 message", "2 message", "3 message", "4 message", "5 message"));
        response.limitRate(2);

        Mockito.when(this.messagesService.getHomeMessages()).thenReturn(response);

        FluxExchangeResult<String> result = this.webClient
                .get()
                .uri("/stream/messages")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class);

        StepVerifier.create(result.getResponseBody())
                .expectSubscription()
                .thenRequest(5)
                .expectNextCount(2)
                .expectNextCount(2)
                .expectNext("5 message")
                .verifyComplete();
    }

}
