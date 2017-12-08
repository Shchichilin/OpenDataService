//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.InputStreamReader;
//import java.math.BigInteger;
//import java.security.MessageDigest;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.zip.GZIPInputStream;
//
//public class DataManager {
//    private static Props prop = Props.getProps();
//
//    public Map<String,Object> getOneTestDataByAddressName(String address, String method){
//        Map<String,Object> map = null;
//        Connection connection = null;
//        Statement statement = null;
//        String query = Context.SQL_GET_ALL_DATA_BY_ADDRESS.replace("{0}", address).replace("{1}",method);
//        try{
//            Class.forName("org.postgresql.Driver");
//            connection = DriverManager.getConnection(prop.CONNECTION_STRING, prop.DB_USER, prop.DB_PWD);
//            statement = connection.createStatement();
//            ResultSet result = statement.executeQuery(query);
//            List<HashMap<String,Object>> list = convertResultSetToMapList(result);
//            map = list.get(0);
//        } catch (Exception ex){
//            ex.printStackTrace();
//        } finally {
//            if (statement != null) {
//                try {
//                    statement.close();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//        return map;
//    }
//
//    public String getIdByIdent(String ident){
//        String id = "";
//        Map<String,Object> map;
//        Connection connection = null;
//        Statement statement = null;
//        String query = "SELECT * FROM TaskQueue where ident = '" + ident + "'";
//        try{
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            connection = DriverManager.getConnection(prop.API_TEST_CONNECTION);
//            statement = connection.createStatement();
//            ResultSet rs = statement.executeQuery(query);
//            ResultSetMetaData md = rs.getMetaData();
//            int columns = md.getColumnCount();
//            List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
//            while (rs.next()) {
//                HashMap<String,Object> row = new HashMap<String, Object>(columns);
//                for(int i=1; i<=columns; ++i) {
//                    row.put(md.getColumnName(i),rs.getObject(i));
//                }
//                list.add(row);
//            }
//            map = list.get(0);
//            id = map.get("Id").toString();
//        } catch (Exception ex){
//            ex.printStackTrace();
//        } finally {
//            if (statement != null) {
//                try {
//                    statement.close();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//        return id;
//    }
//
//    private List<HashMap<String,Object>> convertResultSetToMapList(ResultSet rs) throws SQLException {
//        ResultSetMetaData md = rs.getMetaData();
//        int columns = md.getColumnCount();
//        List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
//        while (rs.next()) {
//            HashMap<String,Object> row = new HashMap<String, Object>(columns);
//            for(int i=1; i<=columns; ++i) {
//                row.put(md.getColumnName(i),rs.getObject(i));
//            }
//            list.add(row);
//        }
//        return list;
//    }
//
//    public String getZippedString (String id) {
//        String result = "";
//        Connection connection = null;
//        Statement statement = null;
//        String query = "SELECT * FROM TaskResult where Id = '" + id + "'";
//        try{
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            connection = DriverManager.getConnection(prop.API_TEST_CONNECTION);
//            statement = connection.createStatement();
//            ResultSet rs = statement.executeQuery(query);
//            ResultSetMetaData md = rs.getMetaData();
//            int columns = md.getColumnCount();
//            List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
//            while (rs.next()) {
//                HashMap<String,Object> row = new HashMap<String, Object>(columns);
//                for(int i=1; i<=columns; ++i) {
//                    row.put(md.getColumnName(i),rs.getObject(i));
//                }
//                list.add(row);
//            }
//            HashMap<String, Object> first = list.get(0);
//            byte[] array = (byte[]) first.get("Data");
//            GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(array));
//            InputStreamReader reader = new InputStreamReader(gzis);
//            BufferedReader in = new BufferedReader(reader);
//            String readed;
//            while ((readed = in.readLine()) != null) {
//                result += readed;
//            }
//        } catch (Exception ex){
//            ex.printStackTrace();
//        } finally {
//            if (statement != null) {
//                try {
//                    statement.close();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//        return result;
//    }
//
//
//
//    private String generateNewHashCode (String id) {
//        String hash = "";
//        try {
//            MessageDigest md5 = MessageDigest.getInstance("MD5");
//            md5.update(id.getBytes(), 0, id.length());
//            hash = String.format("%32x", new BigInteger(1, md5.digest()));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return hash;
//    }
//}
