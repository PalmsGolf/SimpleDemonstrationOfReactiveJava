package com.mike.projectreactortest.services;

import com.mike.projectreactortest.repository.MessagesRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

@SpringBootTest
public class HomeServiceTest {

    @Autowired
    private HomeService homeService;

    @MockBean
    private MessagesRepository messagesRepository;

    @Test
    public void test() {
        Mockito.when(this.messagesRepository.getMessages()).thenReturn(Flux.fromIterable(List.of("Mike", "Nooo")));
        Flux<String> fluxResponse = this.homeService.getHomeMessages();

        StepVerifier.create(fluxResponse)
                .expectSubscription()
                .expectNext("Mike")
                .expectNext("Nooo")
                .verifyComplete();
    }

}
