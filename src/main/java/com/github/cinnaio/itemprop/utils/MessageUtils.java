package com.github.cinnaio.itemprop.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {
    @Deprecated
    public static void sendActionBar(Player player, String msg) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
    }

    public static void sendMessage(Player p, String msg) {
        p.sendMessage(HexCodeUtils.translateHexCodes("&#FBEDF6" + msg));
    }

    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(HexCodeUtils.translateHexCodes("&#FBEDF6" + msg));
    }
}
