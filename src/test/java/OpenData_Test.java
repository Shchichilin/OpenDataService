import com.opencsv.CSVReader;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ru.yandex.qatools.allure.annotations.Description;
import java.io.*;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OpenData_Test {
    private static Props prop = Props.getProps();
    private boolean status_tsk = false;
    private int Hash_Code_DB;

    @Test
    @Description("0. Почищены записи в таблице (OpenDataMasAddress)")
    public void Step0_Preconditions() throws Exception {

        DB_Connect.Delete_All_Rows_From_Table(Context.SQL_Delete_All_Rows_From_OpenDataMasAddress);
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection connection = DriverManager.getConnection(prop.CONNECTION_STRING, prop.DB_USER, prop.DB_PWD);
        if (connection == null) System.exit(0);
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs_OpenDataMassAddress = statement.executeQuery(Context.SQL_GET_ALL_DATA_From_OpenDataMasAddress);
        int Count_Rows = DB_Connect.GetResultSetRowCount(rs_OpenDataMassAddress);

        if (Count_Rows != 0) {
            System.out.println("Количество записей в таблице OpenDataMassAddress = " + Count_Rows);
            Assert.assertEquals("В таблице присутствуют записи", "Rows ==0", "Rows !=0");
        }
    }

    @Test
    @Description("1. Запуск сервиса OpenDataService")
    public void Step1_Start() throws Exception{
        Runtime.getRuntime().exec("cmd /c start C:\\OpenDataService\\OpenDataService.exe / console");
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
                    System.out.print("Ура! есть такой процесс! \n");
                }
            }
        }
        if (!status_tsk){
            throw new Exception("Процесс OpenDataService.exe отсутствует!!!");}
    }

    @Test
    @Description("5. Проверка создания записей в таблице (OpenDataMasAddress)")
    public void Step3_Check_Rows_In_Table() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(prop.CONNECTION_STRING, prop.DB_USER, prop.DB_PWD);
            if (connection == null) System.exit(0);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs_OpenDataMassAddress = statement.executeQuery(Context.SQL_GET_ALL_DATA_From_OpenDataMasAddress);
            int Count_Rows = DB_Connect.GetResultSetRowCount(rs_OpenDataMassAddress);

            if (Count_Rows == 0) {
                System.out.println("Количество записей в таблице OpenDataMassAddress = " + Count_Rows);
                Assert.assertEquals("В таблице отсутствуют записи", "Rows !=0", "Rows ==0");
            } else System.out.println("Количество записей в таблице OpenDataMassAddress = ");
            if (rs_OpenDataMassAddress != null) rs_OpenDataMassAddress.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Description("Выбор строки из CSV файда и подсчет ее Hash-кода")
    public void Step4_Check_HashCode() throws Exception {

        /** Путь к файлу с .CSV временно указан вручную. */

        CSVReader reader = new CSVReader(new FileReader("C:\\OpenDataService\\Storage\\masaddress\\2017\\12\\08\\15\\data-19112017-structure-06242015.csv")
                ,';', '"', 2);
        String[] firstLine = reader.readNext();
        if (firstLine == null)
            throw new Exception("Файл пустой");

        String line = Arrays.toString(firstLine);
        String FirstRow = line.substring(1, line.length() - 1);
        System.out.println("Выбранная строка в файле .CSV = " + FirstRow); // Первая строка в .CSV файле без []
        int hash_Code_CSV = FirstRow.hashCode();
        System.out.println("Hash_Code_CSV = " + hash_Code_CSV);

        /** Description("Выбор строки из таблицы БД и подсчет ее Hash-кода") */

        ResultSet resultSet;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(prop.CONNECTION_STRING, prop.DB_USER, prop.DB_PWD);
            if (connection == null) System.exit(0);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            resultSet = statement.executeQuery(Context.SQL_GET_FIRST_RECORD_From_OpenDataMasAddress);

            while (resultSet.next()) {
                String Region = resultSet.getString("Region");
                String Area = resultSet.getString("Area");
                String City = resultSet.getString("City");
                String Town = resultSet.getString("Town");
                String Street = resultSet.getString("Street");
                String House = resultSet.getString("House");
                String Building = resultSet.getString("Building");
                String Flat = resultSet.getString("Flat");
                String EntityNumber = resultSet.getString("EntityNumber");

                String response = Region + ", " + Area + ", " + City + ", " + Town + ", "
                        + Street + ", " + House + ", " + Building + ", " + Flat + ", " + EntityNumber;
                response = response.replace("null","");

                Hash_Code_DB = response.hashCode();
                System.out.println("Выбранная строка из БД = " + response + "\n" +"Hash_Code_DB = " + Hash_Code_DB);
            }
            System.out.println();

            /**------- Проверка что запрос вернул хотя-бы одну запись ------------*/

            int Count_Rows = DB_Connect.GetResultSetRowCount(resultSet);
            if (Count_Rows == 0) {
                System.out.println("Количество записей в таблице OpenDataMassAddress = " + Count_Rows);
                Assert.assertEquals("В таблице отсутствуют записи", "Rows !=0", "Rows ==0");
            } else System.out.println("Количество выбранных записей для подсчета Hash'a в таблице OpenDataMassAddress = "
                    + Count_Rows + "\n");

            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

            /** Description("Проверка равенства Hash-кода выбранных строк в .CSV файле и БД") */

        if (hash_Code_CSV == Hash_Code_DB){
            System.out.println("Данные в БД соответствуют данным в файле .CSV");
        } else {System.out.println(Hash_Code_DB + "\n" + hash_Code_CSV);
            throw new Exception("Hash-коды не равны.  Hash_DB = " + Hash_Code_DB + "\n" + "Hash_CSV = " + hash_Code_CSV);}

             /** Description("Убиваем процесс OpenDataService.exe") */

            Scanner sc = new Scanner(Runtime.getRuntime().exec("tasklist").getInputStream());
            StringBuffer sb = new StringBuffer();
            while (sc.hasNext()){
                sb.append(sc.nextLine());
            }
            if (sb.toString().indexOf("OpenDataService.exe") != -1){
                Runtime.getRuntime().exec("taskkill /f /im OpenDataService.exe");
            }
            Thread.sleep(500);
    }
}
