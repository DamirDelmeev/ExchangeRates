package ru.liga.algorithms;

import org.junit.Test;
import org.mockito.Mockito;
import ru.liga.constants.Constant;
import ru.liga.currencyFile.CurrencyFileReader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class RegressionAlgorithmTest {
    @Test
    public void testRealizeAlgorithmGetRate() {
        CurrencyFileReader mock = mock(CurrencyFileReader.class);
        List<CurrencyFileReader> list = List.of(mock);
        RegressionAlgorithm regressionAlgorithm = new RegressionAlgorithm();

        LinkedList<LocalDate> dateList = new LinkedList<>();
        dateList.add(LocalDate.of(2021, 10, 20));
        dateList.add(LocalDate.of(2021, 11, 19));
        dateList.add(LocalDate.of(2021, 12, 19));
        List<BigDecimal> rateList = Arrays.asList(
                new BigDecimal(10),
                new BigDecimal(11),
                new BigDecimal(11)
        );

        List<LocalDate> dateListForForecast = List.of(
                LocalDate.of(2022, 4, 14)
        );
        Mockito.when(mock.getDateForForecastList()).thenReturn(dateListForForecast);
        Mockito.when(mock.getDateList()).thenReturn(dateList);
        Mockito.when(mock.getRateList()).thenReturn(rateList);
        regressionAlgorithm.realizeAlgorithm(list);
        assertEquals(String.format(Constant.BIG_DECIMAL_FORMAT.getName(), new BigDecimal("9.65000")),
                String.format(Constant.BIG_DECIMAL_FORMAT.getName(), regressionAlgorithm.getResultsRateTest().get(0)));
    }

    @Test
    public void testRealizeAlgorithmGetString() {
        CurrencyFileReader mock = mock(CurrencyFileReader.class);
        List<CurrencyFileReader> list = List.of(mock);
        RegressionAlgorithm regressionAlgorithm = new RegressionAlgorithm();

        LinkedList<LocalDate> dateList = new LinkedList<>();
        dateList.add(LocalDate.of(2021, 10, 20));
        dateList.add(LocalDate.of(2021, 11, 19));
        dateList.add(LocalDate.of(2021, 12, 19));
        List<BigDecimal> rateList = Arrays.asList(
                new BigDecimal(10),
                new BigDecimal(11),
                new BigDecimal(11)
        );

        List<LocalDate> dateListForForecast = List.of(
                LocalDate.of(2022, 4, 14)
        );
        Mockito.when(mock.getDateForForecastList()).thenReturn(dateListForForecast);
        Mockito.when(mock.getDateList()).thenReturn(dateList);
        Mockito.when(mock.getRateList()).thenReturn(rateList);
        regressionAlgorithm.realizeAlgorithm(list);

        String expected = " 14.04.2022 : 9,65";
        String actual = regressionAlgorithm.getStringListTest().get(0).substring(2);
        assertEquals(expected, actual);
    }
}