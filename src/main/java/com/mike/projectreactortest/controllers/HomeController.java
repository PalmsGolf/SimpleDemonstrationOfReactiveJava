package com.mike.projectreactortest.controllers;

import com.mike.projectreactortest.services.HomeService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final HomeService homeService;

    public HomeController(final HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/message")
    public String getMessage(@RequestHeader(name = HttpHeaders.USER_AGENT) final String userAgent) {
        return this.homeService.getHomeMessage();
    }

    @GetMapping("/messages")
    public List<String> getMessages(@RequestHeader(name = HttpHeaders.USER_AGENT) final String userAgent) {
        List<String> response = new ArrayList<>();
        this.homeService.getHomeMessages().subscribe(response::add);

        return response;
    }
}
