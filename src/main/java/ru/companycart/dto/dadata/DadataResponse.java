package ru.companycart.dto.dadata;

import lombok.Data;

import java.util.List;

@Data
public class DadataResponse {

    private List<Suggestion> suggestions;

    @Data
    public static class Suggestion {
        private CompanyData data;
    }

    @Data
    public static class CompanyData {
        private String inn;
        private String kpp;
        private String ogrn;
        private Name name;
        private State state;
    }

    @Data
    public static class Name {
        private String full_with_opf;
        private String short_with_opf;
    }

    @Data
    public static class State {
        private String status;
    }
}
