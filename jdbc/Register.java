package demo.chat.jdbc;

import java.sql.*;

public class Register {
    public static void register(String name, String psd) throws SQLException {
        Connection conn = null;
        conn = JdbcUtils.getConncetion();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sql = "INSERT INTO chatuser (name, password)  VALUE (?, ?)";
        ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ps.setString(2, psd);
        ps.execute();
    }

    /*public static void main(String args[]) throws SQLException {
        register("Dewily", "123456");
    }*/
}
