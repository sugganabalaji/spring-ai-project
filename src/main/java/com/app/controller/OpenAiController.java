package com.app.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAiController {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @GetMapping("/")
    public String ok() {
        return "Ok.";
    }

    @GetMapping("/openai")
    public String getOpenAi() {
        return "Hello OpenAi";
    }

    @GetMapping("/openai/{chat}")
    public String getOpenAiChat(@PathVariable String chat) {
        return "This is a chat from OpenAi. " + chat;
    }

    @GetMapping("/openai/message")
    public String getOpenAiMessage() {
        return "This is a message from OpenAi";
    }

    @GetMapping("/openai/response")
    public String getOpenAiResponse() {
        return "This is a response from OpenAi";
    }

    @GetMapping("/openai/error")
    public String getOpenAiError() {
        return "This is an error from OpenAi";
    }

    @GetMapping("/openai/success")
    public String getOpenAiSuccess() {
        return "This is a success from OpenAi";
    }
}
