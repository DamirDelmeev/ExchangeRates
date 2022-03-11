package ru.liga.commands;

import lombok.Getter;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.algorithm.ActualAlgorithm;
import ru.liga.algorithm.Algorithm;
import ru.liga.algorithm.MysticalAlgorithm;
import ru.liga.algorithm.RegressionAlgorithm;
import ru.liga.bot.Bot;
import ru.liga.chart.Chart;
import ru.liga.currencyFile.CurrencyFileReader;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * ENUM команды
 */

public enum Commands implements AllCommands {
    /**
     * ENUM ALG
     *
     * есть команда и список аргументов
     * есть метод, который в зависимости от полученного аргумента вызывает конструктор
     * алгоритма
     */
    ALG("alg", List.of("actual", "mystical", "regression")) {
        @Override
        public List<CurrencyFileReader> action(String argument, List<CurrencyFileReader> list) {
            checkArguments(argument);
            switch (argument) {
                case "actual": {
                    Algorithm actual = new ActualAlgorithm();
                    actual.realizeAlgorithm(list);
                    return list;
                }
                case "mystical": {
                    Algorithm mystical = new MysticalAlgorithm();
                    mystical.realizeAlgorithm(list);
                    return list;
                }
                case "regression": {
                    Algorithm regression = new RegressionAlgorithm();
                    regression.realizeAlgorithm(list);
                    return list;
                }
            }
            return null;
        }
    },
    /**
     * ENUM DATE
     *
     * есть команда и список аргументов
     * есть метод, который в зависимости от полученного аргумента создаст список дат
     * прогноза
     */
    DATE("date", List.of("tomorrow")) {
        @Override
        public List<CurrencyFileReader> action(String argument, List<CurrencyFileReader> list) {
            List<LocalDate> listDateResult = new ArrayList<>();
            if (isDate(argument)) {
                listDateResult.add(parseDate(argument));
            } else {
                checkArguments(argument);
                if (argument.equals("tomorrow")) {
                    listDateResult.add(LocalDate.now().plusDays(1));
                }
            }
            list.forEach(file -> file.setDateForForecastList(listDateResult));
            return list;
        }
    },
    /**
     * ENUM PERIOD
     *
     * есть команда и список аргументов
     * есть метод, который в зависимости от полученного аргумента создаст список дат
     * прогноза
     */
    PERIOD("period", List.of("month", "week")) {
        @Override
        public List<CurrencyFileReader> action(String argument, List<CurrencyFileReader> list) {
            checkArguments(argument);
            List<LocalDate> listDateResult ;
            if (argument.equals("month")) {
                listDateResult = datesRangeClosed(LocalDate.now().plusDays(1),
                        LocalDate.now().plusMonths(1).plusDays(1));
            } else {
                listDateResult = datesRangeClosed(LocalDate.now().plusDays(1),
                        LocalDate.now().plusWeeks(1).plusDays(1));
            }
            List<LocalDate> finalListDateResult = listDateResult;
            list.forEach(file -> file.setDateForForecastList(finalListDateResult));
            return list;
        }
    },
    /**
     * ENUM OUTPUT
     *
     * есть команда и список аргументов
     * есть метод, который в зависимости от полученного аргумента выдаст результат в
     * разных форматах
     */
    OUTPUT("output", List.of("graph", "list")) {
        @Override
        public List<CurrencyFileReader> action(String argument, List<CurrencyFileReader> list) {
            checkArguments(argument);
            if (argument.equals("graph")) {
                list.get(0).setShowChart(true);
                Chart lineChartSample = new Chart();
                File extracted = lineChartSample.extracted(list);
                list.get(0).setFile(extracted);
                return list;
            } else {
                return list;
            }
        }
    };
    /**
     * Команда
     */
    @Getter
    public final String value;
    /**
     * Список аргументов
     */
    private final List<String> arguments;

    Commands(String value, List<String> arguments) {
        this.value = value;
        this.arguments = arguments;
    }

    /**
     * Метод реализует проверку на наличие аргумента в списке
     *
     * @param argument-аргумент после команды new RuntimeException if false
     */
    protected void checkArguments(String argument) {
        if (arguments.contains(argument)) {
        } else {
            Logger logger = LoggerFactory.getLogger(Commands.class);
            logger.error("Ошибка неправильный аргумент");
            throw new RuntimeException();
        }
    }

    /**
     * Метод реализует вызов команды (из ввода пользователя)
     *
     * @param commands- команды (из ввода пользователя)
     * @return Commands: -alg=ALG
     */
    public static Commands checkCommands(String commands) {
        return Arrays.stream(values()).filter(v -> v.getValue().equals(commands.toLowerCase())).findFirst().orElse(null);
    }

    /**
     * Метод реализует проверку аргумента на случай, если это дата
     *
     * @param date- аргумент (из ввода пользователя)
     * @return boolean
     */
    public boolean isDate(String date) {
        try {
            parseDate(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Метод реализует parse даты из ввода в формат LocaleDateTime
     *
     * @param date- аргумент (из ввода пользователя)
     * @return LocalDate
     */
    public LocalDate parseDate(String date) {
        try {
            return DateUtils.parseDate(date,
                    "d.MM.yyyy").toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Метод создаёт список дат из полученного периода
     *
     * @param fromInclusive,toInclusive-от даты, до даты
     * @return List<LocalDate>
     */
    public List<LocalDate> datesRangeClosed(LocalDate fromInclusive, LocalDate toInclusive) {
        long daysInPeriod = fromInclusive.until(toInclusive, DAYS);
        return LongStream.rangeClosed(0, daysInPeriod)
                .mapToObj(fromInclusive::plusDays)
                .collect(Collectors.toList());
    }
}
