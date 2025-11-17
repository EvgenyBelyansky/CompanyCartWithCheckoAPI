package ru.companycart.service;

import org.springframework.stereotype.Service;
import ru.companycart.dto.checko.AddressComponents;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AddressParserService {

    public AddressComponents parseAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            return new AddressComponents();
        }

        AddressComponents components = new AddressComponents();
        components.setFullAddress(fullAddress.trim());

        try {
            String[] parts = fullAddress.split(",");
            if (parts.length > 0) {
                String firstPart = parts[0].trim();
                if (firstPart.matches("\\d{6}")) {
                    components.setPostalCode(firstPart);
                }
            }
            parseByKeywords(fullAddress, components);
        } catch (Exception e) {
            components.setFullAddress(fullAddress);
        }
        return components;
    }

    private void parseByKeywords(String address, AddressComponents components) {
        String lowerAddress = address.toLowerCase();

        if (lowerAddress.contains("россия") || lowerAddress.contains("russia")) {
            components.setCountry("Россия");
        }

        String[] parts = address.split(",");
        boolean cityFound = false;

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            String lowerPart = part.toLowerCase();

            if (lowerPart.contains("обл") || lowerPart.contains("край") ||
                    lowerPart.contains("респ") || lowerPart.contains("ао")) {
                components.setRegion(cleanAddressPart(part, "region"));
            } else if ((lowerPart.startsWith("г ") || lowerPart.startsWith("гор ") ||
                    lowerPart.startsWith("город ") || lowerPart.contains("г.")) &&
                    !cityFound) {
                components.setCity(cleanAddressPart(part, "city"));
                cityFound = true;
            } else if ((lowerPart.startsWith("д ") || lowerPart.startsWith("дер ") ||
                    lowerPart.startsWith("деревня ") || lowerPart.contains("д.")) &&
                    !cityFound && !isHousePart(part)) { // ← добавляем проверку
                components.setCity(cleanAddressPart(part, "city"));
                cityFound = true;
            } else if ((lowerPart.startsWith("с ") || lowerPart.startsWith("сел ") ||
                    lowerPart.startsWith("село ") || lowerPart.contains("с.")) &&
                    !cityFound) {
                components.setCity(cleanAddressPart(part, "city"));
                cityFound = true;
            } else if (lowerPart.contains("район") || lowerPart.contains("р-н")) {
                components.setDistrict(cleanAddressPart(part, "district"));
            } else if (lowerPart.startsWith("ул ") || lowerPart.startsWith("улица ") ||
                    lowerPart.contains("ул.") || lowerPart.startsWith("пр ") ||
                    lowerPart.startsWith("проспект ") || lowerPart.contains("пр.") ||
                    lowerPart.startsWith("пр-кт ") || lowerPart.contains("пр-кт") ||
                    lowerPart.startsWith("ш ") || lowerPart.startsWith("шоссе ") ||
                    lowerPart.contains("ш.") || lowerPart.startsWith("б-р ") ||
                    lowerPart.startsWith("бульвар ") || lowerPart.startsWith("пер ") ||
                    lowerPart.startsWith("переулок ") || lowerPart.contains("пер.") ||
                    lowerPart.startsWith("проезд ") || lowerPart.contains("проезд")) { // ← добавить проезд
                components.setStreet(cleanAddressPart(part, "street"));
            }
        }

        Map<String, String> buildingComponents = new LinkedHashMap<>();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            String lowerPart = part.toLowerCase();

            if ((lowerPart.startsWith("зд ") || lowerPart.startsWith("здание ") ||
                    lowerPart.contains("зд.")) && components.getHouse() == null) {
                components.setHouse(extractHouseNumber(part));
            }
            else if ((lowerPart.startsWith("д ") || lowerPart.startsWith("дом ") ||
                    lowerPart.contains("д.")) && components.getHouse() == null &&
                    isHousePart(part)) {
                components.setHouse(extractHouseNumber(part));
            }
            else if ((lowerPart.startsWith("корп ") || lowerPart.startsWith("корпус ") ||
                    lowerPart.startsWith("к ") || lowerPart.contains("корп.")) &&
                    buildingComponents.get("корпус") == null) {
                buildingComponents.put("корпус", extractBuildingNumber(part));
            }
            else if ((lowerPart.startsWith("стр ") || lowerPart.startsWith("строение ") ||
                    lowerPart.contains("стр.")) && buildingComponents.get("строение") == null) {
                buildingComponents.put("строение", extractBuildingNumber(part));
            }
            else if ((lowerPart.contains("литера ") || lowerPart.startsWith("литер ") ||
                    lowerPart.contains("лит.") || lowerPart.startsWith("лит ")) &&
                    buildingComponents.get("литера") == null) {
                buildingComponents.put("литера", extractLiter(part));
            }
            else if ((lowerPart.startsWith("помещ ") || lowerPart.startsWith("помещение ") ||
                    lowerPart.contains("помещ.") || lowerPart.startsWith("кв ") ||
                    lowerPart.startsWith("квартира ") || lowerPart.contains("кв.") ||
                    lowerPart.startsWith("оф ") || lowerPart.startsWith("офис ") ||
                    lowerPart.contains("оф.") || lowerPart.contains("п. ")) &&
                    components.getApartment() == null) {
                components.setApartment(extractApartmentNumber(part));
            }
        }

        if (components.getHouse() != null || !buildingComponents.isEmpty()) {
            StringBuilder fullHouseBuilder = new StringBuilder();

            if (components.getHouse() != null) {
                fullHouseBuilder.append("д. ").append(components.getHouse());
            }

            for (Map.Entry<String, String> entry : buildingComponents.entrySet()) {
                if (fullHouseBuilder.length() > 0) {
                    fullHouseBuilder.append(", ");
                }
                fullHouseBuilder.append(entry.getKey()).append(" ").append(entry.getValue());
            }
            components.setFullHouseNumber(fullHouseBuilder.toString());
        }

        if (components.getHouse() == null) {
            String houseFromAddress = findFirstHouseNumberInAddress(address);
            if (houseFromAddress != null) {
                components.setHouse(houseFromAddress);
            }
        }
    }

    private boolean isHousePart(String part) {
        String lowerPart = part.toLowerCase();

        if (lowerPart.matches(".*(д|дом)\\.?\\s+\\d+.*")) {
            return true;
        }

        if (lowerPart.matches("^\\s*\\d+.*")) {
            return true;
        }

        if (lowerPart.contains("зд") || lowerPart.contains("стр") ||
                lowerPart.contains("корп") || lowerPart.matches(".*\\d+.*")) {
            return true;
        }

        return false;
    }

    private String findFirstHouseNumberInAddress(String address) {
        Pattern pattern = Pattern.compile(
                "(?:д\\.?|дом)\\s*(\\d+[а-я]?)|" +
                        "(?<=ул\\.?|улица|пр\\.?|проспект|ш\\.?|шоссе|б-р|бульвар|пер\\.?|переулок).*?\\b(\\d+[а-я]?)\\b",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(address);
        if (matcher.find()) {
            return matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
        }

        return null;
    }

    private String cleanAddressPart(String part, String partType) {
        String cleaned = part.trim();

        switch (partType) {
            case "city":
                cleaned = cleaned.replaceAll(
                        "^(г\\.?|гор\\.?|город|с\\.?|сел\\.?|село|д\\.?|дер\\.?|деревня)\\s*",
                        ""
                ).trim();

                cleaned = cleaned.replaceAll(
                        "^\\d+\\s*",
                        ""
                ).trim();
                break;
            case "street":
                cleaned = cleaned.replaceAll(
                        "^(ул\\.?|улица|пр\\.?|проспект|ш\\.?|шоссе|б-р|бульвар|пер\\.?|переулок|проезд)\\s*",
                        ""
                ).trim();
                break;
            case "district":
                cleaned = cleaned.replaceAll(
                        "^(район|р-н)\\s*",
                        ""
                ).trim();
                break;
            case "region":
                break;
            default:
                cleaned = cleaned.replaceAll(
                        "^[гдкупршб]\\.?\\s*",
                                ""
                        )
                        .replaceAll(
                                "^(ул|улица|пр|проспект|ш|шоссе|б-р|бульвар|район|р-н|корп|корпус|стр|строение|" +
                                        "кв|квартира|оф|офис|помещ|помещение)\\.?\\s*",
                                ""
                        )
                        .trim();
        }
        return cleaned;
    }

    private String extractHouseNumber(String part) {
        Pattern pattern0 = Pattern.compile(
                "(?:зд|здание)\\.?\\s*(\\d+[а-я]?)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher0 = pattern0.matcher(part);
        if (matcher0.find()) {
            return matcher0.group(1);
        }

        Pattern pattern1 = Pattern.compile(
                "(?:д|дом)\\.?\\s*(\\d+[а-я]?)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher1 = pattern1.matcher(part);
        if (matcher1.find()) {
            return matcher1.group(1);
        }

        Pattern pattern2 = Pattern.compile("\\b(\\d+[а-я]?)\\b");
        Matcher matcher2 = pattern2.matcher(part);
        if (matcher2.find()) {
            return matcher2.group(1);
        }
        return null;
    }

    private String extractBuildingNumber(String part) {
        Pattern pattern = Pattern.compile(
                "(?:корп|корпус|стр|строение|к)\\.?\\s*(\\d+[а-я]?)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(part);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String extractLiter(String part) {
        String lowerPart = part.toLowerCase();
        int literIndex = lowerPart.indexOf("литер");
        if (literIndex >= 0) {
            for (int i = literIndex + 5; i < part.length(); i++) {
                char c = part.charAt(i);
                if (Character.isLetter(c)) {
                    return String.valueOf(c).toUpperCase();
                }
            }
        }
        return null;
    }

    private String extractApartmentNumber(String part) {
        Pattern pattern = Pattern.compile(
                "(?:помещ|помещение|кв|квартира|оф|офис|п)\\.?\\s*(\\d+[а-я]?[\\/\\-]?\\d*)",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = pattern.matcher(part);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}