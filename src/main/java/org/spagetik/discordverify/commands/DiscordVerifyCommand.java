package org.spagetik.discordverify.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;


public class DiscordVerifyCommand extends AbstractCommand {

    private final ArrayList<UUID> playersWhoHaveCode = new ArrayList<>();

    public DiscordVerifyCommand() {
        super("discordverify");
    }

    private int RandomCode() {
        return new Random().nextInt((999999 - 100000) + 1) + 100000;
    }

    private void sendSuccessMsg(@NotNull Player player, int code) {
        String msg = org.spagetik.discordverify.DiscordVerify.getInstance().getConfig().getString("minecraft.messages.successful");
        assert msg != null;
        if (msg.contains("{player}")) {
            msg = msg.replaceAll("\\{player}", player.getName());
        }
        if (msg.contains("{code}")) {
            msg = msg.replaceAll("\\{code}", String.valueOf(code));
        }
        TextComponent message = new TextComponent(msg);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(code)));
        player.sendMessage(message);
    }

    private void sendUnsuccessfulMsg(@NotNull Player player) {
        String msg = org.spagetik.discordverify.DiscordVerify.getInstance().getConfig().getString("minecraft.messages.unsuccessful");
        assert msg != null;
        player.sendMessage(msg);
    }

    private void sendYouAlreadyHaveCode(@NotNull Player player) {
        String msg = org.spagetik.discordverify.DiscordVerify.getInstance().getConfig().getString("minecraft.messages.havecode");
        assert msg != null;
        player.sendMessage(msg);
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            if (!playersWhoHaveCode.contains(uuid)) {
                int code = RandomCode();
                playersWhoHaveCode.add(uuid);
                if (org.spagetik.discordverify.DiscordVerify.getCodeDataBase().addCodeToDb(uuid, code)) sendSuccessMsg(player, code);
                else sendUnsuccessfulMsg(player);
                Bukkit.getScheduler().runTaskLaterAsynchronously(org.spagetik.discordverify.DiscordVerify.getInstance(),() -> {
                    if (org.spagetik.discordverify.DiscordVerify.getCodeDataBase().removeCodeFromDb(code)) playersWhoHaveCode.remove(uuid);
                }, 1200);
            }
            else sendYouAlreadyHaveCode(player);
        }
        else sender.sendMessage("You can't use this command via console!");
    }
}
