package com.mike.projectreactortest.services;

import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MessagesService {
    private final static String GET_MESSAGE_PATH = "messages";

    @Getter
    final ReactiveWebClient webClient;

    public MessagesService(final ReactiveWebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> getHomeMessageFromExternalService() {
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("action", "get");

        return this.webClient.getRequest(new HttpHeaders(), GET_MESSAGE_PATH, String.class, queryParams);
    }

    public Mono<String> getHomeMessage() {
        return Mono.just("some message");
    }

    public Flux<String> getHomeMessages() {
        return Flux.fromIterable(List.of("1 message", "2 message", "3 message", "4 message", "5 message"));
    }
}
