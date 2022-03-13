package ru.liga.handler;

import org.junit.Test;
import ru.liga.currencyFile.CurrencyFileReader;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InputHandlerTest {

    @Test()
    public void testCheckUserCommands() {

        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> {
                    InputHandler inputHandler =
                            new InputHandler("rate USD,EUR -date some -alg actual -output list");
                    inputHandler.checkUserCommands();
                }).withMessage("Ошибка неправильная команда или аргумент.");
    }

    @Test
    public void testDefineCurrencyType() {
        InputHandler inputHandler = new InputHandler("rate USD,EUR -date tomorrow -alg actual -output list");
        List<CurrencyFileReader> currencyFileReaders = inputHandler.defineCurrencyType();
        assertEquals(2, currencyFileReaders.size());
    }


    @Test
    public void testIsUserCommands() {
        InputHandler inputHandler = new InputHandler("rate USD,EUR -date tomorrow -alg actual -output list");
        assertTrue(inputHandler.isUserCommands);
    }
}