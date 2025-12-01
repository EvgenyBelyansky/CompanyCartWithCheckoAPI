package ru.companycart.dto;

import lombok.Data;
import java.util.List;

@Data
public class OkvedDto {
    private String mainOkved;
    private List<String> additionalOkveds;
}