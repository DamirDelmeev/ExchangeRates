package ru.liga.chart;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import ru.liga.constants.Constant;
import ru.liga.currencyFile.CurrencyFileReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс реализует график по листу файлов
 */
public class Chart {
    /**
     * Метод вернёт график
     *
     * @param currencyFileReaders лист файлов
     * @return File
     */
    public File extracted(List<CurrencyFileReader> currencyFileReaders) {
        String name = UUID.randomUUID().toString();
        String path = "target/" + name + ".png";
        Plot plt = Plot.create();
        plt.title("Currency rate forecast");
        plt.ylabel("Currency rate");
        plt.xlabel("Date");
        currencyFileReaders.forEach(file -> addLineToChart(plt, file)
        );
        try {
            plt.legend().loc("upper right");
            plt.savefig(path);
            plt.executeSilently();
        } catch (IOException | PythonExecutionException e) {
            throw new RuntimeException("Ошибка при выводе графика");
        }
        return new File(path);
    }

    /**
     * Метод добавит линию на график
     *
     * @param plt,file сцена и файл по которому будет линия
     */
    private void addLineToChart(Plot plt, CurrencyFileReader file) {
        List<Double> x =
                NumpyUtils.linspace(0, file.getDateForForecastList().size(),
                        file.getDateForForecastList().size());
        List<Double> rate = new ArrayList<>();
        for (int i = 0; i < file.getRateForForecastList().size(); i++) {
            String format = String.format(Constant.BIG_DECIMAL_FORMAT.getName(), file.getRateForForecastList().get(i));
            String s = format.replaceAll(",", "\\.");
            Double aDouble = Double.valueOf(s);
            rate.add(aDouble);
        }
        plt.plot().add(x, rate)
                .label(file.getCurrency().getName());
    }
}