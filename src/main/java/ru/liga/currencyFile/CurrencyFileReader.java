package ru.liga.currencyFile;

import au.com.bytecode.opencsv.CSVReader;
import lombok.Data;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс читает файл с курсами валюты.
 *
 * @version 1.0
 * @autor Дельмеев Дамир
 */
@Data
public class CurrencyFileReader {

    /**
     * Поле тип валюты
     */
    private final Currency currency;
    /**
     * Поле лист списка строк из файла
     */
    private final List<String> lineList = new ArrayList<>();
    /**
     * Поле лист списка BigDecimal(rate) из файла с заполненными пробелами
     */
    private final LinkedList<BigDecimal> rateList = new LinkedList<>();
    /**
     * Поле лист списка LocalDate из файла с заполненными пробелами
     */
    private final LinkedList<LocalDate> dateList = new LinkedList<>();
    /**
     * Поле лист списка LocalDate с датами прогноза
     */
    private List<LocalDate> dateForForecastList = new ArrayList<>();
    /**
     * Поле лист списка BigDecimal(rate)  с результатами прогноза
     */
    private List<BigDecimal> rateForForecastList = new ArrayList<>();
    /**
     * Поле строка с результатами прогноза date+rate
     */
    public String resultString = "";
    /**
     * Поле содержит ли файл график
     */
    public boolean showChart = false;
    /**
     * Поле файл содержит график
     */
    public File file;

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
                if (!lines[0].contains("nominal")) {
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
     * Метод обрабатывает список строк и записывает список дат и список курсов валют заполняя пропуски.
     *
     * @param lineList- список строк из файла
     */
    public void findRateListAndDate(List<String> lineList) {
        String[] splitForDate = lineList.get(0).split(";");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
        dateList.add(LocalDate.parse(splitForDate[1], formatter));
        int counterDays = 0;
        for (String line : lineList) {
            String[] split = line.split(";");
            BigDecimal currentRate = BigDecimal.valueOf(RefactorOurLineForDouble(split));
            BigDecimal nominal = BigDecimal.valueOf(Double.valueOf(split[0]));
            LocalDate currentDate = LocalDate.parse(split[1], formatter);
            if (currentDate.plusDays(counterDays).equals(dateList.get(0))) {
                currentRate = currentRate.divide(nominal);
                rateList.add(currentRate);
                if (counterDays != 0) {
                    dateList.add(currentDate);
                }
            } else {
                Period between = getPeriodOfSkipAndAddChanges(split, currentDate, nominal);
                currentRate = currentRate.divide(nominal);
                rateList.add(currentRate);
                dateList.add(currentDate);
                counterDays = counterDays + between.getDays() - 1;
            }
            counterDays++;
        }
    }

    /**
     * Метод обрабатывает строку и парсит в дабл курс валюты.
     *
     * @param split- строка с текущим курсом
     * @return курсц
     */
    private Double RefactorOurLineForDouble(String[] split) {
        split[2] = split[2].replaceAll("\"", "");
        split[2] = split[2].replaceAll(",", ".");
        return Double.valueOf(split[2]);
    }

    /**
     * Метод обрабатывает строку и вернёт количество пропущенных дней в файле.
     *
     * @param split- строка с текущим курсом
     * @return количество дней
     */
    private Period getPeriodOfSkipAndAddChanges(String[] split, LocalDate checkIfDateSkip, BigDecimal nominal) {
        Period between = Period.between(checkIfDateSkip, dateList.getLast());
        for (int i = 1; i < between.getDays(); i++) {
            rateList.add(BigDecimal.valueOf(Double.valueOf(split[2])).divide(nominal));
            dateList.add(dateList.getLast().minusDays(1));
        }
        return between;
    }


    public List<BigDecimal> getRateList() {
        return rateList;
    }

    public Currency getCurrency() {
        return currency;
    }

    public List<String> getLineList() {
        return lineList;
    }


}
