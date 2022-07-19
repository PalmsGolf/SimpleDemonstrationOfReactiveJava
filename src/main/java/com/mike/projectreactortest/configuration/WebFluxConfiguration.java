package com.mike.projectreactortest.configuration;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebFluxConfiguration {
    private static final int TIMEOUT_TIME_SEC = 10;

    @Value("${messages-server.base-url}")
    private String serverBaseUrl;

    @Bean
    public WebClient webClient() {
        final HttpClient httpClient = getHttpClient();
        final ReactorClientHttpConnector reactorClientHttpConnector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .baseUrl(serverBaseUrl)
                .clientConnector(reactorClientHttpConnector)
                .build();
    }

    private HttpClient getHttpClient() {
        return HttpClient.create()
                .wiretap(HttpClient.class.getName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                .responseTimeout(Duration.ofSeconds(TIMEOUT_TIME_SEC))
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(TIMEOUT_TIME_SEC, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT_TIME_SEC, TimeUnit.SECONDS))
                );
    }
}
