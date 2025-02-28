package com.example.controller;

import com.example.service.MlflowService;
import com.example.service.OllamaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class PredictionController {

    @Autowired
    private OllamaService ollamaService;

    @Autowired
    private MlflowService mlflowService;

    @PostMapping("/ask")
    public String askQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        Map<String, Double> values = ollamaService.extractValues(question);
        String jsonPayload = ollamaService.generateJsonPayload(values);
        String prediction = mlflowService.getPrediction(jsonPayload);
        return ollamaService.generateSpanishResponse(prediction, values);
    }
}
