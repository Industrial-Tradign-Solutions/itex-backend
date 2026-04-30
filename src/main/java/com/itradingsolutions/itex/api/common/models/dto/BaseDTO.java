package com.itradingsolutions.itex.api.common.models.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public abstract class BaseDTO {

    private ZonedDateTime createdAt;

    private UUID id;

    protected String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder resp = new StringBuilder();
        var texts =  text.toLowerCase().trim().split(" ");
        for (String newText: texts) {
            resp.append(StringUtils.capitalize(newText)).append(" ");
        }
        return resp.toString().trim();
    }

    protected static String normalizeText(String input) {
        if (input == null || input.isEmpty()) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}", "");
    }

    protected static String removeSpecialChars(String input) {
        String regex = "[~!@#$%^*\\\\{}\\[\\];:?<>_]";
        return input.replaceAll(regex, "");
    }
}
