package testapi.common.api;
import java.sql.*;
import java.util.ArrayList;

public class PgApi {
    Statement stmt = null;
    Connection c = null;
    public  PgApi(String ip,int port,String user,String password,String dbname){
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection(String.format("jdbc:postgresql://%s:%s/%s",ip,String.valueOf(port),dbname),
                            user, password);
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        System.out.println("Operation done successfully");
    }



    public ArrayList execsqlex(String sql)
    {
        try {
            stmt = c.createStatement();
            ArrayList res=new ArrayList<String>();
            ResultSet rs = stmt.executeQuery( sql+";");
            int column=rs.getMetaData().getColumnCount();
            while ( rs.next() ) {
                String int_value="";
                for(int i=1;i<column+1;i++) {
                    int_value =int_value+rs.getString(i)+",";
                }
                int_value=int_value.substring(0,int_value.length()-1);
                res.add(int_value);
            }
            return res;
        } catch ( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    public String execsql(String sql)
    {
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( sql+";");
            while ( rs.next() ) {
                String int_value = rs.getString(1);
                return int_value.trimCLRF();
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            return "";
        }
        return "";
    }



}
