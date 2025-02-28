package com.example.service;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MlflowService {

    private final WebClient webClient;

    public MlflowService() {
        this.webClient = WebClient.builder().baseUrl("http://api:8000").build();
    }

    public String getPrediction(String jsonPayload) {
        return webClient.post()
                .uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonPayload)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
