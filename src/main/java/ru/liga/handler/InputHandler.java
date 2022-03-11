package ru.liga.handler;

import lombok.Getter;
import ru.liga.commands.Commands;
import ru.liga.currencyFile.Currency;
import ru.liga.currencyFile.CurrencyFileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Класс обработчик ввода
 */
public class InputHandler {
    /**
     * Ввод пользователя
     */
    private final String userIn;

    /**
     * Список доступных команд
     */
    private final List<String> availableCommands = Arrays.asList("rate", "-alg", "actual", "mystical", "regression",
            "-date", "tomorrow", "-output", "list", "graph", "-period", "month", "week",
            Currency.AMD.getName(), Currency.EUR.getName(), Currency.USD.getName(), Currency.TRY.getName(),
            Currency.BGN.getName());


    /**
     * Проверка на наличие недопустимых слов вводе
     */
    @Getter
    public boolean isUserCommands;

    public InputHandler(String userIn) {
        this.userIn = userIn;
        isUserCommands = this.checkUserCommands();
    }

    /**
     * Метод проверяет ввод пользователя.
     *
     * @return boolean если ввод будет содержать недопустимые команды.
     */
    public boolean checkUserCommands() {
        for (String commands : userIn.split("[ ,]")) {
            if (commands.matches("\\d*\\.\\d*\\.\\d*")) {
                return true;
            }
            if (!availableCommands.contains(commands)) {
                throw new RuntimeException("Ошибка неправильная команда или аргумент");
            }
        }
        return true;
    }

    /**
     * Метод определяющий тип валюты.
     *
     * @return возвращает список CurrencyFileReader в этих объектах будет список курсов валюты.
     */
    public List<CurrencyFileReader> defineCurrencyType() {
        List<CurrencyFileReader> currencyFileList = new ArrayList<>();
        Currency[] availableCurrency = {Currency.EUR, Currency.TRY, Currency.USD, Currency.AMD, Currency.BGN};
        for (Currency currency : availableCurrency) {
            if (userIn.contains(currency.getName())) {
                CurrencyFileReader currencyFileReader = new CurrencyFileReader(currency);
                currencyFileReader.readCsvFile();
                currencyFileList.add(currencyFileReader);
            }
        }
        return currencyFileList;
    }

    /**
     * Метод реализует взаимодействие файла из листа и команд пользователя
     *
     * @return List<CurrencyFileReader> с результатами
     */
    public List<CurrencyFileReader> realizeCommands() {
        List<CurrencyFileReader> action = new ArrayList<>();
        if (isUserCommands) {
            String substring = userIn.substring(userIn.indexOf('-'));
            String[] commands = substring.split("-");
            List<CurrencyFileReader> currencyFileReaders = this.defineCurrencyType();
            for (String command : commands) {
                if (!command.isEmpty()) {
                    String[] commandsAndArgument = command.split(" ");
                    action = Commands.checkCommands(commandsAndArgument[0]).action(commandsAndArgument[1], currencyFileReaders);
                }
            }
        }
        return action;
    }
}
