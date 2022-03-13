package ru.liga.algorithms;

import org.junit.Test;
import org.mockito.Mockito;
import ru.liga.constants.Constant;
import ru.liga.currencyFile.CurrencyFileReader;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class MysticalAlgorithmTest {

    @Test
    public void testGetRate() {
        CurrencyFileReader mock = mock(CurrencyFileReader.class);
        List<CurrencyFileReader> list = List.of(mock);
        MysticalAlgorithm mysticalAlgorithm = new MysticalAlgorithm();

        LinkedList<LocalDate> dateList = new LinkedList<>();
        dateList.add(LocalDate.of(2021, 10, 20));
        dateList.add(LocalDate.of(2021, 11, 19));
        dateList.add(LocalDate.of(2021, 12, 19));
        List<BigDecimal> rateList = Arrays.asList(
                new BigDecimal(10),
                new BigDecimal(11),
                new BigDecimal(11)
        );
        Mockito.when(mock.getDateList()).thenReturn(dateList);
        Mockito.when(mock.getRateList()).thenReturn(rateList);
        Mockito.when(mock.getDateForForecastList())
                .thenReturn(new ArrayList<>(Collections.singleton(LocalDate.of(2022, 3, 14))));
        mysticalAlgorithm.realizeAlgorithm(list);

        assertEquals(String.format(Constant.BIG_DECIMAL_FORMAT.getName(), new BigDecimal("10.66666666")),
                String.format(Constant.BIG_DECIMAL_FORMAT.getName(), mysticalAlgorithm.getResultsRateTest().get(0)));

    }

    @Test
    public void testGetDate() {
        CurrencyFileReader mock = mock(CurrencyFileReader.class);
        List<CurrencyFileReader> list = List.of(mock);
        MysticalAlgorithm mysticalAlgorithm = new MysticalAlgorithm();

        LinkedList<LocalDate> dateList = new LinkedList<>();
        dateList.add(LocalDate.of(2021, 10, 20));
        dateList.add(LocalDate.of(2021, 11, 19));
        dateList.add(LocalDate.of(2021, 12, 19));
        List<BigDecimal> rateList = Arrays.asList(
                new BigDecimal(10),
                new BigDecimal(11),
                new BigDecimal(11)
        );
        Mockito.when(mock.getDateList()).thenReturn(dateList);
        Mockito.when(mock.getRateList()).thenReturn(rateList);
        Mockito.when(mock.getDateForForecastList())
                .thenReturn(new ArrayList<>(Collections.singleton(LocalDate.of(2022, 3, 14))));

        mysticalAlgorithm.realizeAlgorithm(list);
        String expected = " 14.03.2022 : 10,67";
        String actual = mysticalAlgorithm.getStringListTest().get(0).substring(2);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetMoonDate() {
        CurrencyFileReader mock = mock(CurrencyFileReader.class);
        List<CurrencyFileReader> list = List.of(mock);
        MysticalAlgorithm mysticalAlgorithm = new MysticalAlgorithm();

        LinkedList<LocalDate> dateList = new LinkedList<>();
        dateList.add(LocalDate.of(2021, 10, 20));
        dateList.add(LocalDate.of(2021, 11, 19));
        dateList.add(LocalDate.of(2021, 12, 19));
        List<BigDecimal> rateList = Arrays.asList(
                new BigDecimal(10),
                new BigDecimal(11),
                new BigDecimal(11)
        );
        Mockito.when(mock.getDateList()).thenReturn(dateList);
        Mockito.when(mock.getRateList()).thenReturn(rateList);
        Mockito.when(mock.getDateForForecastList())
                .thenReturn(new ArrayList<>(Collections.singleton(LocalDate.of(2022, 3, 14))));
        mysticalAlgorithm.realizeAlgorithm(list);

        assertEquals(6, mysticalAlgorithm.moonDate.size());
    }
}