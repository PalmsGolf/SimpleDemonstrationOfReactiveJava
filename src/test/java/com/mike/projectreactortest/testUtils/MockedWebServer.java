package com.mike.projectreactortest.testUtils;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class MockedWebServer {
    private static final int SERVER_PORT = 5555;
    private final MockWebServer server;

    public MockedWebServer() throws IOException {
        this.server = new MockWebServer();
        this.server.start(SERVER_PORT);
    }

    public void shutdown() throws IOException {
        server.shutdown();
    }

    public void responseWith(HttpStatus status, String responseBody) {
        MockResponse response = new MockResponse()
                .setResponseCode(status.value())
                .setBody(responseBody);

        this.server.enqueue(response);
    }
}
