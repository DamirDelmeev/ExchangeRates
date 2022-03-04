package ru.liga;

import ch.qos.logback.classic.util.StatusViaSLF4JLoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс реализует прогноз курса валюты.
 *
 * @version 1.0
 * @autor Дельмеев Дамир
 */
public class ForecastWithAverage {
    /**
     * Объект в котором есть список из файла
     */
    CurrencyFileReader currencyFileReader;
    /**
     * Количество дней прогноза
     */
    private final int amountDays;
    /**
     * Курсы валюты
     */
    private final List<Double> listValues;
    /**
     * Результат
     */
    private final List<String> results = new ArrayList<>();
    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(StatusViaSLF4JLoggerFactory.class);

    public ForecastWithAverage(CurrencyFileReader currencyFileReader, int amountDays, List<Double> listValues) {
        this.currencyFileReader = currencyFileReader;
        this.amountDays = amountDays;
        this.listValues = listValues;
    }

    /**
     * Метод обновляет список с курсами валюты.
     *
     * @return возвращает список курсов валюты.
     */
    public List<Double> getActualInfo() {
        LocalDate currentDate = LocalDate.now();
        Period between = Period.between(currentDate, currencyFileReader.getLastUpdate());
        for (int i = 0; i < Math.abs(between.getDays()); i++) {
            listValues.add(0, findAverage(listValues));
        }
        return listValues;
    }

    /**
     * Метод получает среднее число за 7 значений.
     *
     * @param listValues - список курсов
     * @return среднее число.
     */
    public double findAverage(List<Double> listValues) {
        final int AVERAGE_DAYS = 7;
        double sumRate = 0.0;
        for (int j = 0; j < AVERAGE_DAYS; j++) {
            sumRate = sumRate + listValues.get(j);
        }
        return sumRate / AVERAGE_DAYS;
    }

    /**
     * Метод реализует прогноз.
     *
     * @param listValues,amountDays - список курсов и количество дней прогноза
     * @return возвращает список строк.
     */
    public List<String> getForecast(List<Double> listValues, int amountDays) {
        getActualInfo();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EE dd.MM.yyyy");
        for (int i = 1; i < amountDays + 1; i++) {
            LocalDate requestDate = LocalDate.now().plusDays(i);
            String value = String.format("%.2f", findAverage(listValues));
            listValues.add(0, findAverage(listValues));
            results.add(" " + dateFormat.format(requestDate) + " - " + value);
        }
        show();
        return results;
    }

    /**
     * Метод выводит результат в консоль и лог файл.
     */
    private void show() {
        for (String line : results) {
            logger.info("log message: {}", line);
        }
    }


    public List<Double> getListValues() {
        return listValues;
    }
}
