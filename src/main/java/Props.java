import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class Props {
    private static Props instance;
    private final Properties prop = new Properties();
    private InputStream input;

    public String CONNECTION_STRING;
    public String DB_USER;
    public String DB_PWD;

    private Props(){
        try{
            input = new FileInputStream("src/main/resources/config.properties");
            prop.load(input);
            CONNECTION_STRING = prop.getProperty("connection_string");
            DB_USER = prop.getProperty("db_user");
            DB_PWD = prop.getProperty("db_pwd");
        } catch (Exception ex){
            ex.printStackTrace();
            System.exit(10);
        } finally {
            finish();
        }
    }

    public static synchronized Props getProps(){
        if (instance == null)
            instance = new Props();
        return instance;
    }

    private void finish() {
        if (input!=null){
            try {
                input.close();
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}