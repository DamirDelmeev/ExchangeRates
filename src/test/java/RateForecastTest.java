import org.junit.Assert;
import org.junit.Test;
import ru.liga.Currency;
import ru.liga.RateForecast;

public class RateForecastTest {

    @Test
    public void testReadUserIn() {
        Assert.assertEquals(0, RateForecast.defineAmountDays("rate USD month"));
        Assert.assertEquals(1, RateForecast.defineAmountDays("rate USD tomorrow"));
        Assert.assertEquals(7, RateForecast.defineAmountDays("rate USD week"));
    }

    @Test
    public void testReadFile() {
        Assert.assertEquals(Currency.USD, RateForecast.defineCurrencyType("rate USD month").getCurrency());
        Assert.assertEquals(Currency.EUR, RateForecast.defineCurrencyType("rate EUR tomorrow").getCurrency());
        Assert.assertEquals(Currency.TRY, RateForecast.defineCurrencyType("rate TRY week").getCurrency());
    }
}
