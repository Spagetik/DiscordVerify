package org.spagetik.discordverify.bot.events;

import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.spagetik.discordverify.DiscordVerify;

public class OnBan extends ListenerAdapter {

    @Override
    public void onGuildBan(@NotNull GuildBanEvent event) {
        DiscordVerify.getGuildDataBase().banMember(event.getUser());
    }
}
