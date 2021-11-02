package org.spagetik.discordverify.bot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.jetbrains.annotations.NotNull;
import org.spagetik.discordverify.DiscordVerify;

public class OnReady extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        DiscordVerify.getInstance().getLogger().info("Discord bot is ready");
        String guild_id = DiscordVerify.getInstance().getConfig().getString("discord.guild.guild_id");
        assert guild_id != null;
        Guild guild = DiscordVerify.getBot().getGuildById(guild_id);
        CommandData verifyCommand = new CommandData("verify","Verify command")
                .addOption(OptionType.INTEGER, "code", "Code which you on from minecraft server", true);
        assert guild != null;
        guild.upsertCommand(verifyCommand).queue();
    }
}
