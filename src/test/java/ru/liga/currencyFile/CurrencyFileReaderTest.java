package ru.liga.currencyFile;

import org.junit.Test;
import ru.liga.constants.Constant;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CurrencyFileReaderTest {
    @Test
    public void testReadCsvFile() {
        CurrencyFileReader currencyFileReader = new CurrencyFileReader(Currency.USD);
        currencyFileReader.readCsvFile();
        assertEquals(4233, currencyFileReader.getLineList().size());
    }

    @Test
    public void testFindRateListAndDate() {
        List<String> list = Arrays.asList("1;05.03.2022;116.5312;Евро", "2;06.03.2022;11.5312;Евро");
        CurrencyFileReader currencyFileReader = new CurrencyFileReader(Currency.EUR);
        currencyFileReader.findRateListAndDate(list);
        assertEquals(2, currencyFileReader.getDateList().size());
        assertEquals(String.format(Constant.BIG_DECIMAL_FORMAT.getName(), new BigDecimal("116.5312")),
                String.format(Constant.BIG_DECIMAL_FORMAT.getName(), currencyFileReader.getRateList().get(0)));
    }
}