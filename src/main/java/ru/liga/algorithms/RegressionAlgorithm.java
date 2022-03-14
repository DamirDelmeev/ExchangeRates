package ru.liga.algorithms;

import lombok.Data;
import ru.liga.constants.Constant;
import ru.liga.currencyFile.CurrencyFileReader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Класс реализует алгоритм линейной регрессии.
 */
@Data
public class RegressionAlgorithm implements Algorithm {
    /**
     * Поле список результатов используется для тестов.
     */
    List<String> stringListTest = new ArrayList<>();
    /**
     * Поле список курсов используется для тестов.
     */
    List<BigDecimal> resultsRateTest = new ArrayList<>();

    /**
     * Метод реализует алгоритм регрессии.
     *
     * @param list CurrencyFileReader -файлы, в которых есть лист дат, лист курсов и количество дней для прогноза.
     */
    @Override
    public void realizeAlgorithm(List<CurrencyFileReader> list) {
        list.forEach(this::getResult);
    }

    /**
     * Метод реализует алгоритм регрессии по формуле: Y=a+bx.
     * где Y: dependent variable -результат.
     * a: intersect variable with Y-среднее ,узнаем по формуле a=y-bx (xWithBar)(yWithBar)
     * b: slope-отклонение  ,узнаем по формуле b=(sum xy)-nxy)/(sum x*x)-n(x*x))
     * x: independent variable- дата.
     * добавит String result в файл из листа.
     *
     * @param currencyFileReader -файл, в котором есть лист дат, лист курсов и количество дней для прогноза.
     */
    private void getResult(CurrencyFileReader currencyFileReader) {
        List<BigDecimal> resultsRate = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        List<BigDecimal> dateListForRegression = getDateListToBigDecimal(currencyFileReader.getDateList());
        List<BigDecimal> rateListForRegression = getRateListForRegression(currencyFileReader.getRateList());
        List<BigDecimal> bAndAValues =
                getIntersectVariableAndSlopeValues(dateListForRegression, rateListForRegression);
        List<BigDecimal> dateListToForecastDate = getDateListToForecastDate(currencyFileReader.getDateList(),
                currencyFileReader.getDateForForecastList());

        dateListToForecastDate.forEach(date -> resultsRate
                .add(bAndAValues.get(0)
                        .add(bAndAValues.get(1).multiply(date))));
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(Constant.DATE_FORMAT.getName());
        for (int i = 0; i < currencyFileReader.getDateForForecastList().size(); i++) {
            stringList.add(dateFormat.format(currencyFileReader.getDateForForecastList().get(i))
                    + " : " + String.format(Constant.BIG_DECIMAL_FORMAT.getName(), resultsRate.get(i)));
        }
        setAllList(currencyFileReader, resultsRate, stringList);
    }

    /**
     * Метод задаёт значение для файла и тестов.
     *
     * @param currencyFileReader,resultsRate,stringList файлы,курсы,строки
     */
    private void setAllList(CurrencyFileReader currencyFileReader, List<BigDecimal> resultsRate, List<String> stringList) {
        currencyFileReader.setResultString(String.join("\n", stringList));
        currencyFileReader.setRateForForecastList(resultsRate);
        resultsRateTest = new ArrayList<>(resultsRate);
        stringListTest = new ArrayList<>(stringList);
    }

    /**
     * Метод получает среднее & отклонение.
     * a: intersect variable with Y-среднее ,узнаем по формуле a=y-bx (xWithBar)(yWithBar)
     * b: slope-отклонение  ,узнаем по формуле b=(sum xy)-nxy)/sum(x*x)-n(x*x))
     *
     * @param dateListForRegression,rateListForRegression лист курсов и дат в BigDecimal.
     * @return список из значений a и b.
     */
    private List<BigDecimal> getIntersectVariableAndSlopeValues(List<BigDecimal> dateListForRegression,
                                                                List<BigDecimal> rateListForRegression) {
        List<BigDecimal> xAndYList = getRateAndDateSum(dateListForRegression, rateListForRegression);
        BigDecimal xAndYSumListTotal = getSumBigDecimalList(xAndYList);

        BigDecimal xWithBar = getSumBigDecimalList(dateListForRegression)
                .divide(BigDecimal.valueOf(dateListForRegression.size()), 2, RoundingMode.HALF_UP);
        BigDecimal yWithBar =
                getSumBigDecimalList(rateListForRegression)
                        .divide(BigDecimal.valueOf(rateListForRegression.size()), 2, RoundingMode.HALF_UP);

        List<BigDecimal> xSquareList = getXSquareInList(dateListForRegression);
        BigDecimal xSquareSum = getSumBigDecimalList(xSquareList);

        BigDecimal b = getSlope(xAndYSumListTotal, dateListForRegression.size(), xWithBar, yWithBar, xSquareSum);
        BigDecimal a = getIntersectVariable(yWithBar, b, xWithBar);
        return Arrays.asList(a, b);
    }

    /**
     * Метод выдаёт среднее.
     * a: intersect variable with Y-среднее ,узнаем по формуле a=y-bx (xWithBar)(yWithBar)
     *
     * @param yWithBar,b,xWithBar -среднее число по курсам, отклонение, среднее число по датам.
     * @return значение a- среднее
     */
    private BigDecimal getIntersectVariable(BigDecimal yWithBar, BigDecimal b, BigDecimal xWithBar) {
        return yWithBar.subtract(b.multiply(xWithBar));
    }

    /**
     * Метод выдаёт лист дат в BigDecimal.
     *
     * @param dateList - лист дат из файла.
     *                 +1 чтобы не умножать 0 на курс
     * @return лист счётчика дат.
     */
    private List<BigDecimal> getDateListToBigDecimal(LinkedList<LocalDate> dateList) {
        List<BigDecimal> BigDecimalDateList = new ArrayList<>();
        dateList.forEach(localDate -> BigDecimalDateList.add(BigDecimal.valueOf((dateList.indexOf(localDate) + 1))));
        return BigDecimalDateList;
    }

    /**
     * Метод выдаёт лист дат прогноза в BigDecimal.
     *
     * @param dateList -лист дат по которым будет прогноз.
     * @return лист счётчика дат+счётчик дат прогноза.
     */
    private List<BigDecimal> getDateListToForecastDate(List<LocalDate> dateList, List<LocalDate> forecastDate) {
        List<BigDecimal> BigDecimalDateList = new ArrayList<>();
        forecastDate.forEach(localDate ->
                BigDecimalDateList.add(BigDecimal.valueOf((forecastDate.indexOf(localDate)) + 1 + dateList.size())));
        return BigDecimalDateList;
    }

    /**
     * Метод выдаёт лист курсов в BigDecimal.
     *
     * @param rateList - лист курсов по которым будет прогноз.
     * @return лист курсов в BigDecimal.
     */
    private List<BigDecimal> getRateListForRegression(List<BigDecimal> rateList) {
        List<BigDecimal> rateListForRegression = new ArrayList<>(rateList);
        Collections.reverse(rateListForRegression);
        return rateListForRegression;
    }

    /**
     * Метод выдаёт лист суммы курсов и даты (sum xy).
     *
     * @param dateListForRegression,rateListForRegression -лист курсов и дат в BigDecimal.
     * @return лист (sum xy);
     */
    private List<BigDecimal> getRateAndDateSum(List<BigDecimal> dateListForRegression,
                                               List<BigDecimal> rateListForRegression) {
        List<BigDecimal> listXAndYSum = new ArrayList<>();
        for (int i = 0; i < dateListForRegression.size(); i++) {
            listXAndYSum.add((dateListForRegression.get(i).multiply(rateListForRegression.get(i))));
        }
        return listXAndYSum;
    }

    /**
     * Метод выдаёт сумму листа.
     *
     * @param listBigDecimal -лист BigDecimal.
     * @return total values
     */
    private BigDecimal getSumBigDecimalList(List<BigDecimal> listBigDecimal) {
        return listBigDecimal.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Метод выдаёт лист площади курсов валют.
     *
     * @param dateListForRegression - лист дат для которых нужен прогноз.
     * @return list<rate * rate>
     */
    private List<BigDecimal> getXSquareInList(List<BigDecimal> dateListForRegression) {
        List<BigDecimal> xSquareList = new ArrayList<>();
        dateListForRegression.forEach(dateInInteger -> xSquareList.add((dateInInteger.multiply(dateInInteger))));
        return xSquareList;
    }

    /**
     * Метод выдаёт b,slope-отклонение  ,узнаем по формуле b=(sum xy)-nxy)/(sum x*x)-n(x*x))
     * xAndYSumListTotal-(sum xy)
     * n-sizeDateList
     * xWithBar-x
     * yWithBar-y
     * (sum x*x)-xSquareSum
     *
     * @param xAndYSumListTotal,sizeDateList,xWithBar,yWithBar,yWithBar -лист дат для которых нужен прогноз.
     * @return b
     */
    private BigDecimal getSlope(BigDecimal xAndYSumListTotal, Integer sizeDateList,
                                BigDecimal xWithBar, BigDecimal yWithBar, BigDecimal xSquareSum) {
        BigDecimal numerator =
                xAndYSumListTotal.subtract(BigDecimal.valueOf(sizeDateList).multiply(xWithBar.multiply(yWithBar)));
        BigDecimal denominator =
                xSquareSum.subtract(xWithBar.multiply(xWithBar.multiply(BigDecimal.valueOf(sizeDateList))));
        return numerator.divide(denominator, 10, RoundingMode.HALF_UP);
    }
}


