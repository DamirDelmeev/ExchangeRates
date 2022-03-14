package ru.liga.algorithms;


import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.liga.currencyFile.CurrencyFileReader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class ActualAlgorithmTest {

    @Test
    public void testRealizeAlgorithm() {
        ActualAlgorithm actualAlgorithm = new ActualAlgorithm();
        CurrencyFileReader mock = mock(CurrencyFileReader.class);

        List<CurrencyFileReader> list = List.of(mock);
        List<BigDecimal> rateList = Arrays.asList(
                new BigDecimal(10),
                new BigDecimal(11)
        );
        LinkedList<LocalDate> dateList = new LinkedList<>();
        dateList.add(LocalDate.of(2020, 10, 20));
        dateList.add(LocalDate.of(2019, 10, 20));
        List<LocalDate> dateListForForecast = List.of(
                LocalDate.of(2022, 10, 20)
        );

        Mockito.when(mock.getRateList()).thenReturn(rateList);
        Mockito.when(mock.getDateForForecastList()).thenReturn(dateListForForecast);
        Mockito.when(mock.getDateList()).thenReturn(dateList);

        actualAlgorithm.realizeAlgorithm(list);
        assertEquals(new BigDecimal(21), actualAlgorithm.getResultsRateTest().get(0));
    }

    @Test
    public void testRealizeAlgorithmForException() {
        CurrencyFileReader mock = mock(CurrencyFileReader.class);
        ActualAlgorithm actualAlgorithm = new ActualAlgorithm();

        List<CurrencyFileReader> list = List.of(mock);
        List<BigDecimal> rateList = Arrays.asList(
                new BigDecimal(10),
                new BigDecimal(11)
        );
        LinkedList<LocalDate> dateList = new LinkedList<>();
        dateList.add(LocalDate.of(2020, 10, 20));
        dateList.add(LocalDate.of(2019, 10, 20));
        List<LocalDate> dateListForForecast = List.of(
                LocalDate.of(2023, 10, 20)
        );

        Mockito.when(mock.getRateList()).thenReturn(rateList);
        Mockito.when(mock.getDateForForecastList()).thenReturn(dateListForForecast);
        Mockito.when(mock.getDateList()).thenReturn(dateList);

        assertThrows(RuntimeException.class, () -> actualAlgorithm.realizeAlgorithm(list));
    }
}