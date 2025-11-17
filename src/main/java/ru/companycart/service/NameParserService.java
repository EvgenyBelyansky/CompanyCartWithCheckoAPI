package ru.companycart.service;

import org.springframework.stereotype.Service;
import ru.companycart.dto.checko.NameComponents;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NameParserService {

    /**
     * Парсит полное ФИО на отдельные компоненты
     */
    public NameComponents parseFullName(String fullName) {
        NameComponents components = new NameComponents();

        if (fullName == null || fullName.trim().isEmpty()) {
            return components;
        }

        String normalizedName = normalizeName(fullName);

        if (tryParseStandardFormat(normalizedName, components)) {
            return components;
        }

        if (tryParseReverseFormat(normalizedName, components)) {
            return components;
        }

        if (tryParseShortFormat(normalizedName, components)) {
            return components;
        }

        components.setLastName(fullName.trim());
        return components;
    }

    /**
     * Стандартный формат: Фамилия Имя Отчество
     */
    private boolean tryParseStandardFormat(String name, NameComponents components) {
        Pattern pattern = Pattern.compile(
                "^([а-яё]+(-[а-яё]+)?)\\s+([а-яё]+)\\s+([а-яё]+)$",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );

        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            components.setLastName(capitalizeName(matcher.group(1)));
            components.setName(capitalizeName(matcher.group(3)));
            components.setMidlName(capitalizeName(matcher.group(4)));
            return true;
        }
        return false;
    }

    /**
     * Обратный формат: Имя Отчество Фамилия
     */
    private boolean tryParseReverseFormat(String name, NameComponents components) {
        Pattern pattern = Pattern.compile(
                "^([а-яё]+)\\s+([а-яё]+)\\s+([а-яё]+(-[а-яё]+)?)$",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );

        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            components.setName(capitalizeName(matcher.group(1)));
            components.setMidlName(capitalizeName(matcher.group(2)));
            components.setLastName(capitalizeName(matcher.group(3)));
            return true;
        }
        return false;
    }

    /**
     * Короткий формат: Фамилия И.О. или И.О. Фамилия
     */
    private boolean tryParseShortFormat(String name, NameComponents components) {
        // Формат: Фамилия И.О.
        Pattern pattern1 = Pattern.compile(
                "^([а-яё]+(-[а-яё]+)?)\\s+([а-яё])\\.?\\s*([а-яё])\\.?$",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );

        Matcher matcher1 = pattern1.matcher(name);
        if (matcher1.find()) {
            components.setLastName(capitalizeName(matcher1.group(1)));
            components.setName(capitalizeName(matcher1.group(3) + "."));
            components.setMidlName(capitalizeName(matcher1.group(4) + "."));
            return true;
        }

        Pattern pattern2 = Pattern.compile(
                "^([а-яё])\\.?\\s*([а-яё])\\.?\\s+([а-яё]+(-[а-яё]+)?)$",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
        );

        Matcher matcher2 = pattern2.matcher(name);
        if (matcher2.find()) {
            components.setName(capitalizeName(matcher2.group(1) + "."));
            components.setMidlName(capitalizeName(matcher2.group(2) + "."));
            components.setLastName(capitalizeName(matcher2.group(3)));
            return true;
        }

        return false;
    }

    /**
     * Нормализует имя: убирает лишние пробелы, приводит к нижнему регистру
     */
    private String normalizeName(String name) {
        return name.trim()
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    /**
     * Приводит имя к правильному регистру: первая буква заглавная, остальные строчные
     */
    private String capitalizeName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        if (name.contains("-")) {
            String[] parts = name.split("-");
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) result.append("-");
                result.append(capitalizeWord(parts[i]));
            }
            return result.toString();
        }

        return capitalizeWord(name);
    }

    private String capitalizeWord(String word) {
        if (word.length() == 1) {
            return word.toUpperCase();
        }
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}
