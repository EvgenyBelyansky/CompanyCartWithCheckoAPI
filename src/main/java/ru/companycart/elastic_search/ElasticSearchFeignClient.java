package ru.companycart.elastic_search;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "elasticsearch-client",
        url = "${elasticsearch.host}",
        configuration = ElasticSearchFeignConfig.class
)
public interface ElasticSearchFeignClient {
}
