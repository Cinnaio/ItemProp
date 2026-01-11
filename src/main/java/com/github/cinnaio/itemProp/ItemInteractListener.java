package com.github.cinnaio.itemProp;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemInteractListener implements Listener {

    private final Plugin plugin;
    // 绑定 ID 的 Key，对应 /itemprop bind <id>
    private static final String ITEM_ID_KEY = "item_id";

    public ItemInteractListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 只响应右键操作
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        Player player = event.getPlayer();
        
        // 检查 Config 绑定的 ID
        NamespacedKey idKey = new NamespacedKey(plugin, ITEM_ID_KEY);
        if (container.has(idKey, PersistentDataType.STRING)) {
            String itemId = container.get(idKey, PersistentDataType.STRING);
            String path = "items." + itemId;
            
            if (plugin.getConfig().contains(path)) {
                List<String> commands = plugin.getConfig().getStringList(path + ".commands");
                boolean consume = plugin.getConfig().getBoolean(path + ".consume", false);

                // 执行命令列表
                for (String cmd : commands) {
                    processAction(player, cmd);
                }

                // 处理物品消耗
                if (consume && player.getGameMode() != GameMode.CREATIVE) {
                    item.setAmount(item.getAmount() - 1);
                }
            }
        }
    }

    private void processAction(Player player, String actionLine) {
        if (actionLine == null || actionLine.isEmpty()) return;

        // 替换占位符
        String finalAction = actionLine.replace("%player%", player.getName());
        
        // 解析颜色 (支持 & 和 &#RRGGBB)
        finalAction = ColorUtil.color(finalAction);

        // 检查 [title]
        if (finalAction.toLowerCase().startsWith("[title]")) {
            String content = finalAction.substring(7).trim();
            String title = content;
            String subtitle = "";
            if (content.contains("|")) {
                String[] parts = content.split("\\|", 2);
                title = parts[0].trim();
                subtitle = parts[1].trim();
            }
            player.sendTitle(title, subtitle, 10, 70, 20);
            return;
        }

        // 检查 [message]
        if (finalAction.toLowerCase().startsWith("[message]")) {
            String content = finalAction.substring(9).trim();
            player.sendMessage(content);
            return;
        }
        
        // 检查 [potion]
        if (finalAction.toLowerCase().startsWith("[potion]")) {
            String content = finalAction.substring(8).trim();
            String[] parts = content.split(" ");
            if (parts.length >= 2) {
                try {
                    String effectName = parts[0].toUpperCase();
                    int duration = Integer.parseInt(parts[1]) * 20; // 转换为 ticks
                    int amplifier = 0;
                    if (parts.length >= 3) {
                        amplifier = Math.max(0, Integer.parseInt(parts[2]) - 1); // 转换为 amplifier (0 = level 1)
                    }
                    
                    PotionEffectType type = PotionEffectType.getByName(effectName);
                    if (type != null) {
                        player.addPotionEffect(new PotionEffect(type, duration, amplifier));
                    } else {
                        plugin.getLogger().warning("未知的药水效果: " + effectName);
                    }
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("药水效果参数错误: " + content);
                }
            }
            return;
        }

        // 命令执行逻辑
        String cmd = finalAction;
        boolean isConsole = false;
        boolean isOp = false;

        // 检查执行者标签
        if (cmd.toLowerCase().startsWith("[console]")) {
            isConsole = true;
            cmd = cmd.substring(9).trim();
        } else if (cmd.toLowerCase().startsWith("[op]")) {
            isOp = true;
            cmd = cmd.substring(4).trim();
        } else if (cmd.toLowerCase().startsWith("[player]")) {
            cmd = cmd.substring(8).trim();
        } else if (cmd.toLowerCase().startsWith("console:")) { // 兼容旧格式
            isConsole = true;
            cmd = cmd.substring(8).trim();
        }

        if (isConsole) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
        } else if (isOp) {
            if (player.isOp()) {
                player.performCommand(cmd);
            } else {
                try {
                    player.setOp(true);
                    player.performCommand(cmd);
                } finally {
                    player.setOp(false);
                }
            }
        } else {
            player.performCommand(cmd);
        }
    }
}
