package com.mike.projectreactortest.services;

import com.mike.projectreactortest.exceptions.DataBaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetrySpec;

import java.net.URI;
import java.util.function.Predicate;

@Service
public class ReactiveWebClient {
    public static final String TOKEN_HEADER_NAME = "some_header";
    public static final String TOKEN_HEADER_VALUE = "TJaO9YhSt5w7dLxujwLjvg==";
    public static final String EMERGENCY_TOKEN_HEADER_VALUE = "89aFKKA@--22FFwLjvg==";
    private final WebClient webClient;

    @Value("${messages-server.base-url}")
    private String serverBaseUrl;

    public ReactiveWebClient(final WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> Mono<T> getRequest(final HttpHeaders headers, final String requestPath, final Class<T> elementClass, final MultiValueMap<String, String> queryParams) {
        prepareRequestHeader(headers);                               // Prepare headers
        final URI uri = getRequestUri(requestPath, queryParams);     // Prepare URI
        final RetrySpec retrySpec = getRetrySpec(headers);           // Prepare our retry strategy

        return this.webClient                                        // Using Reactive Web Client
                .get()                                               // Defines request method
                .uri(uri)                                            // Defines request URI
                .headers(consumer -> consumer.putAll(headers))       // Defines request headers
                .retrieve()                                          // Proceed to declare how to extract the response
                .bodyToMono(elementClass)                            // Decodes body to mono
                .retryWhen(retrySpec)                                // Defines retry strategy
                .doOnError(Mono::error);                             // Adds behavior when error
    }

    private URI getRequestUri(final String path, final MultiValueMap<String, String> queryParams) {
        return UriComponentsBuilder.fromHttpUrl(this.serverBaseUrl)
                .path(path)
                .queryParams(queryParams)
                .build()
                .toUri();
    }

    private void prepareRequestHeader(final HttpHeaders headers) {
        headers.set(TOKEN_HEADER_NAME, TOKEN_HEADER_VALUE);
        headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    private RetrySpec getRetrySpec(final HttpHeaders headers) {
        return Retry.max(1)
                .filter(isDataBaseException())
                .doBeforeRetry(retrySignal -> headers.set(TOKEN_HEADER_NAME, EMERGENCY_TOKEN_HEADER_VALUE));
    }

    private Predicate<? super Throwable> isDataBaseException() {
        return t -> t instanceof DataBaseException;
    }
}
