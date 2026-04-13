package com.app.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ollama")
public class OllamaController {

    @Autowired
    @Qualifier("ollamaChatModel")
    private OllamaChatModel chatModel;
    private ChatClient ollamaChatClient;

    public OllamaController() {}

    // Way-1
    public OllamaController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
        this.ollamaChatClient = ChatClient.create(chatModel);
    }

    // Way-2
    public OllamaController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatModel = OllamaChatModel.builder().build();
        this.ollamaChatClient = builder
                // explain more (remembers old messages)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build()
                ).build();
    }


    @GetMapping("/chat-model/{message}")
    public String chatWithOpenAi(@PathVariable String message) {
        return chatModel.call(message);
    }

    @GetMapping("/chat-client/{content}")
    public ResponseEntity<String> getOpenAiMessage(@PathVariable String content) {
        return ResponseEntity.ok(ollamaChatClient.prompt(content).call().content());
    }

    @GetMapping("/chat-client/metadata")
    public ResponseEntity<String> workWithChatResponseAndMetaData(@PathVariable String content) {
        ChatResponse chatResponse = ollamaChatClient.prompt(content)
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
