package com.github.cinnaio.itemprop.handler;

import com.github.cinnaio.itemprop.ItemProp;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class i18Handler {
    public static String prefix;
    public static String mainColor = "&#F3EAF4";
    public static String inferiorColor = "&#E5CEDC";

    public static String effect_type;
    public static String effect_format;
    public static String command_sender;
    public static String expression_format;
    public static String scarcity_money;
    public static String scarcity_exp;
    public static String error_add;

    public static String bind;
    public static String reload;
    public static String getmeta;
    public static String success_add;


    public i18Handler(File i18) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(i18);

        effect_type = config.getString("error.effect_type");
        effect_format = config.getString("error.effect_format");
        command_sender = config.getString("error.command_sender");
        expression_format = config.getString("error.expression_format");
        scarcity_money = config.getString("error.scarcity_money");
        scarcity_exp = config.getString("error.scarcity_exp");
        error_add = config.getString("error.add");

        bind = config.getString("success.bind");
        reload = config.getString("success.reload");
        getmeta = config.getString("success.getmeta");
        success_add = config.getString("success.add");

        if (ItemProp.getInstance().getConfig().getBoolean("prefix.enable"))
            prefix = ItemProp.getInstance().getConfig().getString("prefix.context");
    }
}
