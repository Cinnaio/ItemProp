package com.github.cinnaio.itemprop.handler;

import com.github.cinnaio.itemprop.ItemProp;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.github.cinnaio.itemprop.handler.i18Handler.*;
import static com.github.cinnaio.itemprop.utils.MessageUtils.sendMessage;

public class CommandHandler implements TabExecutor {
    private final JavaPlugin ins = ItemProp.getInstance();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0)
            return false;

        switch (args[0]) {
            case "reload" : {
                ItemProp.getFileHandler().reload(ins);
                sendMessage((Player) sender, reload);
                break;
            }
            case "add" : {
                FunctionHandler.addCustomTag((Player) sender, args[1], args[2]);
                break;
            }
            case "getmeta" : {
                sendMessage((Player) sender, (getmeta + inferiorColor).replace("{0}", "&n" + ((Player) sender).getInventory().getItemInMainHand().getItemMeta().getAsString()));
                break;
            }
            default : {
                return false;
            }
        }
        return false;
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("reload");
            completions.add("add");
            completions.add("getmeta");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            completions.add("目录名 标签名");
        }

        return completions;
    }
}