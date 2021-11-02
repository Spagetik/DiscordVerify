package org.spagetik.discordverify.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.spagetik.discordverify.DiscordVerify;
import org.spagetik.discordverify.bot.events.*;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;


public class DiscordBot {

    private JDA jda;

    public DiscordBot() {
        EnumSet<GatewayIntent> intents = EnumSet.allOf(
                GatewayIntent.class
        );
        try {
            String token = DiscordVerify.getInstance().getConfig().getString("discord.bot.token");
            jda = JDABuilder.create(token, intents).build();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        jda.addEventListener(new OnVerifySlashCommand());
        jda.addEventListener(new OnReady());
        jda.addEventListener(new OnBan());
        jda.addEventListener(new OnUnban());
    }

    public JDA jda() {
        return jda;
    }
}
