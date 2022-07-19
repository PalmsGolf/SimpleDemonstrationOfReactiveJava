package com.mike.projectreactortest.services;

import com.mike.projectreactortest.services.utils.ReactiveWebClientTestUtils;
import com.mike.projectreactortest.testUtils.MockedWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReactiveWebClientTestWithMockedServer {
    private final static String REQUEST_URI = "home";
    private MockedWebServer mockedWebServer;

    @Autowired
    private ReactiveWebClientTestUtils testUtils;

    @Autowired
    private ReactiveWebClient webClient;

    @BeforeAll
    void beforeAll() throws IOException {
        this.mockedWebServer = new MockedWebServer();
    }

    @AfterAll
    void afterAll() throws IOException {
        this.mockedWebServer.shutdown();
    }

    @Test
    public void testMonoResponse() {
        final String response = "Hello there!";
        mockedWebServer.responseWith(HttpStatus.OK, response);

        final Mono<String> responseMono = this.webClient.getRequest(this.testUtils.createDummyHeaders(), REQUEST_URI, String.class, null);

        StepVerifier.create(responseMono)
                .expectNext(response)
                .verifyComplete();
    }

}
