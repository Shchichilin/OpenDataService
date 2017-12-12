import com.opencsv.CSVReader;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.yandex.qatools.allure.annotations.Description;
import java.io.*;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OpenDate_Test {
    private static Props prop = Props.getProps();
    byte[] Hash_DB;
    byte[] hash_CSV;
    byte[] messageBuffer;
    boolean status_tsk = false;

    @Test
    @Description("0. Почищены записи в таблице (OpenDataMasAddress) + ссылки на сохраненный файл(OpenDataPath " +
            "+ удален файл .txt с логами)")
    public void Step0_Preconditions() {
        DB_Connect.Delete_All_Rows_From_Table(Context.SQL_Delete_All_Rows_From_OpenDataMasAddress);
        //DB_Connect.Delete_All_Rows_From_Table(Context.SQL_DELETE_ALL_DATA_From_OpenDataPath);

    }

    @Test
    @Description("1. Запуск сервиса OpenDataService")
    public void Step1_Start() throws Exception{
        Process a = Runtime.getRuntime().exec("cmd /c start C:\\OpenDataService\\OpenDataService.exe / console");
        TimeUnit.SECONDS.sleep(60);
    }

    @Test
    @Description("2. Проверка запуска сервиса OpenDataService") // Search for this service in windows processes.
    public void Step2_CheckAPP() throws Exception {
        Process p = Runtime.getRuntime().exec("tasklist.exe /nh");
        BufferedReader input = new BufferedReader
                (new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = input.readLine()) != null) {
            if (!line.trim().equals("")) {
                if (line.substring(0, line.indexOf(" ")).equals("OpenDataService.exe")) {
                    status_tsk = true;
                    System.out.print("Ура! есть такой процесс!");
                }
            }
        }
        if (status_tsk !=true){
            System.out.println(status_tsk);
            throw new Exception("Процесс отсутствует!!!");}
    }

    @Test
    @Description("5. Проверка создания записей в таблице (OpenDataMasAddress)")
    public void Step3_Check_Rows_In_Table() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(prop.CONNECTION_STRING, prop.DB_USER, prop.DB_PWD);
            if (connection != null) System.out.println("Connection Successful !\n");
            if (connection == null) System.exit(0);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs_OpenDataMassAddress = statement.executeQuery(Context.SQL_GET_ALL_DATA_From_OpenDataMasAddress);
            int Count_Rows = DB_Connect.GetResultSetRowCount(rs_OpenDataMassAddress);
            ResultSet rs_OpenDataPath = statement.executeQuery(Context.SQL_GET_ALL_DATA_From_OpenDataPath);
            int Count_Rows_Path = DB_Connect.GetResultSetRowCount(rs_OpenDataPath);

            if (Count_Rows_Path == 0 || Count_Rows == 0) {
                System.out.println("Количество записей в таблице OpenDataMassAddress = " + Count_Rows
                        + "    Количество записей в таблице OpenDataPath = " + Count_Rows_Path);
                Assert.assertEquals("В таблице отсутствуют записи", "Rows !=0", "Rows ==0");
            } else System.out.println("Количество записей в таблице OpenDataMassAddress = " + Count_Rows
                    + "    Количество записей в таблице OpenDataPath = " + Count_Rows_Path);
            if (rs_OpenDataMassAddress != null) rs_OpenDataMassAddress.close();
            if (rs_OpenDataPath != null) rs_OpenDataPath.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Description("Выбор строки из CSV файда и подсчет ее Hash-кода")
    public void Step4_Check_Hash_In_CSV() throws Exception {
        String line;

        CSVReader reader = new CSVReader(new FileReader("C:\\OpenDataService\\Storage\\masaddress\\2017\\12\\08\\15\\data-19112017-structure-06242015.csv"), ';', '"', 1);
        String[] firstLine = reader.readNext();
        if (firstLine == null)
            throw new Exception("Файл пустой");

        line = Arrays.toString(firstLine);
        String FirstRow = line.substring(1, line.length() - 1);
        System.out.println("Первая строка в файле .CSV =  " + FirstRow); // Первая строка в .CSV файле без []

        messageBuffer = FirstRow.getBytes();
        MessageDigest sha1digest = MessageDigest.getInstance("SHA-1");
        hash_CSV = sha1digest.digest(messageBuffer); // Hash-код первой строки из .CSV
        System.out.println("hash_CSV = " + hash_CSV);
//    }
//
//    @Test
//    @Description("Выбор строки из таблицы БД и подсчет ее Hash-кода")
//    public void Step5_Check_Hash_In_DB() throws NoSuchAlgorithmException {

        /** Description("Выбор строки из таблицы БД и подсчет ее Hash-кода") */

        ResultSet resultSet;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(prop.CONNECTION_STRING, prop.DB_USER, prop.DB_PWD);
            if (connection != null) System.out.println("Connection Successful !\n");
            if (connection == null) System.exit(0);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(Context.SQL_GET_FIRST_RECORD_From_OpenDataMasAddress);

            int Count_Rows = DB_Connect.GetResultSetRowCount(resultSet);
            int x = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= x; i++) {
                    Hash_DB = resultSet.getBytes(i); /** Hash_DB = Hash код первой записи из тыблицы*/
                    System.out.print("Hash_DB = "+ Hash_DB);
                }
                System.out.println();
            }

            /**------- Проверка что запрос вернул хотя-юы одну запись ------------*/
            if (Count_Rows == 0) {
                System.out.println("Количество записей в таблице OpenDataMassAddress = " + Count_Rows);
                Assert.assertEquals("В таблице отсутствуют записи", "Rows !=0", "Rows ==0");
            } else System.out.println("Количество выбранных записей для подсчета Hash'a в таблице OpenDataMassAddress = " + Count_Rows);

            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

            /** Description("Проверка равенства Hash-кода выбранных строк в .CSV файле и БД") */

//        if (Hash_DB == hash_CSV){
//            System.out.println("Test_check = ");
//        } else throw new Exception("Hash-коды не равны.  Hash_DB = "+Hash_DB + "  hash_CSV = "+ hash_CSV);
    }
}
