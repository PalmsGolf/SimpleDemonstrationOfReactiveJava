package com.mike.projectreactortest.services;

import com.mike.projectreactortest.repository.MessagesRepository;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HomeService {
    private final static String GET_HOME_MESSAGE_PATH = "home";
    @Getter
    final ReactiveWebClient webClient;
    final MessagesRepository messagesRepository;

    public HomeService(final ReactiveWebClient webClient, final MessagesRepository messagesRepository) {
        this.webClient = webClient;
        this.messagesRepository = messagesRepository;
    }

    public Mono<String> getHomeMessage() {
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("action", "get");

        return this.webClient.getRequest(HttpHeaders.EMPTY, GET_HOME_MESSAGE_PATH, String.class, queryParams);
    }

    public Flux<String> getHomeMessages() {
        return this.messagesRepository.getMessages();
    }
}
