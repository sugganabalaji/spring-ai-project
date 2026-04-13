package com.app.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/openai")
public class OpenAiController {

    @Autowired
    @Qualifier("openAiChatModel")
    private OpenAiChatModel chatModel;
    private ChatClient chatClient;

    public OpenAiController() {}

    // Way-1
    public OpenAiController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
        this.chatClient = ChatClient.create(chatModel);
    }

    // Way-2
    public OpenAiController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatModel = OpenAiChatModel.builder().build();
        this.chatClient = builder
                // explain more (remembers old messages)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build()
                ).build();
    }

    @GetMapping("/")
    public String ok() {
        return "Ok.";
    }

    @GetMapping("/chat-model/{message}")
    public String chatWithOpenAi(@PathVariable String message) {
        return chatModel.call(message);
    }

    @GetMapping("/chat-client/{content}")
    public ResponseEntity<String> getOpenAiMessage(@PathVariable String content) {
        return ResponseEntity.ok(chatClient.prompt(content).call().content());
    }

    @GetMapping("/chat-client/metadata")
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
