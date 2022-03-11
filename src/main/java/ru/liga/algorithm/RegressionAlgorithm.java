package ru.liga.algorithm;

import ru.liga.currencyFile.CurrencyFileReader;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * ����� ��������� �������� �������� ���������
 */
public class RegressionAlgorithm implements Algorithm {
    /**
     * ����� ��������� �������� ��������� �� �������: Y=a+bx
     * ��� Y: dependent variable -���������
     * a: intersect variable with Y-������� ,������ �� ������� a=y-bx (xWithBar)(yWithBar)
     * b: slope-����������  ,������ �� ������� b=(sum xy)-nxy)/(sum x*x)-n(x*x))
     * x: independent variable- ����
     * ������� String result � ���� �� �����
     *
     * @param list CurrencyFileReader -������ � ������� ���� ���� ��� ���� ������ � ���������� ���� ��� ��������
     */
    @Override
    public void realizeAlgorithm(List<CurrencyFileReader> list) {
        list.forEach(currencyFileReader -> {
            List<String> stringList = new ArrayList<>();
            List<BigDecimal> dateListForRegression = getDateListToBigDecimal(currencyFileReader.getDateList());
            List<BigDecimal> rateListForRegression = getRateListForRegression(currencyFileReader.getRateList());
            List<BigDecimal> bAndAValues =
                    getIntersectVariableAndSlopeValues(dateListForRegression, rateListForRegression);
            List<BigDecimal> dateListToForecastDate = getDateListToForecastDate(currencyFileReader.getDateList(),
                    currencyFileReader.getDateForForecastList());
            List<BigDecimal> results = new ArrayList<>();
            dateListToForecastDate.forEach(date -> {
                results.add(bAndAValues.get(0).add(bAndAValues.get(1).multiply(date)));
            });
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("EE dd.MM.yyyy");
            for (int i = 0; i < currencyFileReader.getDateForForecastList().size(); i++) {
                stringList.add(dateFormat.format(currencyFileReader.getDateForForecastList().get(i))
                        + " : " + String.format("%.2f", results.get(i)));
            }
            currencyFileReader.setResultString(String.join("\n", stringList));
            currencyFileReader.setRateForForecastList(results);
        });
    }

    /**
     * ����� �������� ������� & ����������
     * a: intersect variable with Y-������� ,������ �� ������� a=y-bx (xWithBar)(yWithBar)
     * b: slope-����������  ,������ �� ������� b=(sum xy)-nxy)/sum(x*x)-n(x*x))
     *
     * @param dateListForRegression,rateListForRegression ���� ������ � ��� � BigDecimal
     * @return ������ �� �������� a � b
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
     * ����� ����� �������
     * a: intersect variable with Y-������� ,������ �� ������� a=y-bx (xWithBar)(yWithBar)
     *
     * @param yWithBar,b,xWithBar -������� ����� �� ������, ����������, ������� ����� �� �����
     * @return �������� a- �������
     */
    private BigDecimal getIntersectVariable(BigDecimal yWithBar, BigDecimal b, BigDecimal xWithBar) {
        return yWithBar.subtract(b.multiply(xWithBar));
    }

    /**
     * ����� ����� ���� ��� � BigDecimal
     *
     * @param dateList -���� ��� �� �����
     * @return ���� �������� ���
     */
    public List<BigDecimal> getDateListToBigDecimal(List<LocalDate> dateList) {
        List<BigDecimal> BigDecimalDateList = new ArrayList<>();
        dateList.forEach(localDate -> {
            BigDecimalDateList.add(BigDecimal.valueOf((dateList.indexOf(localDate) + 1)));
        });
        return BigDecimalDateList;
    }

    /**
     * ����� ����� ���� ��� �������� � BigDecimal
     *
     * @param dateList -���� ��� �� ������� ����� �������
     * @return ���� �������� ���+������� ��� ��������
     */
    public List<BigDecimal> getDateListToForecastDate(List<LocalDate> dateList, List<LocalDate> forecastDate) {
        List<BigDecimal> BigDecimalDateList = new ArrayList<>();
        forecastDate.forEach(localDate -> {
            BigDecimalDateList.add(BigDecimal.valueOf((forecastDate.indexOf(localDate)) + 1 + dateList.size()));
        });
        return BigDecimalDateList;
    }

    /**
     * ����� ����� ���� ������ � BigDecimal
     *
     * @param rateList -���� ������ �� ������� ����� �������
     * @return ���� ������ � BigDecimal;
     */
    public List<BigDecimal> getRateListForRegression(List<BigDecimal> rateList) {
        List<BigDecimal> rateListForRegression = new ArrayList<>(rateList);
        Collections.reverse(rateListForRegression);
        return rateListForRegression;
    }

    /**
     * ����� ����� ���� ����� ������ � ���� (sum xy)
     *
     * @param dateListForRegression,rateListForRegression -���� ������ � ��� � BigDecimal
     * @return ���� (sum xy);
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
     * ����� ����� ����� �����
     *
     * @param listBigDecimal -���� BigDecimal
     * @return total values
     */
    private BigDecimal getSumBigDecimalList(List<BigDecimal> listBigDecimal) {
        return listBigDecimal.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * ����� ����� ���� ������� ������ �����
     *
     * @param dateListForRegression -���� ��� ��� ������� ����� �������
     * @return list<rate * rate>
     */
    private List<BigDecimal> getXSquareInList(List<BigDecimal> dateListForRegression) {
        List<BigDecimal> xSquareList = new ArrayList<>();
        dateListForRegression.forEach(dateInInteger -> {
            xSquareList.add((dateInInteger.multiply(dateInInteger)));
        });
        return xSquareList;
    }

    /**
     * ����� ����� b,slope-����������  ,������ �� ������� b=(sum xy)-nxy)/(sum x*x)-n(x*x))
     * xAndYSumListTotal-(sum xy)
     * n-sizeDateList
     * xWithBar-x
     * yWithBar-y
     * (sum x*x)-xSquareSum
     *
     * @param xAndYSumListTotal,sizeDateList,xWithBar,yWithBar,yWithBar -���� ��� ��� ������� ����� �������
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


