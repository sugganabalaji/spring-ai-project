package com.app.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/openai")
public class OpenAiController {

    @Autowired
    private VectorStore vectorStore;

    private OpenAiChatModel chatModel;
    private ChatClient chatClient;
    private EmbeddingModel embeddingModel;
    // public OpenAiController() {}

    // Way-1
    public OpenAiController(OpenAiChatModel chatModel
            , @Qualifier("openAiEmbeddingModel") EmbeddingModel openAiEmbeddingModel) {
        this.chatModel = chatModel;
        this.chatClient = ChatClient.create(chatModel);
        this.embeddingModel = openAiEmbeddingModel;
    }

    // Way-2
    /*public OpenAiController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatModel = OpenAiChatModel.builder().build();
        this.chatClient = builder
                // explain more (remembers old messages)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build()
                ).build();
    }*/

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

    @PostMapping("/recomand")
    public String recomand(@RequestParam String type,
                           @RequestParam String year,
                           @RequestParam String lang) {
        String template = """
                 I Want to watch a {type} movie tonight with good rating,
                 looking for moview around this year {year},
                 The language im loooking for is {lang},
                 suggest one specific movie and tell me the cast and length of the movie.
                """;
        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create();
        return chatClient.prompt(prompt)
                .call()
                .content();
    }

    @PostMapping("/embedding")
    public float[] getEmbedding(@RequestParam String text) {
        return embeddingModel.embed(text);
    }

    @PostMapping("/product")
    public List<Document> getProducts(@RequestParam String text) {
        return vectorStore.similaritySearch(text);
    }

}
