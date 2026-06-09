package com.mo.moaicodemother;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.mo.moaicodemother.mapper")
@ComponentScan("com.mo")
@EnableDubbo
public class MoAiCodeUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoAiCodeUserApplication.class, args);
    }
}
