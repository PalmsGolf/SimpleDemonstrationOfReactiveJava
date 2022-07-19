package com.mike.projectreactortest.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
public class MessagesServiceTest {

    @Autowired
    private MessagesService messagesService;

    @Test
    public void test() {
        //Mockito.when(this.messagesRepository.getMessages()).thenReturn(Flux.fromIterable(List.of("Mike", "Nooo")));
        Flux<String> fluxResponse = this.messagesService.getHomeMessages();

        StepVerifier.create(fluxResponse)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

}
