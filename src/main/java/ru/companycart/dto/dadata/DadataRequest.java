package ru.companycart.dto.dadata;

import lombok.Data;

@Data
public class DadataRequest {
    private String query;
    private Integer count;

    public DadataRequest(String query) {
        this.query = query;
        this.count = 1;
    }

    public DadataRequest(String query, Integer count) {
        this.query = query;
        this.count = count;
    }
}
