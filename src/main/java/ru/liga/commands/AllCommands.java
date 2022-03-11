package ru.liga.commands;

import ru.liga.currencyFile.CurrencyFileReader;

import java.util.List;

/**
 * Интерфейс AllCommands
 */
public interface AllCommands {

    List<CurrencyFileReader> action(String argument, List<CurrencyFileReader> list);
}
