package ru.liga;

import au.com.bytecode.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс читает файл с курсами валюты.
 *
 * @version 1.0
 * @autor Дельмеев Дамир
 */
public class CurrencyFileReader {

    /**
     * Поле тип валюты
     */
    private final Currency currency;
    /**
     * Поле дата последнего обновления курса валюты
     */
    LocalDate lastUpdate;
    /**
     * Поле лист списка строк из файла
     */
    private final List<String> lineList = new ArrayList<>();
    /**
     * Поле лист списка double(rate) из файла
     */
    private final List<Double> rateList = new ArrayList<>();

    public CurrencyFileReader(Currency currency) {
        this.currency = currency;
    }

    /**
     * Метод читает файл и записывает его в лист строк{@link #lineList}.
     */
    public void readCsvFile() {
        try {
            char cvsSplitBy = ';';
            CSVReader csvFile = new CSVReader(new FileReader(currency.getPath()), cvsSplitBy);
            String[] lines;
            while ((lines = csvFile.readNext()) != null) {
                if (!lines[1].matches("curs")) {
                    String line = Arrays.stream(lines).collect(Collectors.joining(";"));
                    lineList.add(line.replaceAll(",", "."));
                }
            }
            findRateListAndDate(lineList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод обрабатывает список строк и записывает курсы валюты и дату последнего курса.
     *
     * @param lineList- ввод пользователя
     */
    public void findRateListAndDate(List<String> lineList) {
        String[] splitForDate = lineList.get(0).split(";");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
        lastUpdate = LocalDate.parse(splitForDate[0], formatter);
        for (String line : lineList) {
            String[] split = line.split(";");
            rateList.add(Double.valueOf(split[1]));
        }
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public List<Double> getRateList() {
        return rateList;
    }

    public Currency getCurrency() {
        return currency;
    }

    public List<String> getLineList() {
        return lineList;
    }
}
