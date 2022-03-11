package ru.liga.chart;

import com.github.sh0nk.matplotlib4j.NumpyUtils;
import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
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
        currencyFileReaders.forEach(file -> {
                    List<Double> x =
                            NumpyUtils.linspace(0, file.getDateForForecastList().size(),
                                    file.getDateForForecastList().size());
                    List<Double> rate = new ArrayList<>();
                    for (int i = 0; i < file.getRateForForecastList().size(); i++) {
                        String format = String.format("%.2f", file.getRateForForecastList().get(i));
                        String s = format.replaceAll(",", "\\.");
                        Double aDouble = Double.valueOf(s);
                        rate.add(aDouble);
                    }
                    plt.plot().add(x, rate)
                            .label(file.getCurrency().getName());
                }
        );
        try {
            plt.legend().loc("upper right");
            plt.savefig(path);
            plt.executeSilently();
        } catch (IOException | PythonExecutionException e) {
            throw new RuntimeException(e);
        }
        return new File(path);
    }
}