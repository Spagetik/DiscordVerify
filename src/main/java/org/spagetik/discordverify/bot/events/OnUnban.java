package org.spagetik.discordverify.bot.events;

import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.spagetik.discordverify.DiscordVerify;

public class OnUnban extends ListenerAdapter {
    @Override
    public void onGuildUnban(@NotNull GuildUnbanEvent event) {
        DiscordVerify.getGuildDataBase().unBanMember(event.getUser());
    }
}
