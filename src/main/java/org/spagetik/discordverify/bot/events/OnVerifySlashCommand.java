package org.spagetik.discordverify.bot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.spagetik.discordverify.DiscordVerify;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OnVerifySlashCommand extends ListenerAdapter {

    private String getNickname (UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    private void sendMsgCodeDoesntExist(@NotNull SlashCommandEvent event) {
        event.reply("Code doesn't exist").setEphemeral(true).queue();
    }

    private void sendMsgSuccess(@NotNull SlashCommandEvent event) {
        event.reply("Verification is successful!").setEphemeral(true).queue();
    }

    private void sendBanMessage(@NotNull SlashCommandEvent event) {
        event.reply("You was banned in this guild!").setEphemeral(true).queue();
    }

    private void setNickname(@NotNull Member member, @NotNull String nickname) {
        member.modifyNickname(nickname).queue();
    }

    private void giveRole(@NotNull Member member, @NotNull Guild guild) {
        String role_id = DiscordVerify.getInstance().getConfig().getString("discord.guild.common_role_id");
        assert role_id != null;
        Role role = guild.getRoleById(role_id);
        assert role != null;
        guild.addRoleToMember(member, role).queue();
    }

    private void giveRoles(@NotNull Member member, @NotNull Guild guild, @NotNull List<Role> roles) {
        for (Role role : roles) {
            guild.addRoleToMember(member, role).queue();
        }
    }

    private @NotNull List<Role> removeAllRoles(@NotNull Member member, @NotNull Guild guild) {
        List<Role> roles = member.getRoles();
        for (Role role : roles) {
            guild.removeRoleFromMember(member, role).queue();
        }
        return roles;
    }

    private void addMemberToDatabase(@NotNull Member member, @NotNull UUID uuid, @NotNull String nickname) {
        DiscordVerify.getGuildDataBase().addNewMember(member, uuid, nickname);
    }

    private void editDiscordId(@NotNull String old_id, @NotNull String new_id) {
        DiscordVerify.getGuildDataBase().editDiscordId(old_id, new_id);
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getName().equals("verify")) {
            OptionMapping option = event.getOption("code");
            assert option != null;
            long code = option.getAsLong();
            Member member = event.getMember();
            Guild guild = event.getGuild();
            ResultSet data = DiscordVerify.getCodeDataBase().checkCode((int) code);
            try {
                if (data.next()) {
                    UUID uuid = UUID.fromString(data.getString("uuid"));
                    ResultSet member_exist = DiscordVerify.getGuildDataBase().checkIfMemberExist(uuid);
                    if (!member_exist.next()) {
                        String nickname = getNickname(uuid);
                        assert member != null;
                        addMemberToDatabase(member, uuid, nickname);
                        assert guild != null;
                        giveRole(member, guild);
                        try {
                            setNickname(member, nickname);
                        } catch (HierarchyException e) {
                            assert true;
                        }
                        sendMsgSuccess(event);
                    }
                    else {
                        assert member != null;
                        if (Objects.equals(member_exist.getString("discord_id"), member.getId())) {
                            if (Objects.equals(member_exist.getString("banned"), "0")) {
                                String nickname = getNickname(uuid);
                                assert guild != null;
                                giveRole(member, guild);
                                try {
                                    setNickname(member, nickname);
                                } catch (HierarchyException e) {
                                    assert true;
                                }
                                sendMsgSuccess(event);
                            }
                            else {
                                sendBanMessage(event);
                            }
                        }
                        else if (!Objects.equals(member_exist.getString("discord_id"), member.getId())) {
                            if (Objects.equals(member_exist.getString("banned"), "0")) {
                                String oldMemberId = member_exist.getString("discord_id");
                                User oldUser = DiscordVerify.getBot().getUserById(oldMemberId);
                                if (oldUser != null) {
                                    oldUser.openPrivateChannel()
                                            .flatMap(channel -> channel.sendMessage("<@" + oldMemberId + "> , you verified from new account - <@" + member.getId() + ">." +
                                                    "\n\n**If you don't move your account, text to server administration!**"))
                                            .queue();
                                    assert guild != null;
                                    Member oldMember = guild.getMember(oldUser);
                                    if (oldMember != null) {
                                        List<Role> roles = removeAllRoles(oldMember, guild);
                                        giveRoles(member, guild, roles);
                                    }
                                }
                                else {
                                    assert guild != null;
                                    giveRole(member, guild);
                                }
                                String nickname = getNickname(uuid);
                                editDiscordId(oldMemberId, member.getId());
                                setNickname(member, nickname);
                                sendMsgSuccess(event);
                            }
                            else {
                                sendBanMessage(event);
                            }
                        }
                    }
                }
                else {
                    sendMsgCodeDoesntExist(event);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DiscordVerify.getCodeDataBase().removeCodeFromDb((int) code);
        }
    }
}
