package com.github.cinnaio.itemProp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Method;
import java.util.List;

public class ItemInteractListener implements Listener {

    private final Plugin plugin;
    // 绑定 ID 的 Key，对应 /itemprop bind <id>
    private static final String ITEM_ID_KEY = "item_id";
    
    // 是否为 Folia 环境
    private final boolean isFolia;

    public ItemInteractListener(Plugin plugin) {
        this.plugin = plugin;
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        this.isFolia = folia;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // 只响应右键操作 (物理交互)
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        // 确保是主手
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        handleInteraction(event.getPlayer(), event.getItem(), null);
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        // 确保是主手
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        Entity clicked = event.getRightClicked();

        handleInteraction(player, item, clicked);
    }

    private void handleInteraction(Player player, ItemStack item, Entity clickedEntity) {
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        
        // 检查 Config 绑定的 ID
        NamespacedKey idKey = new NamespacedKey(plugin, ITEM_ID_KEY);
        if (container.has(idKey, PersistentDataType.STRING)) {
            String itemId = container.get(idKey, PersistentDataType.STRING);
            String path = "items." + itemId;
            
            if (plugin.getConfig().contains(path)) {
                List<String> commands = plugin.getConfig().getStringList(path + ".commands");
                boolean consume = plugin.getConfig().getBoolean(path + ".consume", false);
                boolean requireSuccess = plugin.getConfig().getBoolean(path + ".require_success", false);

                // 准备变量
                String targetName = "";
                if (clickedEntity instanceof Player) {
                    targetName = clickedEntity.getName();
                }

                // 执行命令列表
                boolean allSuccess = true;
                for (String cmd : commands) {
                    if (!processAction(player, cmd, targetName, requireSuccess)) {
                        allSuccess = false;
                        if (requireSuccess) {
                            break;
                        }
                    }
                }

                // 处理物品消耗
                boolean shouldConsume = !requireSuccess || allSuccess;
                
                if (consume && shouldConsume && player.getGameMode() != GameMode.CREATIVE) {
                    item.setAmount(item.getAmount() - 1);
                }
            }
        }
    }

    /**
     * 执行动作
     * @return 执行是否成功 (如果不需要检查，默认返回 true)
     */
    private boolean processAction(Player player, String actionLine, String targetName, boolean checkSuccess) {
        if (actionLine == null || actionLine.isEmpty()) return true;

        // 替换占位符
        String finalAction = actionLine.replace("%player%", player.getName());
        
        // 替换 %right_clicked_player%
        if (targetName != null && !targetName.isEmpty()) {
            finalAction = finalAction.replace("%right_clicked_player%", targetName);
        } else {
            finalAction = finalAction.replace("%right_clicked_player%", "无目标");
        }
        
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
            return true;
        }

        // 检查 [message]
        if (finalAction.toLowerCase().startsWith("[message]")) {
            String content = finalAction.substring(9).trim();
            player.sendMessage(content);
            return true;
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
                        return false;
                    }
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("药水效果参数错误: " + content);
                    return false;
                }
            }
            return true;
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
            final String consoleCmd = cmd;
            if (isFolia) {
                // Folia: 使用反射将控制台命令调度到全局线程
                try {
                    // 获取 GlobalRegionScheduler
                    Method getGlobalRegionScheduler = Bukkit.class.getMethod("getGlobalRegionScheduler");
                    Object globalScheduler = getGlobalRegionScheduler.invoke(null);

                    // 获取 execute 方法
                    // execute(Plugin plugin, Runnable task)
                    Method execute = globalScheduler.getClass().getMethod("execute", org.bukkit.plugin.Plugin.class, Runnable.class);

                    // 执行
                    execute.invoke(globalScheduler, plugin, (Runnable) () -> {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), consoleCmd);
                    });
                    
                    if (checkSuccess) {
                        plugin.getLogger().warning("Folia 环境下不支持控制台命令的同步结果检查 (require_success)，将忽略检查直接执行。");
                    }
                    return true;
                } catch (Exception e) {
                    plugin.getLogger().severe("Folia 调度控制台命令失败: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            } else {
                // Spigot/Paper: 同步执行
                if (checkSuccess) {
                    CapturedConsoleSender sender = new CapturedConsoleSender();
                    boolean success = plugin.getServer().dispatchCommand(sender, consoleCmd);
                    String output = sender.getOutput();
                    if (!success || output.trim().isEmpty()) {
                        return false;
                    }
                    return true;
                } else {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), consoleCmd);
                    return true;
                }
            }
        } else if (isOp) {
            boolean success;
            if (player.isOp()) {
                success = player.performCommand(cmd);
            } else {
                try {
                    player.setOp(true);
                    success = player.performCommand(cmd);
                } finally {
                    player.setOp(false);
                }
            }
            return success;
        } else {
            return player.performCommand(cmd);
        }
    }
}
