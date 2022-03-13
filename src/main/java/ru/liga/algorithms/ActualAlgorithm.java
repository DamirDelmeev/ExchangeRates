package ru.liga.algorithms;

import ru.liga.constants.Constant;
import ru.liga.currencyFile.CurrencyFileReader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс реализует алгоритм "актуальный"
 */
public class ActualAlgorithm implements Algorithm {
    /**
     * Метод реализует работу алгоритма
     *
     * @param list CurrencyFileReader -файлов в которых есть лист дат лист курсов и количество дней для прогноза
     */
    @Override
    public void realizeAlgorithm(List<CurrencyFileReader> list) {
        list.forEach(this::getResult);
    }

    /**
     * Метод реализует добавление в результаты дат и курсов
     *
     * @param currencyFileReader CurrencyFileReader -файл в которых есть лист дат лист курсов и количество дней для прогноза
     */
    private void getResult(CurrencyFileReader currencyFileReader) {
        List<BigDecimal> resultsRate = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        currencyFileReader.getDateForForecastList()
                .forEach(localDate -> getResults(currencyFileReader, resultsRate, stringList, localDate)
                );
        currencyFileReader.setResultString(String.join("\n", stringList));
        currencyFileReader.setRateForForecastList(resultsRate);
    }

    /**
     * Метод реализует алгоритм "актуальный"
     * rate(date-2years)+rate(date-3years) добавит String result в файл из листа
     *
     * @param currencyFileReader,resultsRate,stringList,localDate
     * currencyFileReader -в которых есть лист дат лист курсов и количество дней для прогноза.
     *                                                            resultsRate -список курсов, которые будут в результате
     *                                                            stringList-лист строк результата.
     *                                                            localDate -дата по которой идёт итерация
     */
    private void getResults(CurrencyFileReader currencyFileReader, List<BigDecimal> resultsRate
            , List<String> stringList, LocalDate localDate) {
        LocalDate dateTwoYearsAgo = localDate.minusYears(2);
        int indexOfTwoYears = currencyFileReader.getDateList().indexOf(dateTwoYearsAgo);
        LocalDate dateThreeYearsAgo = localDate.minusYears(3);
        int indexOfThreeYears = currencyFileReader.getDateList().indexOf(dateThreeYearsAgo);
        if (indexOfThreeYears < 0 || indexOfTwoYears < 0) {
            throw new RuntimeException("Ошибка в работе актуального алгоритма");
        }
        BigDecimal resultOnDate =
                currencyFileReader.getRateList().get(indexOfTwoYears)
                        .add(currencyFileReader.getRateList().get(indexOfThreeYears));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT.getName());
        stringList.add(dateFormat.format(localDate) + " : " + String.format(Constant.BIG_DECIMAL_FORMAT.getName(), resultOnDate));
        resultsRate.add(resultOnDate);
    }
}
