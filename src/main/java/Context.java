public class Context {
    public static String SQL_GET_ALL_DATA_From_OpenDataMasAddress = "SELECT * FROM OpenDataMasAddress";
    public static String SQL_Delete_All_Rows_From_OpenDataMasAddress = "TRUNCATE TABLE  OpenDataMasAddress";
    public static String SQL_GET_ALL_DATA_From_OpenDataPath = "SELECT * FROM OPENDATAPATH";
    public static String SQL_DELETE_ALL_DATA_From_OpenDataPath = "TRUNCATE TABLE  OPENDATAPATH";
    public static String SQL_GET_FIRST_RECORD_From_OpenDataMasAddress = "SELECT Hash FROM OpenDataMasAddress where id = 0;";

}
