package uniVerse.posterPlot.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum Genre {
    ACTION, CRIME, ROMANCE, SCI_FI, COMEDY, SPORTS, FANTASY, MUSIC, MUSICAL, WAR, HORROR, THRILLER;

    @JsonCreator
    public static Genre fromString(String value) {
        return Arrays.stream(Genre.values())
                .filter(genre -> genre.name().equalsIgnoreCase(value)) // ✅ 대소문자 구분 없이 매핑
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 장르 입력: " + value));
    }
}
