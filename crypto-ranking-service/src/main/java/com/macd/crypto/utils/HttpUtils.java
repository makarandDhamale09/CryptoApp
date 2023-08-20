package com.macd.crypto.utils;

import java.util.Collections;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpUtils {
  private static final String API_HOST = "coinranking1.p.rapidapi.com";
  private static final String API_KEY = "7b3f9e5027msh9e0e6555e8abb72p1e74b2jsnf50ebdd4eeec";

  public static HttpEntity<String> getHttpEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("X-RapidAPI-Host", API_HOST);
    headers.set("X-RapidAPI-Key", API_KEY);
    return new HttpEntity<>(null, headers);
  }
}
