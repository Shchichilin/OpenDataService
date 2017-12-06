import org.junit.Assert;
import org.junit.Test;
import ru.yandex.qatools.allure.annotations.Description;
import java.io.*;
import java.util.logging.ErrorManager;

import static java.lang.Runtime.getRuntime;


public class OpenDate_Test {

    @Test
    @Description("1. Запуск сервиса OpenDataService")
    public void Step_1_start() throws IOException {
        Runtime a = getRuntime();
        Process proc = a.exec("cmd /c start C:\\OpenDataService\\OpenDataService.exe / console");
    }

    @Test
    @Description("2. Проверка запуска сервиса OpenDataService")
    public void Step_2_CheckAPP() throws IOException {
        Process p = Runtime.getRuntime().exec("tasklist.exe /nh");
        BufferedReader input = new BufferedReader
                (new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            if (!line.trim().equals("")) {
                if (line.substring(0, line.indexOf(" ")).equals("OpenDataService.exe")) {
                    System.out.print("Ура! есть такой процесс!");
                }
            }
        }
    }

    @Test
    @Description("Проверка формирования исходящего запроса от сервиса ")
    public void Step_3_Check_GET_Request() {

    }
}
