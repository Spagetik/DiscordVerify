package org.spagetik.discordverify.sql;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.UUID;

public class GuildDataBase extends AbstractDataBase {

    public GuildDataBase(String host, String port, String name, String user, String pass) {
        super(host, port, name, user, pass);
        this.SendSqlRequest("CREATE TABLE IF NOT EXISTS discord_users (" +
                "uuid VARCHAR(36) NOT NULL, " +
                "username VARCHAR(16) NOT NULL," +
                "discord_id BIGINT NOT NULL," +
                "banned BOOL NOT NULL)", null);
    }

    public ResultSet checkIfMemberExist(UUID uuid) {
        String[] data = new String[1];
        data[0] = String.valueOf(uuid);
        return this.SendSqlRequest("SELECT * FROM discord_users WHERE uuid = ?", data);
    }

    public ResultSet getAll() {
        return this.SendSqlRequest("SELECT * FROM discord_users", null);
    }

    public void addNewMember(@NotNull Member member, @NotNull UUID uuid, @NotNull String nickname) {
        String[] data = new String[4];
        data[0] = String.valueOf(uuid);
        data[1] = nickname;
        data[2] = member.getId();
        data[3] = "0";
        this.SendSqlRequest("INSERT INTO discord_users (uuid, username, discord_id, banned) VALUES (?, ?, ?, ?)", data);
    }

    public void editDiscordId(String old_id, String new_id) {
        String[] data = new String[2];
        data[0] = new_id;
        data[1] = old_id;
        this.SendSqlRequest("UPDATE discord_users SET discord_id = ? WHERE discord_id = ?", data);
    }

    public void banMember(@NotNull User user) {
        String[] data = new String[2];
        data[0] = "1";
        data[1] = user.getId();
        this.SendSqlRequest("UPDATE discord_users SET banned = ? WHERE discord_id = ?", data);
    }

    public void unBanMember(@NotNull User user) {
        String[] data = new String[2];
        data[0] = "0";
        data[1] = user.getId();
        this.SendSqlRequest("UPDATE discord_users SET banned = ? WHERE discord_id = ?", data);
    }

    public void editNickname(UUID uuid, String nickname) {
        String[] data = new String[2];
        data[0] = nickname;
        data[1] = String.valueOf(uuid);
        this.SendSqlRequest("UPDATE discord_users SET username = ? WHERE uuid = ?", data);
    }
}
