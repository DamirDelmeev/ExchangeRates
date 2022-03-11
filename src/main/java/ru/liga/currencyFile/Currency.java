package ru.liga.currencyFile;

/**
 * Класс тип валюты.
 */
public enum Currency {
    USD("src/main/resources/USD_F01_02_2005_T05_03_2022.csv", "USD"),
    EUR("src/main/resources/EUR_F01_02_2005_T05_03_2022.csv", "EUR"),
    TRY("src/main/resources/TRY_F01_02_2005_T05_03_2022.csv", "TRY"),
    BGN("src/main/resources/BGN_F01_02_2005_T05_03_2022.csv", "BGN"),
    AMD("src/main/resources/AMD_F01_02_2005_T05_03_2022.csv", "AMD");
    /**
     * Путь
     */
    final String path;
    /**
     * Название
     */
    final String name;

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
