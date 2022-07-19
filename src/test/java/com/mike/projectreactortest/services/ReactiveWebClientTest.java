package com.mike.projectreactortest.services;

import com.mike.projectreactortest.exceptions.DataBaseException;
import com.mike.projectreactortest.services.utils.ReactiveWebClientTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"rawtypes", "unchecked"})
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReactiveWebClientTest {
    private final static String REQUEST_URI = "home";

    @Autowired
    private ReactiveWebClient webClient;

    @Autowired
    private ReactiveWebClientTestUtils testUtils;

    @MockBean
    private WebClient webClientMock;

    @Captor
    private ArgumentCaptor<URI> uriArgumentCaptor;

    @Test
    public void testRetryOnWhenRetryStrategyMet() {
        final String exceptionMessage = "some_message";
        final DataBaseException exception = new DataBaseException(exceptionMessage);
        mockWebClientResponse(exception, HttpMethod.GET);

        final Mono<String> responseMono = this.webClient.getRequest(this.testUtils.createDummyHeaders(), REQUEST_URI, String.class, null);
        final AtomicBoolean isCalled = new AtomicBoolean(false);
        responseMono.subscribe(
                value -> {},
                error -> isCalled.set(true),
                () -> {});

        Assertions.assertTrue(isCalled.get());
    }

    @Test
    public void testRetryOnWhenRetryStrategyNotMet() {
        final String exceptionMessage = "some_message";
        final IllegalArgumentException exception = new IllegalArgumentException(exceptionMessage);
        mockWebClientResponse(exception, HttpMethod.GET);

        final Mono<String> responseMono = this.webClient.getRequest(this.testUtils.createDummyHeaders(), REQUEST_URI, String.class, null);

        StepVerifier.create(responseMono)
                .expectSubscription()
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals(exceptionMessage))
                .verify();
    }


    private void mockWebClientResponse(final Object response, final HttpMethod httpMethod) {
        final WebClient.RequestHeadersUriSpec headersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        final WebClient.RequestHeadersSpec headersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        final WebClient.ResponseSpec responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

        mockWebClientMethod(httpMethod, headersUriSpecMock);
        Mockito.when(headersUriSpecMock.uri(this.uriArgumentCaptor.capture())).thenReturn(headersSpecMock);
        Mockito.when(headersSpecMock.header(Mockito.notNull(), Mockito.notNull())).thenReturn(headersSpecMock);
        Mockito.when(headersSpecMock.headers(Mockito.notNull())).thenReturn(headersSpecMock);
        Mockito.when(headersSpecMock.retrieve()).thenReturn(responseSpecMock);

        if (response instanceof final String responseAsString) {
            Mockito.when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<String>>notNull()))
                    .thenReturn(Mono.just(responseAsString));
        } else if (response instanceof final Exception responseAsException) {
            Mockito.when(responseSpecMock.bodyToMono(ArgumentMatchers.<Class<String>>notNull()))
                    .thenReturn(Mono.error(responseAsException));
        } else {
            Assertions.fail("Response type is not handled.");
        }
    }

    private void mockWebClientMethod(final HttpMethod httpMethod, final WebClient.RequestHeadersUriSpec uriSpecMock) {
        switch (httpMethod) {
            case GET -> Mockito.when(this.webClientMock.get()).thenReturn(uriSpecMock);
            case DELETE -> Mockito.when(this.webClientMock.delete()).thenReturn(uriSpecMock);
            default -> Assertions.fail("Provided HttpMethod can not be mocked.");
        }
    }
}
