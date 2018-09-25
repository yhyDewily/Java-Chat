package demo.chat.jdbc;

import java.sql.*;

public final class JdbcUtils {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/javaee?useSSL=true";

    static final String USER = "root";
    static final String PASS = "fcbarca1899";
    private JdbcUtils(){

    }
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConncetion() throws SQLException {
        return DriverManager.getConnection(DB_URL,USER,PASS);
    }

    public static void free(ResultSet rs, Statement st, Connection conn) throws SQLException {
        try {
            if(rs!=null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(st!=null)
                    st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (conn != null)
                    conn.close();
            }
        }
    }
}
