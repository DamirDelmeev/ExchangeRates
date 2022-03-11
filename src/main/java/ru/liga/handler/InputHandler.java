package ru.liga.handler;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.commands.Commands;
import ru.liga.currencyFile.Currency;
import ru.liga.currencyFile.CurrencyFileReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * ����� ���������� �����
 */
public class InputHandler {
    /**
     * ���� ������������
     */
    private final String userIn;

    /**
     * ������ ��������� ������
     */
    private final List<String> availableCommands = Arrays.asList("rate", "-alg", "actual", "mystical", "regression",
            "-date", "tomorrow", "-output", "list", "graph", "-period", "month", "week",
            Currency.AMD.getName(), Currency.EUR.getName(), Currency.USD.getName(), Currency.TRY.getName(),
            Currency.BGN.getName());
    /**
     * �������� �� ������� ������������ ���� �����
     */
    @Getter
    public boolean isUserCommands;

    public InputHandler(String userIn) {
        this.userIn = userIn;
        isUserCommands = this.checkUserCommands();
    }

    /**
     * ����� ��������� ���� ������������.
     *
     * @return boolean ���� ���� ����� ��������� ������������ �������.
     */
    public boolean checkUserCommands() {
        Logger logger = LoggerFactory.getLogger(InputHandler.class);
        for (String commands : userIn.split("[ ,]")) {
            if (commands.matches("\\d*\\.\\d*\\.\\d*")) {
                return true;
            }
            if (!availableCommands.contains(commands)) {
                logger.error("������ ������������ �������.");
                throw new RuntimeException();
            }
        }
        return true;
    }

    /**
     * ����� ������������ ��� ������.
     *
     * @return ���������� ������ CurrencyFileReader � ���� �������� ����� ������ ������ ������.
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
     * ����� ��������� �������������� ����� �� ����� � ������ ������������
     *
     * @return List<CurrencyFileReader> � ������������
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
        } else {
            throw new RuntimeException();
        }
        return action;
    }
}
