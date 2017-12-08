import com.sun.deploy.util.Waiter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.qatools.allure.annotations.Description;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;
import java.util.logging.ErrorManager;

import static java.lang.Runtime.getRuntime;


public class OpenDate_Test {
    private static Props prop = Props.getProps();

    @Test
    @Description("0.1Почищены записи в таблице (OpenDataMasAddress) + ссылки на сохраненный файл")
    public void Step_0_Preconditions()  {
        DB_Connect.Delete_All_Rows_From_Table(Context.SQL_Delete_All_Rows_From_OpenDataMasAddress);
        DB_Connect.Delete_All_Rows_From_Table(Context.SQL_DELETE_ALL_DATA_From_OpenDataPath);
    }

    @Test
    @Description("1. Запуск сервиса OpenDataService")
    public void Step_1_Start() throws IOException, InterruptedException {
        Process a = Runtime.getRuntime().exec("cmd /c start C:\\OpenDataService\\OpenDataService.exe / console");
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    @Description("2. Проверка запуска сервиса OpenDataService") //
    // - Search for this service in windows processes.
    public void Step_2_CheckAPP() throws IOException {
        Process p = Runtime.getRuntime().exec("tasklist.exe /nh");
        BufferedReader input = new BufferedReader
                (new InputStreamReader(p.getInputStream()));
        String line;
        Boolean a;
        while ((line = input.readLine()) != null) {
            if (!line.trim().equals("")) {
                if (a = line.substring(0, line.indexOf(" ")).equals("OpenDataService.exe")){
                    System.out.print("Ура! есть такой процесс!");
                }
            }
        }
    }

    @Test
    @Description("Проверка формирования исходящего(GET) запроса от сервиса")
    public void Step_3_Check_GET_Request() {
//        HttpRequest<String> httpRequest = HttpRequestBuilder.createGet(uri, String.class)
//                .responseDeserializer(ResponseDeserializer.ignorableDeserializer()).build();
    }

    @Test
    @Description("5. Проверка создания записей в таблице (OpenDataMasAddress) + ссылка на местонахождение файла  *.csv\n")
    public void Step_4_Check_rows_in_table(){
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(prop.CONNECTION_STRING, prop.DB_USER, prop.DB_PWD);
            if (connection != null) System.out.println("Connection Successful !\n");
            if (connection == null) System.exit(0);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs_OpenDataMassAddress = statement.executeQuery(Context.SQL_GET_ALL_DATA_From_OpenDataMasAddress);
            ResultSet rs_OpenDataPath = statement.executeQuery(Context.SQL_GET_ALL_DATA_From_OpenDataPath);
            int Count_Rows = DB_Connect.GetResultSetRowCount(rs_OpenDataMassAddress);
            int Count_Rows_Path = DB_Connect.GetResultSetRowCount(rs_OpenDataPath);

            if (Count_Rows ==0 && Count_Rows_Path ==0){
                Assert.assertEquals("В таблице отсутствуют записи", "Rows !=0","Rows ==0");
            } else System.out.println("Количество записей в таблице OpenDataMassAddress = " + Count_Rows
                    + "Количество записей в таблице OpenDataPath = "+ Count_Rows_Path);
            if (rs_OpenDataMassAddress != null) rs_OpenDataMassAddress.close();
            if (rs_OpenDataPath != null) rs_OpenDataPath.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
