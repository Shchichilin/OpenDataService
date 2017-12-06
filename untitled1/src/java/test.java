package java;

import java.io.IOException;

import static java.lang.Runtime.*;

public class test {
    int a;
    public static void stare() throws IOException {
        Runtime a = getRuntime();

        Process proc = a.exec("C:\\OpenDataService\\OpenDataService.exe");
        System.out.println("проверка"+proc);
    }
    public static void main(){}
}
