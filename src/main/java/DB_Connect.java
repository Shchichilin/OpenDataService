import org.junit.Assert;
import org.junit.Test;

import java.sql.*;

public class DB_Connect {
    private static Props prop = Props.getProps();

/**--------------------------------------------------------------------------------------------------------------------*/
    public static int GetResultSetRowCount(ResultSet resultSet) { // Метод для определения количества строк в таблице
        int size = 0;
        try {
            resultSet.last();
            size = resultSet.getRow();
            resultSet.beforeFirst();
        }
        catch(SQLException ex) {
            return 0;
        }
        return size;
    }
/**--------------------------------------------------------------------------------------------------------------------*/
@Test
    public void Check_New_Rows_OpenDataMasAddress()
    {
        String Get_All_Data_By_OpenDataMasAddress = Context.SQL_GET_ALL_DATA_From_OpenDataMasAddress;
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(prop.CONNECTION_STRING, prop.DB_USER, prop.DB_PWD);
            if (connection != null) System.out.println("Connection Successful !\n");
            if (connection == null) System.exit(0);
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery(Context.SQL_GET_FIRST_RECORD_From_OpenDataMasAddress);
            int Count_Rows = GetResultSetRowCount(resultSet);
            System.out.println("Количество записей в таблице = "+ Count_Rows);
            int x = resultSet.getMetaData().getColumnCount(); //Resultset.getMetaData() получаем информацию результирующей таблице
            while (resultSet.next())
            {
                for (int i = 1; i <= x; i++)
                {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
            }
            if (Count_Rows == 0) {
                System.out.println("Количество записей в таблице OpenDataMassAddress = " + Count_Rows);
                Assert.assertEquals("В таблице отсутствуют записи", "Rows !=0", "Rows ==0");
            } else System.out.println("Количество записей в таблице OpenDataMassAddress = " + Count_Rows);
            System.out.println();
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void Delete_All_Rows_From_Table(String Context){
        try
        {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection connection = DriverManager.getConnection(prop.CONNECTION_STRING, prop.DB_USER, prop.DB_PWD);
            if (connection != null) System.out.println("Connection Successful !\n");
            if (connection == null) System.exit(0);
            Statement statement = connection.createStatement();
            statement.executeUpdate(String.valueOf(Context));

            if (statement != null) statement.close();
            if (connection != null) connection.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    }