package ru.companycart.dto.checko;

import lombok.Data;

@Data
public class CheckoRequest {
    private String query;
    private Integer count;

    public CheckoRequest(String query) {
        this.query = query;
        this.count = 1;
    }

    public CheckoRequest(String query, Integer count) {
        this.query = query;
        this.count = count;
    }
}
