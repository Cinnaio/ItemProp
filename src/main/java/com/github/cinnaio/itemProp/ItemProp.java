package com.github.cinnaio.itemProp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.function.Consumer;

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
        
        // 启动配置文件监听任务
        // 检测是否在 Folia 环境下
        boolean isFolia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }

        ConfigWatcher watcher = new ConfigWatcher(this);
        if (isFolia) {
            // Folia: 使用反射调用全局区域调度器，避免编译时依赖问题
            try {
                // 获取 GlobalRegionScheduler
                Method getGlobalRegionScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler");
                Object globalScheduler = getGlobalRegionScheduler.invoke(null);

                // 获取 runAtFixedRate 方法
                // 方法签名: runAtFixedRate(Plugin plugin, Consumer<ScheduledTask> task, long initialDelayTicks, long periodTicks)
                Method runAtFixedRate = globalScheduler.getClass().getMethod("runAtFixedRate", org.bukkit.plugin.Plugin.class, Consumer.class, long.class, long.class);

                // 构造 Consumer<ScheduledTask>
                Consumer<Object> taskWrapper = (scheduledTask) -> watcher.run();

                // 调用方法
                runAtFixedRate.invoke(globalScheduler, this, taskWrapper, 40L, 20L);
                
                getLogger().info("已使用 Folia 全局调度器启动配置监听任务。");
            } catch (Exception e) {
                getLogger().severe("尝试在 Folia 环境下启动调度器失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Spigot/Paper: 使用传统 BukkitScheduler
            Bukkit.getScheduler().runTaskTimer(this, watcher, 40L, 20L);
        }
        
        getLogger().info("ItemProp 插件已加载！(Folia 支持: " + isFolia + ")");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
