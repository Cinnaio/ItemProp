package com.github.cinnaio.itemprop.event;

import com.github.cinnaio.itemprop.ItemProp;
import com.github.cinnaio.itemprop.handler.FileHandler;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import static com.github.cinnaio.itemprop.handler.FunctionHandler.*;
import static org.bukkit.inventory.EquipmentSlot.OFF_HAND;

public class InteractEvent implements Listener {
    private final FileHandler fileHandler = ItemProp.getFileHandler();

    @EventHandler
    public void playerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack itemStack = p.getInventory().getItemInMainHand();

        if (e.getHand() == OFF_HAND)
            e.setCancelled(true);

        if (ckeckCustomTag(e) && e.getAction().isRightClick()) {
            modifyAmount(itemStack, p, -1);
            addMaterial(Material.DIAMOND, p, 1);
            giveEffects(fileHandler.getTags(fileHandler.getMap()), p);
        }
    }
}
