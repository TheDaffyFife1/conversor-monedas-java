package com.conversor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

public class ExchangeRateClient {

    private static final String API_BASE_URL = "https://v6.exchangerate-api.com/v6/09be52d21fb56fb6451c58ea/latest/";
    private final HttpClient client;

    public ExchangeRateClient() {
        this.client = HttpClient.newHttpClient();
    }

    public HttpResponse<String> getExchangeRates(String baseCurrency) {
        String apiUrl = API_BASE_URL + baseCurrency;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .build();

        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch data", e);
        }
    }
}
