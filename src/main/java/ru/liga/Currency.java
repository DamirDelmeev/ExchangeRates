package ru.liga;

/**
 * Класс тип валюты.
 *
 * @version 1.0
 * @autor Дельмеев Дамир
 */
public enum Currency {
    USD("src/main/resources/USD_F01_02_2002_T01_02_2022.csv", "USD"),
    TRY("src/main/resources/TRY_F01_02_2002_T01_02_2022.csv", "TRY"),
    EUR("src/main/resources/EUR_F01_02_2002_T01_02_2022.csv", "EUR");
    String path;
    String name;

    Currency(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }
}
