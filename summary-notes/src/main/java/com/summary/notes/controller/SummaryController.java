package com.summary.notes.controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.summary.notes.dto.SummaryRequest;
import com.summary.notes.dto.SummaryResponse;
import com.summary.notes.service.GeminiService;

@RestController
@CrossOrigin(origins = "*" )
@RequestMapping("/api")
public class SummaryController {

    private final GeminiService geminiService;

    public SummaryController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/summarize")
    public SummaryResponse summarize(@RequestBody SummaryRequest request) {

        String result = geminiService.summarize(request.getText());

        return new SummaryResponse(result);
    }
}