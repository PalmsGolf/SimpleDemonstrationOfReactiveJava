package com.mike.projectreactortest.controllers;

import com.mike.projectreactortest.services.HomeService;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final HomeService homeService;

    public HomeController(final HomeService homeService) {
        this.homeService = homeService;
    }


    @GetMapping("/messages")
    public List<String> getHomePageInfo(@RequestHeader(name = HttpHeaders.USER_AGENT) final String userAgent) {
        final List<String> response = this.homeService.getHomePageMessages();

        return response;
    }
}
