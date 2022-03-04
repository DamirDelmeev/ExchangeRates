import org.junit.Assert;
import org.junit.Test;
import ru.liga.Currency;
import ru.liga.CurrencyFileReader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CurrencyFileReaderTest {
    CurrencyFileReader currencyFileReader = new CurrencyFileReader(Currency.USD);

    @Test
    public void testFindRateListAndDate() {
        List<String> listForTest = new ArrayList<>();
        listForTest.add("27.02.2022;21.12213;some STR");
        currencyFileReader.findRateListAndDate(listForTest);
        LocalDate localDate = LocalDate.of(2022, 02, 27);
        Assert.assertEquals(localDate, currencyFileReader.getLastUpdate());
    }

    @Test
    public void testReadCsvFile() {
        currencyFileReader.readCsvFile();
        Assert.assertEquals(4956, currencyFileReader.getLineList().size());
    }

}
