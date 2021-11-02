package org.spagetik.discordverify.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;

public abstract class AbstractDataBase {

    private final String host;
    private final String port;
    private final String name;
    private final String user;
    private final String pass;

    public AbstractDataBase(String host, String port, String name, String user, String pass) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.user = user;
        this.pass = pass;
    }

    public Connection getDbConnection() throws SQLException {
        Connection dbConnection;
        String conStr = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.name;
        dbConnection = DriverManager.getConnection(conStr, this.user, this.pass);
        return dbConnection;
    }

    public ResultSet SendSqlRequest(@NotNull String SqlRequest, @Nullable String[] args) {
        try {
            Connection con = getDbConnection();
            PreparedStatement prSt = con.prepareStatement(SqlRequest);
            if (args != null) {
                for(int i = 0; i < args.length; i++) {
                    prSt.setString(i+1, args[i]);
                }
            }
            if(SqlRequest.contains("SELECT")) {
                return prSt.executeQuery();
            }
            else {
                prSt.executeUpdate();
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}
