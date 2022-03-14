package ru.liga.chart;

import org.junit.Test;
import org.mockito.Mockito;
import ru.liga.currencyFile.Currency;
import ru.liga.currencyFile.CurrencyFileReader;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ChartTest {
    @Test
    public void testGetChart() {
        CurrencyFileReader mock = mock(CurrencyFileReader.class);
        List<CurrencyFileReader> list = List.of(mock);
        Chart chart = new Chart();
        List<LocalDate> dateListForForecast = Arrays.asList(
                LocalDate.of(2022, 3, 20),
                LocalDate.of(2022, 3, 21),
                LocalDate.of(2022, 3, 22)
        );
        List<BigDecimal> rateList = Arrays.asList(
                new BigDecimal(10),
                new BigDecimal(9),
                new BigDecimal(11)
        );
        Mockito.when(mock.getDateForForecastList()).thenReturn(dateListForForecast);
        Mockito.when(mock.getRateForForecastList()).thenReturn(rateList);
        Mockito.when(mock.getCurrency()).thenReturn(Currency.USD);
        File file = chart.getChart(list);
        String expected = "target";
        String actual = file.getPath().substring(0, 6);
        assertEquals(expected, actual);
    }
}