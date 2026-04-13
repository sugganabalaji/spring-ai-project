package com.app.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAiController {

    private final OpenAiChatModel chatModel;

    public OpenAiController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/")
    public String ok() {
        return "Ok.";
    }

    @GetMapping("/openai/{message}")
    public String chatWithOpenAi(@PathVariable String message) {
        return chatModel.call(message);
    }

}
