package ru.companycart.checko.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class CheckoResponse {
    private Map<String, Object> data;
    private Map<String, Object> meta;

    public String getInn() {
        return data != null ? getStringValue(data, "ИНН") : null;
    }

    public String getKpp() {
        return data != null ? getStringValue(data, "КПП") : null;
    }

    public String getOgrn() {
        return data != null ? getStringValue(data, "ОГРН") : null;
    }

    public String getOkopf() {
        return data != null ? getStringValue(data, "ОКОПФ") : null;
    }

    public String getFullName() {
        return data != null ? getStringValue(data, "НаимПолн") : null;
    }

    public String getShortName() {
        return data != null ? getStringValue(data, "НаимСокр") : null;
    }

    public String getStatus() {
        if (data != null) {
            Map<String, Object> status = getMapValue(data, "Статус");
            return status != null ? getStringValue(status, "Наим") : null;
        }
        return null;
    }

    public String getRegistrationDate() {
        return data != null ? getStringValue(data, "ДатаРег") : null;
    }

    public String getAddress() {
        if (data != null) {
            Map<String, Object> address = getMapValue(data, "ЮрАдрес");
            return address != null ? getStringValue(address, "АдресРФ") : null;
        }
        return null;
    }

    public String getMainOkved() {
        if (data != null) {
            Map<String, Object> okved = getMapValue(data, "ОКВЭД");
            return okved != null ? getStringValue(okved, "Код") : null;
        }
        return null;
    }

    public List<String> getAdditionalOkveds() {
        if (data != null) {
            List<Map<String, Object>> okvedDop = getListValue(data, "ОКВЭДДоп");
            if (okvedDop != null) {
                return okvedDop.stream()
                        .map(okved -> getStringValue(okved, "Код"))
                        .toList();
            }
        }
        return null;
    }

    public List<String> getPhone() {
        if (data != null) {
            Map<String, Object> contacts = getMapValue(data, "Контакты");
            if (contacts != null) {
                List<String> phones = getListValue2(contacts, "Тел");
                return phones != null ? phones : new ArrayList<>();
            }
        }
        return new ArrayList<>();
    }

    public String getEmail() {
        if (data != null) {
            Map<String, Object> contacts = getMapValue(data, "Контакты");
            if (contacts != null) {
                return getFirstListValue(contacts, "Емэйл");
            }
        }
        return null;
    }

    public String getWebsite() {
        if (data != null) {
            Map<String, Object> contacts = getMapValue(data, "Контакты");
            return contacts != null ? getStringValue(contacts, "ВебСайт") : null;
        }
        return null;
    }

    public String getManagerName() {
        if (data != null) {
            List<Map<String, Object>> management = getListValue(data, "Руковод");
            if (management != null && !management.isEmpty()) {
                return getStringValue(management.get(0), "ФИО");
            }
        }
        return null;
    }

    public String getManagerPosition() {
        if (data != null) {
            List<Map<String, Object>> management = getListValue(data, "Руковод");
            if (management != null && !management.isEmpty()) {
                return getStringValue(management.get(0), "НаимДолжн");
            }
        }
        return null;
    }

    public String getManagerInn() {
        if (data != null) {
            List<Map<String, Object>> management = getListValue(data, "Руковод");
            if (management != null && !management.isEmpty()) {
                return getStringValue(management.get(0), "ИНН");
            }
        }
        return null;
    }

    private String getStringValue(Map<String, Object> map, String key) {
        return map.containsKey(key) && map.get(key) != null ?
                String.valueOf(map.get(key)) : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMapValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof Map ? (Map<String, Object>) value : null;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getListValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof List ? (List<Map<String, Object>>) value : null;
    }

    @SuppressWarnings("unchecked")
    private List<String> getListValue2(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List) {
            List<Object> rawList = (List<Object>) value;
            return rawList.stream()
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String getFirstListValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof List && !((List<?>) value).isEmpty()) {
            List<Object> list = (List<Object>) value;
            return list.get(0) != null ? String.valueOf(list.get(0)) : null;
        }
        return null;
    }
}

