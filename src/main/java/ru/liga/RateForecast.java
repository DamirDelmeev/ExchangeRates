package ru.liga;

import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Класс выдаёт прогноз курса валюты.
 *
 * @version 1.0
 * @autor Дельмеев Дамир
 */
public class RateForecast {
    private static final Logger logger = LoggerFactory.getLogger(StatusViaSLF4JLoggerFactory.class);

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String userIn = in.nextLine();
        int amountDays = defineAmountDays(userIn);
        CurrencyFileReader file = defineCurrencyType(userIn);
        ForecastWithAverage forecastWithAverage = new ForecastWithAverage(file, amountDays, file.getRateList());
        forecastWithAverage.getForecast(file.getRateList(), amountDays);
    }

    /**
     * Метод определяющий тип валюты.
     *
     * @param userIn - ввод пользователя
     * @return возвращает объект в котором есть список курсов валюты.
     */
    public static CurrencyFileReader defineCurrencyType(String userIn) {
        CurrencyFileReader currencyFileReader = null;
        Currency[] availableCurrency = {Currency.EUR, Currency.TRY, Currency.USD};
        for (Currency currency : availableCurrency) {
            if (userIn.contains(currency.getName())) {
                currencyFileReader = new CurrencyFileReader(currency);
            }
        }
        if (currencyFileReader != null) {
            currencyFileReader.readCsvFile();
        }
        return currencyFileReader;
    }

    /**
     * Метод определяющий количество дней прогноза.
     *
     * @param userIn - ввод пользователя
     * @return возвращает количество дней.
     */
    public static int defineAmountDays(String userIn) {
        if (userIn.contains("week")) {
            return 7;
        }
        if (userIn.contains("tomorrow")) {
            return 1;
        } else {
            logger.error("Ошибка при вводе.");
            System.out.println("Ошибка при вводе.");
            return 0;
        }
    }
}
