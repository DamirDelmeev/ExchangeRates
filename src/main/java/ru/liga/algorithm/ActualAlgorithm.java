package ru.liga.algorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.bot.Bot;
import ru.liga.currencyFile.CurrencyFileReader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс реализует алгоритм "актуальный"
 *
 */
public class ActualAlgorithm implements Algorithm {
    /**
     * Метод реализует алгоритм "актуальный"
     * rate(date-2years)+rate(date-3years) добавит String result в файл из листа
     *
     * @param list CurrencyFileReader -файлов в которых есть лист дат лист курсов и количество дней для прогноза
     */
    @Override
    public void realizeAlgorithm(List<CurrencyFileReader> list) {
        Logger logger = LoggerFactory.getLogger(ActualAlgorithm.class);
        for (CurrencyFileReader currencyFileReader : list) {
            List<BigDecimal> resultsRate = new ArrayList<>();
             List<String> stringList = new ArrayList<>();
            currencyFileReader.getDateForForecastList().forEach(localDate -> {
                LocalDate dateTwoYearsAgo = localDate.minusYears(2);
                int indexOfTwoYears = currencyFileReader.getDateList().indexOf(dateTwoYearsAgo);
                LocalDate dateThreeYearsAgo = localDate.minusYears(3);
                int indexOfThreeYears = currencyFileReader.getDateList().indexOf(dateThreeYearsAgo);
                if (indexOfThreeYears < 0 || indexOfTwoYears < 0) {
                    logger.error("Ошибка в работе актуального алгоритма");
                    throw new RuntimeException();
                }
                BigDecimal resultOnDate =
                        currencyFileReader.getRateList().get(indexOfTwoYears)
                                .add(currencyFileReader.getRateList().get(indexOfThreeYears));
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EE dd.MM.yyyy");
                        stringList.add(dateFormat.format(localDate) + " : " + String.format("%.2f",resultOnDate));
                resultsRate.add(resultOnDate);
            }
            );
            currencyFileReader.setResultString(String.join("\n", stringList));
            currencyFileReader.setRateForForecastList(resultsRate);
        }
    }
}
