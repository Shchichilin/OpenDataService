
import java.io.IOException;

import static java.lang.Runtime.getRuntime;

public class OpenDataTests {
    public static void main() throws IOException {
        Runtime a = getRuntime();

        Process proc = a.exec("C:\\OpenDataService\\OpenDataService.exe");
        System.out.println("проверка"+proc);
    }
}
