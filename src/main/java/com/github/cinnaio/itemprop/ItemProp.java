package com.github.cinnaio.itemprop;

import com.github.cinnaio.itemprop.event.InteractEvent;
import com.github.cinnaio.itemprop.handler.CommandHandler;
import com.github.cinnaio.itemprop.handler.FileHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemProp extends JavaPlugin {
    private static JavaPlugin instance;

    private static FileHandler fileHandler;

    @Override
    public void onEnable() {
        instance = this;
        fileHandler = new FileHandler(instance);

        Bukkit.getPluginCommand("itemprop").setExecutor(new CommandHandler());
        Bukkit.getPluginCommand("itemprop").setTabCompleter(new CommandHandler());

        Bukkit.getPluginManager().registerEvents(new InteractEvent(), this);
    }

    @Override
    public void onDisable() {

    }

    public static JavaPlugin getInstance() {
        return instance;
    }

    public static FileHandler getFileHandler() {
        return fileHandler;
    }
}
