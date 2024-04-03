package com.github.cinnaio.itemprop.handler;

import com.github.cinnaio.itemprop.ItemProp;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static com.github.cinnaio.itemprop.handler.i18Handler.*;
import static com.github.cinnaio.itemprop.utils.MessageUtils.sendMessage;

public class FunctionHandler {
    private static final JavaPlugin ins = ItemProp.getInstance();

    public static void addCustomTag(Player p, String n1, String n2) {
        ItemStack itemStack = p.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();

        NamespacedKey key = new NamespacedKey(ins, n2);
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, n2);
        itemStack.setItemMeta(itemMeta);

        p.getInventory().setItemInMainHand(itemStack);

        ItemProp.getFileHandler().addTagToFile(n1, n2, p);
    }

    public static boolean ckeckCustomTag(PlayerInteractEvent e) {
        HashMap<String, Object> map = ItemProp.getFileHandler().getMap();

        for (String tags : map.keySet()) {
            if (!tags.startsWith("tags.")) {
                continue;
            }

            Object value = map.get(tags);

            if (value instanceof ArrayList) {
                ArrayList<String> arrayList = (ArrayList<String>) value;

                for (String item : arrayList) {
                    if (e.getItem() == null)
                        continue;

                    if (e.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(ins, item), PersistentDataType.STRING)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void modifyAmount(ItemStack itemStack, Player p, int amt) {
        int amount = itemStack.getAmount();

        p.getInventory().getItemInMainHand().setAmount(amount + amt);
    }

    public static void modifyMoney(int money, Player p) {
        Economy economy = ItemProp.getEconomy();

        if (economy != null)
            economy.withdrawPlayer(p, money);
    }

    public static void addMaterial(Material material, Player p, int amount) {
        ItemStack itemStack = new ItemStack(material, amount);

        p.getInventory().addItem(itemStack);
    }

    public static void executeEffects(ArrayList<?> effects, Player p) {
        for (Object effect : effects) {
            String[] parts = effect.toString().split("#");

            if (parts.length == 3) {
                String effectName = parts[0];
                int effectLevel = Integer.parseInt(parts[1]);
                int effectTime = Integer.parseInt(parts[2]);

                applyEffect(effectName, effectLevel, p, effectTime);
            } else {
                sendMessage(p, (effect_format + inferiorColor).replace("{0}", "&n" + effect));
            }
        }
    }

    public static void applyEffect(String effectName, int effectLevel, Player p, int effectTime) {
        PotionEffectType effectType = PotionEffectType.getByName(effectName.toUpperCase());

        if (effectType != null) {
            PotionEffect effect = new PotionEffect(effectType, effectTime * 20, effectLevel - 1);
            p.addPotionEffect(effect);
        } else {
            sendMessage(p, (effect_type + inferiorColor).replace("{0}", "&n" + effectName));
        }
    }

    public static void executeCommand(ArrayList<String> expression, Player p) {
        for (String expr : expression) {
            String[] parts = expr.split("-as:");

            if (parts.length == 2) {
                String command = parts[0];
                String executionMode = parts[1].toLowerCase();

                if (command.contains("%player%")) {
                    command = command.replace("%player%", p.getName());
                }

                switch (executionMode) {
                    case "op": {
                        if (!p.isOp()) {
                            p.setOp(true);
                            p.performCommand(command);
                            p.setOp(false);
                        } else {
                            p.performCommand(command);
                        }

                        break;
                    }
                    case "console": {
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
                        break;
                    }
                    case "player": {
                        p.performCommand(command);
                        break;
                    }
                    default:
                        sendMessage(p, (command_sender + inferiorColor).replace("{0}", "&n" + executionMode));
                        break;
                }
            } else {
                sendMessage(p, (expression_format + inferiorColor).replace("{0}", "&n" + expr));
            }
        }
    }

    public static HashMap<String, Object> getTag(String tag) {
        HashMap<String, Object> map = ItemProp.getFileHandler().getMap();
        HashMap<String, Object> mapSpecialTag = new HashMap<>();

        for (String tags : map.keySet()) {
            if (tags.startsWith("tags.")) {
                continue;
            }

            String[] tagg = tags.split("\\.");
            if (!(tagg.length >= 3 && tagg[2].equals(tag)))
                continue;

            mapSpecialTag.put(tagg[3], map.get(tags));
        }
        return mapSpecialTag;
    }

    public static String getItemTag(@NotNull Set<NamespacedKey> namespacedKey) {
        for (Object object : namespacedKey) {
            String[] strings = object.toString().split(":");

            if (strings[0].equals("itemprop"))
                return strings[1];
        }

        return null;
    }
}
