package com.github.cinnaio.itemprop;

import com.github.cinnaio.itemprop.event.InteractEvent;
import com.github.cinnaio.itemprop.handler.CommandHandler;
import com.github.cinnaio.itemprop.handler.FileHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class ItemProp extends JavaPlugin {
    private static JavaPlugin instance;

    private static FileHandler fileHandler;

    private static Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        fileHandler = new FileHandler(instance);

        Bukkit.getPluginCommand("itemprop").setExecutor(new CommandHandler());
        Bukkit.getPluginCommand("itemprop").setTabCompleter(new CommandHandler());

        Bukkit.getPluginManager().registerEvents(new InteractEvent(), this);

        if (!setupEconomy()) {
            getLogger().severe("缺少 Vault 必要前置，插件卸载。");
            getServer().getPluginManager().disablePlugin(this);
        }
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

    public static Economy getEconomy() {
        return economy;
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        } else {
            RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);

            if (rsp == null) {
                return false;
            } else {
                economy = (Economy)rsp.getProvider();
                return true;
            }
        }
    }
}
