package com.github.cinnaio.itemprop.event;

import com.github.cinnaio.itemprop.ItemProp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

import static com.github.cinnaio.itemprop.handler.FunctionHandler.*;
import static com.github.cinnaio.itemprop.handler.i18Handler.*;
import static com.github.cinnaio.itemprop.utils.MessageUtils.sendMessage;
import static java.lang.Math.abs;
import static org.bukkit.inventory.EquipmentSlot.OFF_HAND;

public class InteractEvent implements Listener {
    @EventHandler
    public void playerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getHand() == OFF_HAND)
            e.setCancelled(true);

        if (ckeckCustomTag(e) && e.getAction().isRightClick()) {
            String namespaceKey = getItemTag(e.getItem().getItemMeta().getPersistentDataContainer().getKeys());

            if (getTag(namespaceKey).get("amount") != null && e.getItem().getAmount() >= (Integer) getTag(namespaceKey).get("amount")) {
                if (getTag(namespaceKey).get("paymoney") != null) {
                    if (ItemProp.getEconomy().getBalance(p) < (Integer) getTag(namespaceKey).get("paymoney")) {
                        e.setCancelled(true);
                        sendMessage(p, (scarcity_exp + inferiorColor).replace("{0}", "&n" + abs((Integer) getTag(namespaceKey).get("paymoney") - ItemProp.getEconomy().getBalance(p))));
                        return;
                    }
                }

                if (getTag(namespaceKey).get("payexp") != null) {
                    if (p.getTotalExperience() < (Integer) getTag(namespaceKey).get("payexp")) {
                        e.setCancelled(true);
                        sendMessage(p, (scarcity_money + inferiorColor).replace("{0}", "&n" + abs((Integer) getTag(namespaceKey).get("payexp") - p.getTotalExperience()) + "xp"));
                        return;
                    }
                }

                modifyMoney((Integer) getTag(namespaceKey).get("paymoney"), p);
                p.giveExp(-(Integer) getTag(namespaceKey).get("payexp"));
                executeEffects((ArrayList<?>) getTag(namespaceKey).get("effects"), p);
                executeCommand((ArrayList<String>) getTag(namespaceKey).get("expression"), p);
            }
        }
    }
}
