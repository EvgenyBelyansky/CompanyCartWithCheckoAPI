package ru.companycart.elastic_search;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchFeignConfig {

    @Value("${elasticsearch.api-key}")
    private String apiKey;

    @Bean
    public RequestInterceptor elasticsearchApiKeyInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Authorization", "ApiKey " + apiKey);
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Accept", "application/json");
        };
    }
}