package com.app.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAiController {

    private final OpenAiChatModel chatModel;
    private final ChatClient chatClient;

    public OpenAiController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
        this.chatClient = ChatClient.create(chatModel);
    }

    @GetMapping("/")
    public String ok() {
        return "Ok.";
    }

    @GetMapping("/openai/chat-model/{message}")
    public String chatWithOpenAi(@PathVariable String message) {
        return chatModel.call(message);
    }

    @GetMapping("/openai/chat-client/{content}")
    public ResponseEntity<String> getOpenAiMessage(@PathVariable String content) {
        return ResponseEntity.ok(chatClient.prompt(content).call().content());
    }

    @GetMapping("/openai/chat-client/metadata")
    public ResponseEntity<String> workWithChatResponseAndMetaData(@PathVariable String content) {
        ChatResponse chatResponse = chatClient.prompt(content)
                .call()
                .chatResponse();

        String model = chatResponse.getMetadata().getModel();
        System.out.println(model);

        String response = chatResponse.getResult()
                .getOutput()
                .getText();

        return ResponseEntity.ok(response);
    }



}
