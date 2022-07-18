package com.mike.projectreactortest.configuration;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import javax.net.ssl.SSLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebFluxConfiguration {
    private static final int TIMEOUT_TIME_SEC = 10;

    @Bean
    public WebClient webClient() throws SSLException {
        final SslContext sslContext = getSslContext();
        final HttpClient httpClient = getHttpClient().secure(provider -> provider.sslContext(sslContext));
        final ReactorClientHttpConnector reactorClientHttpConnector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .clientConnector(reactorClientHttpConnector)
                .build();
    }

    private SslContext getSslContext() throws SSLException {
        return SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
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
