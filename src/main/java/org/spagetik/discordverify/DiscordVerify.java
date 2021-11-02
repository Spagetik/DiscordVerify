package org.spagetik.discordverify;

import net.dv8tion.jda.api.JDA;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spagetik.discordverify.backprocesses.CheckingNicknames;
import org.spagetik.discordverify.bot.DiscordBot;
import org.spagetik.discordverify.commands.DiscordVerifyCommand;
import org.spagetik.discordverify.sql.CodeDataBase;
import org.spagetik.discordverify.sql.GuildDataBase;

public final class DiscordVerify extends JavaPlugin {

    private static DiscordVerify instance;
    private static DiscordBot bot;
    private static CodeDataBase codeDataBase;
    private static GuildDataBase guildDataBase;
    private static CheckingNicknames checkingNicknames;

    public static DiscordVerify getInstance() {
        return instance;
    }

    public static JDA getBot() {
        return bot.jda();
    }

    public static CodeDataBase getCodeDataBase() {
        return codeDataBase;
    }

    public static GuildDataBase getGuildDataBase() {
        return guildDataBase;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        bot = new DiscordBot();
        codeDataBase = new CodeDataBase(this.getConfig().getString("code_database.host"),
                this.getConfig().getString("code_database.port"),
                this.getConfig().getString("code_database.tablename"),
                this.getConfig().getString("code_database.username"),
                this.getConfig().getString("code_database.password"));
        guildDataBase = new GuildDataBase(this.getConfig().getString("guild_database.host"),
                this.getConfig().getString("guild_database.port"),
                this.getConfig().getString("guild_database.tablename"),
                this.getConfig().getString("guild_database.username"),
                this.getConfig().getString("guild_database.password"));
        checkingNicknames = new CheckingNicknames();
        new DiscordVerifyCommand();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () -> {
            this.getLogger().info("Started checking nick");
            checkingNicknames.check();
            this.getLogger().info("Finished checking nick");
        }, 1000, 1500000);
    }

    @Override
    public void onDisable() {
        bot.jda().removeEventListener(bot.jda().getRegisteredListeners());
        bot.jda().shutdown();
    }
}
