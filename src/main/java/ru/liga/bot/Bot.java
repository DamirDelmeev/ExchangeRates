package ru.liga.bot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.currencyFile.Currency;
import ru.liga.currencyFile.CurrencyFileReader;
import ru.liga.handler.InputHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс реализует бота.
 *
 * @version 1.0
 * @autor Дельмеев Дамир.
 */
public class Bot extends TelegramLongPollingBot {
    final Logger logger = LoggerFactory.getLogger(Bot.class);

    @Override
    public String getBotUsername() {
        return "ForecastCurrencyRateBot";
    }

    @Override
    public String getBotToken() {
        return "5181388962:AAHWz-DKJLBCaVPjWcFf_l88d00BIwXK6oI";
    }

    /**
     * Метод реализует работу бота и взаимодействие с пользователем.
     *
     * @param update сообщение любого типа.
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = String.valueOf(update.getMessage().getChatId());
            if (messageText.equals("/help")) {
                showHelpMenuForUser(chatId);
            } else {
                try {
                    InputHandler inputHandler = new InputHandler(messageText);
                    inputHandler.defineCurrencyType();
                    List<CurrencyFileReader> currencyFileReaders = inputHandler.realizeCommands();
                    if (!currencyFileReaders.get(0).showChart) {
                        showListRateForUser(chatId, currencyFileReaders);
                    } else {
                        showGraphForUser(chatId, currencyFileReaders);
                    }
                } catch (Exception exception) {
                    showErrorMassageForUser(chatId, exception);

                }
            }
        }
    }

    private void showHelpMenuForUser(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Доступные валюты:" + Currency.AMD.getName() + "," + Currency.EUR.getName() + "," +
                Currency.USD.getName() + "," + Currency.TRY.getName() + "," + Currency.BGN.getName() +
                ".\nДоступные временные промежутки:month, week, tomorrow, (any date)for example 22.12.2022. " +
                "\nДоступные алгоритмы:actual, mystical, regression. " +
                "\nДоступный вид результата: list,graph." +
                "\nПримеры команд:" +
                "\nrate USD,EUR -period month -alg actual -output list" +
                "\nrate BGN -period week -alg regression -output graph" +
                "\nrate EUR -date tomorrow -alg actual -output list" +
                "\nrate USD -period week -alg mystical -output graph" +
                "\nrate USD,EUR -period month -alg mystical -output graph"+
                "\nrate TRY -date 22.12.2022 -alg actual -output list");
        try {
            execute(message);
            logger.info("log message: {}", "Пользователь получил меню команд.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error("log message: {}", e.getMessage());
        }
    }

    private void showErrorMassageForUser(String chatId, Exception exception) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Ошибка ввода,попробуйте ещё раз.");
        logger.error("log message: {}", exception.getMessage());
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error("log message: {}", e.getMessage());
        }
    }

    private void showGraphForUser(String chatId, List<CurrencyFileReader> currencyFileReaders) {
        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);
        photo.setPhoto(new InputFile(currencyFileReaders.get(0).getFile()));
        try {
            execute(photo);
            logger.info("log message: {}", "Пользователь получил график.");
        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error("log message: {}", e.getMessage());
        }
    }

    private void showListRateForUser(String chatId, List<CurrencyFileReader> currencyFileReaders) {
        String result = currencyFileReaders.stream()
                .map(CurrencyFileReader::getResultString)
                .collect(Collectors.joining("\n\nNext currency type:\n\n"));
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(result);
        try {
            execute(message);
            logger.info("log message: {}", message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            logger.error("log message: {}", e.getMessage());
        }
    }
}
