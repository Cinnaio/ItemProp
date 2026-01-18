package com.github.cinnaio.itemProp;

import org.bukkit.plugin.Plugin;
import java.io.File;

public class ConfigWatcher implements Runnable {

    private final Plugin plugin;
    private final File configFile;
    private long lastModified;

    public ConfigWatcher(Plugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.yml");
        this.lastModified = configFile.lastModified();
    }

    @Override
    public void run() {
        if (!configFile.exists()) {
            return;
        }

        long currentModified = configFile.lastModified();
        
        // 如果文件被修改 (且不是初始化时的状态)
        if (currentModified > lastModified) {
            lastModified = currentModified;
            
            // 重新加载配置
            plugin.reloadConfig();
            plugin.getLogger().info("检测到配置文件变化，已自动重载 config.yml");
        } else if (currentModified < lastModified) {
            // 处理特殊情况：文件可能被还原或替换，更新时间戳以避免逻辑错误
            lastModified = currentModified;
        }
    }
}
