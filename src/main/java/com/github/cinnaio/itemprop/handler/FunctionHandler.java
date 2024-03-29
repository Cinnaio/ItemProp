package com.github.cinnaio.itemprop.handler;

import com.github.cinnaio.itemprop.ItemProp;
import org.bukkit.Effect;
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

import java.util.ArrayList;
import java.util.HashMap;

public class FunctionHandler {
    private static final JavaPlugin ins = ItemProp.getInstance();

    public static void addCustomTag(Player p, String n) {
        ItemStack itemStack = p.getInventory().getItemInMainHand();
        ItemMeta itemMeta = itemStack.getItemMeta();

        NamespacedKey key = new NamespacedKey(ins, n);
        itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, n);
        itemStack.setItemMeta(itemMeta);

        p.getInventory().setItemInMainHand(itemStack);
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

    public static void addMaterial(Material material, Player p, int amount) {
        ItemStack itemStack = new ItemStack(material, amount);

        p.getInventory().addItem(itemStack);
    }

    public static void giveEffects(ArrayList<String> effects, Player p) {
        for (String effect : effects) {
            String[] parts = effect.split("#");
            if (parts.length == 2) {
                String effectName = parts[0];
                int effectLevel = Integer.parseInt(parts[1]);

                // 将效果应用到玩家
                applyEffect(effectName, effectLevel, p);
            } else {
                // 如果格式不正确，忽略此效果
                System.out.println("Invalid effect format: " + effect);
            }
        }
    }

    public static void applyEffect(String effectName, int effectLevel, Player p) {
        // 根据效果名称获取 PotionEffectType
        PotionEffectType effectType = PotionEffectType.getByName(effectName.toUpperCase());
        if (effectType != null) {
            // 创建 PotionEffect 并应用到玩家
            PotionEffect effect = new PotionEffect(effectType, Integer.MAX_VALUE, effectLevel - 1);
            p.addPotionEffect(effect);
        } else {
            System.out.println("Unknown effect type: " + effectName);
        }
    }
}
