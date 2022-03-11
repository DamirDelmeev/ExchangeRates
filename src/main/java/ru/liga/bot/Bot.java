package ru.liga.bot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.liga.currencyFile.CurrencyFileReader;
import ru.liga.handler.InputHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс реализует бота
 *
 * @version 1.0
 * @autor Дельмеев Дамир
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
     * Метод реализует работу бота и взаимодействие с пользователем
     *
     * @param update сообщение любого типа
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String chatId = String.valueOf(update.getMessage().getChatId());
            try {
                InputHandler inputHandler = new InputHandler(messageText);
                inputHandler.defineCurrencyType();
                List<CurrencyFileReader> currencyFileReaders = inputHandler.realizeCommands();
                if (!currencyFileReaders.get(0).showChart) {
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
                } else {
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
            } catch (Exception exception) {
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
        }
    }
}
