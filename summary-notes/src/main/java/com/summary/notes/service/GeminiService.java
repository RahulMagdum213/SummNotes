package com.summary.notes.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class GeminiService {

    private final WebClient webClient;

    @Value("${gemini.api.key}")
    private String apiKey;

    public GeminiService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public String summarize(String text) {

    	String prompt = """
    	        Summarize the following text clearly and concisely.
    	        Keep the important information.
    	        Do not add information that is not present in the text.
    	        Return only the summary. Do not write headings such as "Summary:".

    	        Text:
    	        """ + text;

        String requestBody = """
                {
                    "model": "gemini-3.6-flash",
                    "input": "%s"
                }
                """.formatted(
                        prompt
                                .replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n")
                );

        String response = webClient.post()
                .uri("/v1beta/interactions")
                .header("x-goog-api-key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Parse Gemini JSON response
        JsonParser parser = JsonParserFactory.getJsonParser();

        Map<String, Object> json = parser.parseMap(response);

        // Get steps
        List<?> steps = (List<?>) json.get("steps");

        // Get model output step
        Map<?, ?> modelOutput = (Map<?, ?>) steps.get(1);

        // Get content
        List<?> content = (List<?>) modelOutput.get("content");

        // Get text
        Map<?, ?> textContent = (Map<?, ?>) content.get(0);

        return textContent.get("text").toString();
    }
}