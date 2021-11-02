package org.spagetik.discordverify.sql;

import java.sql.ResultSet;
import java.util.UUID;

public class CodeDataBase extends AbstractDataBase {

    public CodeDataBase(String host, String port, String name, String user, String pass) {
        super(host, port, name, user, pass);
        this.SendSqlRequest("CREATE TABLE IF NOT EXISTS verify_codes (uuid varchar(36) NOT NULL, code varchar(6) NOT NULL)", null);
    }

    public boolean addCodeToDb (UUID uuid, int code) {
        String[] data = new String[2];
        data[0] = String.valueOf(uuid);
        data[1] = String.valueOf(code);
        this.SendSqlRequest("INSERT INTO verify_codes (uuid, code) VALUES (?, ?)", data);
        return true;
    }

    public boolean removeCodeFromDb (int code) {
        String[] data = new String[1];
        data[0] = String.valueOf(code);
        this.SendSqlRequest("DELETE FROM verify_codes WHERE code = ?", data);
        return true;
    }

    public ResultSet checkCode(int code) {
        String[] data = new String[1];
        data[0] = String.valueOf(code);
        return this.SendSqlRequest("SELECT uuid FROM verify_codes WHERE code= ?", data);
    }
}
