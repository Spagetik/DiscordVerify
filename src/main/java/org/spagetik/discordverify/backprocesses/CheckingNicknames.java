package org.spagetik.discordverify.backprocesses;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.spagetik.discordverify.DiscordVerify;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CheckingNicknames {

    private String getNickname (UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    private void setNickname(@NotNull Member member, @NotNull String nickname) {
        member.modifyNickname(nickname).queue();
    }

    private void editDatabaseNickname(UUID uuid, String nickname) {
        DiscordVerify.getGuildDataBase().editNickname(uuid, nickname);
    }

    public void check() {
        ResultSet resultSet = DiscordVerify.getGuildDataBase().getAll();
        while (true) {
            try {
                if (!resultSet.next()) break;
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String discordId = resultSet.getString("discord_id");
                String nickname = resultSet.getString("username");
                String guildId = DiscordVerify.getInstance().getConfig().getString("discord.guild.guild_id");
                assert guildId != null;
                Guild guild = DiscordVerify.getBot().getGuildById(guildId);
                assert guild != null;
                Member member = guild.getMemberById(discordId);
                assert member != null;
                if (!nickname.equalsIgnoreCase(getNickname(uuid))) {
                    setNickname(member, nickname);
                    editDatabaseNickname(uuid, nickname);
                }
                else if (!nickname.equalsIgnoreCase(member.getNickname())) {
                    setNickname(member, nickname);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
