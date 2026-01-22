package ru.companycart.checko.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CheckoBatchResponse {
    private boolean success;
    private Map<String, CheckoResponse> data; // ИНН -> данные компании
    private String errorMessage;
}
