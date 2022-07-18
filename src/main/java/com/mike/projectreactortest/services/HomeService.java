package com.mike.projectreactortest.services;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeService {
    @Getter
    final ReactiveWebClient webClient;

    public HomeService(final ReactiveWebClient webClient) {
        this.webClient = webClient;
    }


    public List<String> getHomePageMessages() {

        return List.of("HELLO", "HI");
    }
}
