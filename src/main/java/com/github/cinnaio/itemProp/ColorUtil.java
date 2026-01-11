package com.github.cinnaio.itemProp;

import net.md_5.bungee.api.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    public static String color(String text) {
        if (text == null) return "";
        
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();
        
        while (matcher.find()) {
            try {
                ChatColor color = ChatColor.of("#" + matcher.group(1));
                matcher.appendReplacement(buffer, color.toString());
            } catch (Exception e) {
                // 忽略错误
            }
        }
        matcher.appendTail(buffer);
        text = buffer.toString();

        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
