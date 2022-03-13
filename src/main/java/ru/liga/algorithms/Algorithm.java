package ru.liga.algorithms;

import ru.liga.currencyFile.CurrencyFileReader;

import java.util.List;

/**
 * Интерфейс алгоритм
 */
public interface Algorithm {
    void realizeAlgorithm(List<CurrencyFileReader> list);
}
