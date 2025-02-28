package com.example.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.parser.MapOutputParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OllamaService {

    @Autowired
    private ChatClient chatClient;

    public Map<String, Double> extractValues(String question) {
      String systemPromptTemplate = """
        You are a helpful assistant that strictly parses questions and extracts values.
        Your only task is to extract the values of 'mezcla' and 'temperatura' from the following text.
        The result MUST be a JSON object with the keys 'mezcla' and 'temperatura' and the values as numbers.
        You MUST NOT add any extra text, explanations, or conversation.
        Your output should contain ONLY the JSON.
        Example:
        Text: 'Hola, quiero saber la resistencia con una mezcla de 0.3 y una temperatura de 30.'
        Result:
        {
          "mezcla": 0.3,
          "temperatura": 30.0
        }
        """;
        SystemMessage systemMessage = new SystemMessage(systemPromptTemplate);
        UserMessage userMessage = new UserMessage(question);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String response = chatClient.call(prompt).getResult().getOutput().getContent();
       
        // Clean the response to remove text before or after the JSON
        String cleanedResponse = cleanJson(response);
        if(cleanedResponse.isEmpty()){
            throw new RuntimeException("Error getting data from Ollama. The response is empty");
        }

        MapOutputParser parser = new MapOutputParser();
        Map<String, Object> parsedResponse;
        try{
            parsedResponse = parser.parse(cleanedResponse);
        } catch (Exception ex){
            throw new RuntimeException("Error parsing JSON from Ollama. Response: "+cleanedResponse);
        }
        Map<String, Double> result = new HashMap<>();
        result.put("mezcla", Double.parseDouble(parsedResponse.get("mezcla").toString()));
        result.put("temperatura", Double.parseDouble(parsedResponse.get("temperatura").toString()));
        return result;
    }

    public String generateJsonPayload(Map<String, Double> values) {
      // Use Gson to directly generate the JSON string
      Gson gson = new Gson();
      return gson.toJson(values);
  }

    public String generateSpanishResponse(String prediction, Map<String, Double> values) {
        String systemPromptTemplate = """
                Eres un asistente que responde en español a preguntas de resistencia.
                Te daré un valor de prediccion y unos valores de mezcla y temperatura.
                Debes responder con una frase que diga que valores se han usado para la prediccion y cual es la prediccion obtenida.
                Ejemplo:
                Predicción: 45.0
                Valores:
                {
                  "mezcla": 0.3,
                  "temperatura": 30.0
                }
                Respuesta: Para una mezcla de 0.3 y una temperatura de 30.0, la predicción de resistencia es de 45.0.
                """;
        String promptUser = "Predicción: " + prediction + "\nValores:\n" + values.toString();
        SystemMessage systemMessage = new SystemMessage(systemPromptTemplate);
        UserMessage userMessage = new UserMessage(promptUser);
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }


    // Helper method to clean the JSON response
    private String cleanJson(String response) {
      // Find the first '{' and the last '}'
      int firstBrace = response.indexOf('{');
      int lastBrace = response.lastIndexOf('}');

      if (firstBrace == -1 || lastBrace == -1) {
          // No braces found, invalid JSON. Return empty or throw an exception
          return "";
      }

      // Extract the substring between the braces
      String potentialJson = response.substring(firstBrace, lastBrace + 1);

      // Validate if the potential JSON is well formed.
      if(isJSONValid(potentialJson)){
          return potentialJson;
      } else {
          // The string is not a JSON, try to get the content between the braces.
          return "";
      }
  }
  public boolean isJSONValid(String jsonInString) {
      try {
          final ObjectMapper mapper = new ObjectMapper();
          mapper.readTree(jsonInString);
          return true;
      } catch (JsonProcessingException e) {
          return false;
      }
  }    
}
