package ru.liga.constants;

import lombok.Getter;

/**
 * Класс часто применяемых констант.
 */
public enum Constant {
    DATE_FORMAT("EE dd.MM.yyyy"),
    DATE_FORMAT_FOR_CSV("d.MM.yyyy"),
    BIG_DECIMAL_FORMAT("%.2f");

    @Getter
    final
    String name;

    Constant(String name) {
        this.name = name;
    }
}
