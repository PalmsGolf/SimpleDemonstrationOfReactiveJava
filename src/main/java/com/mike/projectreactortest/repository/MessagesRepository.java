package com.mike.projectreactortest.repository;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

//Dummy repository with mocked response
@Repository
public class MessagesRepository {

    public Flux<String> getMessages() {
        return Flux.fromIterable(List.of("Hello!", "HI HI"));
    }
}
