import org.junit.Assert;
import org.junit.Test;
import ru.liga.Currency;
import ru.liga.CurrencyFileReader;
import ru.liga.ForecastWithAverage;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ForecastWithAverageTest {
    CurrencyFileReader currencyFileReader = new CurrencyFileReader(Currency.USD);
    ForecastWithAverage forecastWithAverage = new ForecastWithAverage(currencyFileReader, 7,
            currencyFileReader.getRateList());
    //пример мока
    private ForecastWithAverage forecastWithAverageWithMoc = mock(ForecastWithAverage.class);

    @Test
    public void testGetActualInfo() {
        currencyFileReader.readCsvFile();
        Assert.assertEquals(4956, currencyFileReader.getLineList().size());
        forecastWithAverage.getActualInfo();
        LocalDate currentDate = LocalDate.now();
        Period between = Period.between(currentDate, currencyFileReader.getLastUpdate());
        List<Double> list = new ArrayList<>();
        list.add(1.0);
        when(forecastWithAverageWithMoc.getListValues()).thenReturn(list);
        Assert.assertEquals(Math.abs(between.getDays()), forecastWithAverageWithMoc.getListValues().size());
    }

    @Test
    public void testFindAverage() {
        List<Double> listValues = new ArrayList<>();
        listValues.add(10.0);
        listValues.add(15.0);
        listValues.add(25.0);
        listValues.add(40.0);
        listValues.add(50.0);
        listValues.add(60.0);
        listValues.add(80.0);
        Assert.assertEquals(Optional.of(40.0), Optional.of(forecastWithAverage.findAverage(listValues)));
    }
}
