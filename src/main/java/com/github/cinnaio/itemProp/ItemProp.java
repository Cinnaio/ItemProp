package com.github.cinnaio.itemProp;

import org.bukkit.plugin.java.JavaPlugin;

public final class ItemProp extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig(); // 保存默认配置
        
        if (getCommand("itemprop") != null) {
            ItemPropCommand cmd = new ItemPropCommand(this);
            getCommand("itemprop").setExecutor(cmd);
            getCommand("itemprop").setTabCompleter(cmd);
        }
        getServer().getPluginManager().registerEvents(new ItemInteractListener(this), this);
        
        // 启动配置文件监听任务 (每 40 ticks 延迟启动，每 20 ticks 检查一次)
        new ConfigWatcher(this).runTaskTimer(this, 40L, 20L);
        
        getLogger().info("ItemProp 插件已加载！");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
