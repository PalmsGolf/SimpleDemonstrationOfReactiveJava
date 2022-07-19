package com.mike.projectreactortest.controllers;

import com.mike.projectreactortest.dto.MessageRequest;
import com.mike.projectreactortest.services.MessagesService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MessagesController {
    private final MessagesService messagesService;

    public MessagesController(final MessagesService messagesService) {
        this.messagesService = messagesService;
    }

    @GetMapping(value = "/stream/messages", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getMessagesStream() {
        return this.messagesService.getHomeMessages();
    }

    @GetMapping("/message/{id}")
    public Mono<String> getMessage(@PathVariable final String id) {
        return this.messagesService.getHomeMessage();
    }

    @PutMapping(value = "/message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> storeMessage(@RequestHeader(name = HttpHeaders.USER_AGENT) final String userAgent,
                                               @RequestBody final MessageRequest body) {
        return ResponseEntity.ok("fine");
    }
}
