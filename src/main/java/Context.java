public class Context {
    public static String SQL_GET_ALL_DATA_From_OpenDataMasAddress = "SELECT * FROM OpenDataMasAddress";
    public static String SQL_Delete_All_Rows_From_OpenDataMasAddress = "TRUNCATE TABLE  OpenDataMasAddress";
    public static String SQL_GET_FIRST_RECORD_From_OpenDataMasAddress = "SELECT  Region,Area,City,Town,Street,House,Building,Flat,EntityNumber " +
            "FROM OpenDataMasAddress where id = 1";
}
