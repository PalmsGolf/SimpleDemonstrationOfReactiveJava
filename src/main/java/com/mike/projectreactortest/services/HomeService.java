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

    public String getHomeMessage() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, "testUseragent");
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.set("action", "get");

        final Mono<String> responseMono = this.webClient.getRequest(headers, GET_HOME_MESSAGE_PATH, String.class, queryParams);

        return responseMono.block();
    }

    public Flux<String> getHomeMessages() {
        return this.messagesRepository.getMessages();
    }
}
