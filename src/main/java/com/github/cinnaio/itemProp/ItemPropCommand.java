package com.github.cinnaio.itemProp;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemPropCommand implements CommandExecutor, TabCompleter {

    private final Plugin plugin;
    private static final String ITEM_ID_KEY = "item_id";

    public ItemPropCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家可以使用此命令。");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (args.length < 1) {
            return false;
        }

        String subCommand = args[0].toLowerCase();

        // 某些命令不需要手持物品
        if (subCommand.equals("reload")) {
            if (!sender.hasPermission("itemprop.admin")) {
                sender.sendMessage(ChatColor.RED + "你没有权限执行此命令。");
                return true;
            }
            plugin.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "配置文件已重载。");
            return true;
        }

        // 其他命令需要手持物品
        if (item.getType().isAir()) {
            player.sendMessage(ChatColor.RED + "你需要手持一个物品。");
            return true;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
             player.sendMessage(ChatColor.RED + "无法获取物品元数据。");
             return true;
        }
        PersistentDataContainer container = meta.getPersistentDataContainer();

        if (subCommand.equals("bind")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "用法: /itemprop bind <config_id>");
                return true;
            }
            String configId = args[1];
            
            // 验证 configId 是否存在
            if (!plugin.getConfig().contains("items." + configId)) {
                player.sendMessage(ChatColor.RED + "警告: 配置文件中未找到 ID 为 '" + configId + "' 的配置项。但绑定仍会继续。");
            }

            NamespacedKey key = new NamespacedKey(plugin, ITEM_ID_KEY);
            container.set(key, PersistentDataType.STRING, configId);
            item.setItemMeta(meta);
            player.sendMessage(ChatColor.GREEN + "已绑定物品 ID: " + configId);

        } else if (subCommand.equals("name")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "用法: /itemprop name <name>");
                return true;
            }
            
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                nameBuilder.append(args[i]).append(" ");
            }
            String name = nameBuilder.toString().trim();
            
            meta.setDisplayName(ColorUtil.color(name));
            item.setItemMeta(meta);
            player.sendMessage(ChatColor.GREEN + "已更新物品名称。");

        } else if (subCommand.equals("lore")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "用法: /itemprop lore <add|set|remove|clear> [args]");
                return true;
            }
            String loreAction = args[1].toLowerCase();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

            if (loreAction.equals("add")) {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "用法: /itemprop lore add <text>");
                    return true;
                }
                StringBuilder textBuilder = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    textBuilder.append(args[i]).append(" ");
                }
                lore.add(ColorUtil.color(textBuilder.toString().trim()));
                player.sendMessage(ChatColor.GREEN + "已添加 Lore。");

            } else if (loreAction.equals("set")) {
                if (args.length < 4) {
                    player.sendMessage(ChatColor.RED + "用法: /itemprop lore set <line> <text>");
                    return true;
                }
                try {
                    int line = Integer.parseInt(args[2]) - 1;
                    if (line < 0 || line >= lore.size()) {
                        player.sendMessage(ChatColor.RED + "无效的行号。当前 Lore 行数: " + lore.size());
                        return true;
                    }
                    StringBuilder textBuilder = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        textBuilder.append(args[i]).append(" ");
                    }
                    lore.set(line, ColorUtil.color(textBuilder.toString().trim()));
                    player.sendMessage(ChatColor.GREEN + "已设置第 " + (line + 1) + " 行 Lore。");
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "行号必须是数字。");
                    return true;
                }

            } else if (loreAction.equals("remove")) {
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "用法: /itemprop lore remove <line>");
                    return true;
                }
                try {
                    int line = Integer.parseInt(args[2]) - 1;
                    if (line < 0 || line >= lore.size()) {
                        player.sendMessage(ChatColor.RED + "无效的行号。当前 Lore 行数: " + lore.size());
                        return true;
                    }
                    lore.remove(line);
                    player.sendMessage(ChatColor.GREEN + "已移除第 " + (line + 1) + " 行 Lore。");
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "行号必须是数字。");
                    return true;
                }

            } else if (loreAction.equals("clear")) {
                lore.clear();
                player.sendMessage(ChatColor.GREEN + "已清除所有 Lore。");
            } else {
                player.sendMessage(ChatColor.RED + "未知 Lore 操作。可用: add, set, remove, clear");
                return true;
            }

            meta.setLore(lore);
            item.setItemMeta(meta);

        } else if (subCommand.equals("check")) {
            NamespacedKey key = new NamespacedKey(plugin, ITEM_ID_KEY);
            if (container.has(key, PersistentDataType.STRING)) {
                String itemId = container.get(key, PersistentDataType.STRING);
                player.sendMessage(ChatColor.YELLOW + "=== 绑定信息 ===");
                player.sendMessage(ChatColor.GOLD + "ID: " + ChatColor.AQUA + itemId);
                
                String path = "items." + itemId;
                if (plugin.getConfig().contains(path)) {
                    boolean consume = plugin.getConfig().getBoolean(path + ".consume", false);
                    List<String> commands = plugin.getConfig().getStringList(path + ".commands");
                    
                    player.sendMessage(ChatColor.GOLD + "消耗物品: " + (consume ? ChatColor.GREEN + "是" : ChatColor.RED + "否"));
                    player.sendMessage(ChatColor.GOLD + "指令列表:");
                    if (commands.isEmpty()) {
                        player.sendMessage(ChatColor.GRAY + "  - 无指令");
                    } else {
                        for (String cmd : commands) {
                            player.sendMessage(ChatColor.GRAY + "  - " + ChatColor.WHITE + cmd);
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "注意: Config 中未找到该 ID 的配置！");
                }
            } else {
                player.sendMessage(ChatColor.YELLOW + "该物品未绑定任何 ID。");
            }

        } else if (subCommand.equals("nbt")) {
            player.sendMessage(ChatColor.YELLOW + "=== 物品自定义标签 (Namespace: " + plugin.getName().toLowerCase() + ") ===");
            boolean found = false;
            for (NamespacedKey key : container.getKeys()) {
                if (key.getNamespace().equals(plugin.getName().toLowerCase())) {
                    String val = container.get(key, PersistentDataType.STRING);
                    player.sendMessage(ChatColor.GOLD + key.getKey() + ChatColor.WHITE + ": " + ChatColor.AQUA + val);
                    found = true;
                }
            }
            if (!found) {
                player.sendMessage(ChatColor.GRAY + "没有找到相关标签。");
            }

        } else {
            player.sendMessage(ChatColor.RED + "未知子命令。可用: bind, name, lore, check, nbt, reload");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(Arrays.asList("bind", "name", "lore", "check", "nbt", "reload"), args[0]);
        }
        
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("bind")) {
                if (plugin.getConfig().contains("items")) {
                    Set<String> keys = plugin.getConfig().getConfigurationSection("items").getKeys(false);
                    return filter(new ArrayList<>(keys), args[1]);
                }
            } else if (args[0].equalsIgnoreCase("lore")) {
                return filter(Arrays.asList("add", "set", "remove", "clear"), args[1]);
            }
        }
        
        return Collections.emptyList();
    }

    private List<String> filter(List<String> list, String input) {
        String lowerInput = input.toLowerCase();
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(lowerInput))
                .collect(Collectors.toList());
    }
}
