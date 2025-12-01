package ru.companycart.dto;

import lombok.Data;
import java.util.List;

@Data
public class ContactDto {
    private List<String> phones;
    private String email;
    private String website;
}
