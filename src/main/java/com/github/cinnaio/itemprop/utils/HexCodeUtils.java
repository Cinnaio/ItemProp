package com.github.cinnaio.itemprop.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexCodeUtils {
    public static String translateHexCodes(String message) {
        Pattern hexPattern = Pattern.compile("&#" + "([A-Fa-f0-9]{6})" + "");
        return translate(hexPattern, message);
    }

    @Deprecated
    private static String translate(Pattern hex, String message) {
        Matcher matcher = hex.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 32);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(buffer,
                    "§x§" + group.charAt(0) + "§" + group.charAt(1) + "§" + group.charAt(2)
                            + "§" + group.charAt(3) + "§" + group.charAt(4) + "§" + group.charAt(5));
        }
        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString()).replace("&", "§");
    }
}
