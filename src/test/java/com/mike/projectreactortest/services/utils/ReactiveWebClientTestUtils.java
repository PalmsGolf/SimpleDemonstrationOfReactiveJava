package com.mike.projectreactortest.services.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class ReactiveWebClientTestUtils {

    public HttpHeaders createDummyHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        final List<Locale> localeList = new ArrayList<>();
        localeList.add(Locale.FRANCE);
        headers.setAcceptLanguageAsLocales(localeList);

        return headers;
    }
}
