package ru.liga.algorithms;

import lombok.Getter;
import ru.liga.constants.Constant;
import ru.liga.currencyFile.CurrencyFileReader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Класс реализует алгоритм мистический
 */
public class MysticalAlgorithm implements Algorithm {
    /**
     * поле random
     */
    private static final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
    /**
     * поле список дат полнолуния
     */
    @Getter
    List<LocalDate> moodDate = new ArrayList<>();

    /**
     * Метод реализует алгоритм мистический
     *
     * @param list CurrencyFileReader -файлов в которых есть лист дат лист курсов и количество дней для прогноза
     */
    @Override
    public void realizeAlgorithm(List<CurrencyFileReader> list) {
        list.forEach(this::getResult);
    }

    /**
     * Метод реализует запись результатов после алгоритма мистический
     *
     * @param currencyFileReader -файл в котором есть лист дат лист курсов и количество дней для прогноза
     */
    private void getResult(CurrencyFileReader currencyFileReader) {
        List<LocalDate> moonDate = getMoonDate();
        List<BigDecimal> resultsRate = new ArrayList<>();
        List<String> stringList = new ArrayList<>();

        if (currencyFileReader.getDateForForecastList().size() == 1) {
            getForecastOnFirstDay(currencyFileReader, moonDate, resultsRate, stringList);
        } else {
            getForecastOnOtherDay(currencyFileReader, moonDate, resultsRate, stringList);
        }

        currencyFileReader.setResultString(String.join("\n", stringList));
        currencyFileReader.setRateForForecastList(resultsRate);
    }

    /**
     * Метод реализует алгоритм мистический
     * - Для расчета на дату используем среднее арифметическое из трех последних от этой даты полнолуний.
     * - Для расчета на неделю и месяц. Первый курс рассчитывается аналогично предыдущему пункту.
     * counterForRate - позволяет Последующие даты рассчитываются рекуррентно по формуле
     * - значение предыдущей даты + случайное число от -10% до +10% от значения предыдущей даты.
     *
     * @param currencyFileReader,moonDate,resultRate,stringList -файл в котором есть лист дат лист курсов и количество дней для прогноза.
     *                                                          - список дат полнолуния.
     *                                                          -список курсов в результат.
     *                                                          -список строк результата с датами и курсами.
     */
    private void getForecastOnOtherDay
    (CurrencyFileReader currencyFileReader, List<LocalDate> moonDate, List<BigDecimal> resultsRate, List<String> stringList) {
        getForecastOnFirstDay(currencyFileReader, moonDate, resultsRate, stringList);
        currencyFileReader.getDateForForecastList().forEach(localDate -> {
            int counterForRate = 0;
            if (!localDate.equals(currencyFileReader.getDateForForecastList().get(0))) {
                BigDecimal resultOnDate =
                        getBigDecimalRange(resultsRate.get(counterForRate)
                                        .multiply(BigDecimal.valueOf(0.9)),
                                resultsRate.get(counterForRate)
                                        .multiply(BigDecimal.valueOf(1.1)));
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT.getName());
                stringList.add(dateFormat.format(localDate)
                        + " : " + String.format(Constant.BIG_DECIMAL_FORMAT.getName(), resultOnDate));
                resultsRate.add(resultOnDate);
            }
        });
    }

    /**
     * Метод реализует алгоритм мистический
     * - Для расчета на дату используем среднее арифметическое из трех последних от этой даты полнолуний.
     *
     * @param currencyFileReader -файл в котором есть лист дат лист курсов и количество дней для прогноза
     */
    private void getForecastOnFirstDay(CurrencyFileReader currencyFileReader, List<LocalDate> moonDate,
                                       List<BigDecimal> resultsRate, List<String> stringList) {
        int[] indexDate = new int[]{currencyFileReader.getDateList().indexOf(moonDate.get(0)),
                currencyFileReader.getDateList().indexOf(moonDate.get(1)),
                currencyFileReader.getDateList().indexOf(moonDate.get(2))};

        BigDecimal sum = currencyFileReader.getRateList().get(indexDate[0])
                .add(currencyFileReader.getRateList().get(indexDate[1]))
                .add(currencyFileReader.getRateList().get(indexDate[2]));
        BigDecimal resultOnDate = sum.divide(BigDecimal.valueOf(3), 10, RoundingMode.HALF_UP);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT.getName());
        stringList.add(dateFormat.format(currencyFileReader.getDateForForecastList().get(0))
                + " : " + String.format(Constant.BIG_DECIMAL_FORMAT.getName(), resultOnDate));
        resultsRate.add(resultOnDate);
    }

    /**
     * Метод получает список дат полнолуний до текущей даты
     *
     * @return List<LocalDate>
     */
    private List<LocalDate> getMoonDate() {
        List<LocalDate> moodDate = new ArrayList<>();
        LocalDateTime ourFirstMoonDate = LocalDateTime.of(2021, 10, 20, 17, 56, 41);
        moodDate.add(LocalDate.from(ourFirstMoonDate));
        while (ourFirstMoonDate.compareTo(LocalDateTime.now()) < 0) {
            ourFirstMoonDate = (ourFirstMoonDate.plusDays(29).plusHours(18).plusMinutes(1).plusSeconds(14));
            moodDate.add(LocalDate.from(ourFirstMoonDate));
        }
        return moodDate;
    }

    /**
     * Метод получает разницу +10 от текущего значения-10% и прибавляет минимальное значение.
     *
     * @return BigDecimal число в результат
     */
    private BigDecimal getBigDecimalRange(BigDecimal minIncluding, BigDecimal maxExcluding) {
        return getBigDecimal(maxExcluding.subtract(minIncluding)).add(minIncluding);
    }

    /**
     * Метод получает рандомное число из этой разницы
     *
     * @return BigDecimal рандомное число
     */
    private BigDecimal getBigDecimal(BigDecimal length) {
        return BigDecimal.valueOf(threadLocalRandom.nextDouble(Double.parseDouble(String.valueOf((length)))));
    }

}
