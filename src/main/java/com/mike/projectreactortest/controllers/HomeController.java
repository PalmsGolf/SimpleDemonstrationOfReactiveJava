package com.mike.projectreactortest.controllers;

import com.mike.projectreactortest.services.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final HomeService homeService;

    public HomeController(final HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/message")
    public String getMessage() {
        return this.homeService.getHomeMessage().block();
    }

    @GetMapping("/messages")
    public Flux<String> getMessages() {
        final Flux<String> response = this.homeService.getHomeMessages();

        return response;
    }
}
