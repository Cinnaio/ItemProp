package com.github.cinnaio.itemprop.handler;

import com.github.cinnaio.itemprop.ItemProp;
import com.github.cinnaio.itemprop.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler implements TabExecutor {
    private final JavaPlugin ins = ItemProp.getInstance();

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0)
            return false;

        switch (args[0]) {
            case "reload" : {
                ItemProp.getFileHandler().reload(ins);
                MessageUtils.sendMessage(sender, "重载成功!");
                break;
            }
            case "list" : {
                sender.sendMessage("bbbb");
                break;
            }
            case "add" : {
                FunctionHandler.addCustomTag((Player) sender, args[1]);
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
            completions.add("list");
            completions.add("add");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            completions.add("[context]");
        }

        return completions;
    }
}