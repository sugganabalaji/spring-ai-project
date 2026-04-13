package com.app.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class AppConfig {

    // Way-1: VectorStore Implementation
    /*@Bean
    public VectorStore vectorStore(@Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }*/

    // way-2: PgVectorStore Implementation
    /*@Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate,
            @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1536) // 1536-dim for OpenAI embedding model
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.HNSW)
                .build();
    }*/

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled("localhost", 6379);
    }

    // way-3: RedisVectorStore Implementation
    @Bean
    public VectorStore vectorStore(JedisPooled jedisPooled,
                                   @Qualifier("ollamaEmbeddingModel") EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
                .indexName("product-index")
                .initializeSchema(true)
                .build();
    }

}
