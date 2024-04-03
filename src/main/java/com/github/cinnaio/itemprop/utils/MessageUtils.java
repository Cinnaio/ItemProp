package com.github.cinnaio.itemprop.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.github.cinnaio.itemprop.handler.i18Handler.mainColor;
import static com.github.cinnaio.itemprop.handler.i18Handler.prefix;

public class MessageUtils {
    @Deprecated
    public static void sendActionBar(Player player, String msg) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
    }

    public static void sendMessage(Player p, String msg) {
        p.sendMessage(HexCodeUtils.translateHexCodes(prefix + mainColor + msg));
    }

    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(HexCodeUtils.translateHexCodes(prefix + mainColor + msg));
    }
}
