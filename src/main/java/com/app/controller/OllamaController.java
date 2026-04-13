package com.app.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ollama")
public class OllamaController {

    @Autowired
    private VectorStore vectorStore;

    @Qualifier("ollamaChatModel")
    private final OllamaChatModel chatModel;
    private final ChatClient ollamaChatClient;
    private final EmbeddingModel embeddingModel;
    //public OllamaController() {}

    // Way-1
    public OllamaController(OllamaChatModel chatModel
            , @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        this.chatModel = chatModel;
        this.ollamaChatClient = ChatClient.create(chatModel);
        this.embeddingModel = embeddingModel;
    }

    // Way-2
    /*public OllamaController(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatModel = OllamaChatModel.builder().build();
        this.ollamaChatClient = builder
                // explain more (remembers old messages)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build()
                ).build();
    }*/


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

    @PostMapping("/recommend")
    public String recommend(@RequestParam String type,
                            @RequestParam String year,
                            @RequestParam String lang) {
        String template = """
                 I Want to watch a {type} movie tonight with good rating,
                 looking for moview around this year {year},
                 The language im loooking for is {lang},
                 suggest one specific movie and tell me the cast and length of the movie.
                
                 Response format should be:
                 Movie Name: <movie name>
                 Cast: <cast>
                 Length: <length>
                 Rating: <rating>
                 Year: <year>
                 Language: <language>
                 Genre: <genre>
                 Director: <director>
                 Actors: <actors>
                 Plot: <plot>
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of("type", type, "year", year, "lang", lang));
        return ollamaChatClient.prompt(prompt)
                .call()
                .content();
    }

    @PostMapping("/embedding")
    public ResponseEntity<?> getEmbedding(@RequestParam String text) {
        try {
            float[] embedding = embeddingModel.embed(text);
            return ResponseEntity.ok(embedding);
        } catch (NonTransientAiException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Embedding request failed. The Ollama service returned an authorization error. " +
                            "Check your Ollama base URL, auth/proxy settings, and model configuration.");
        }
    }

    @PostMapping("/similarity")
    public ResponseEntity<Double> similarity(@RequestParam String text1, @RequestParam String text2) {
        float[] embedding1 = embeddingModel.embed(text1);
        float[] embedding2 = embeddingModel.embed(text2);

        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;

        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += Math.pow(embedding1[i], 2);
            norm2 += Math.pow(embedding2[i], 2);
        }
        double similarityScore = dotProduct *100 / (Math.sqrt(norm1) * Math.sqrt(norm2));
        return ResponseEntity.ok(similarityScore);
    }

    @PostMapping("/product")
    public List<Document> getProducts(@RequestParam String text) {
        //return vectorStore.similaritySearch(text);
        return vectorStore.similaritySearch(SearchRequest.builder().query(text).topK(2).build());
    }

}
