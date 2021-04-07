package me.darkolythe.durabilityalert;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DurabilityListener implements Listener {

    private DurabilityAlert main;
    DurabilityListener(DurabilityAlert plugin) {
        main = plugin;
    }

    @EventHandler
    private void onItemDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        if (player.hasPermission("durabilityalert.alert")) {
            String type = item.getType().toString().toLowerCase();
            List<Integer> data = main.getPlayerData(event.getPlayer());

            boolean isDamaged = false;
            int percent = 0;

            if (type.contains("helmet") || type.contains("chestplate") || type.contains("leggings") || type.contains("boots") || type.contains("elytra")) {
                percent = data.get(1);
                isDamaged = true;
            } else if (type.contains("pickaxe") || type.contains("axe") || type.contains("shovel") || type.contains("sword") || type.contains("hoe") || type.contains("fishing") || type.contains("shears") || type.contains("shield")) {
                percent = data.get(2);
                isDamaged = true;
            }

            if (item.getEnchantments().size() == 0 && data.get(4) == 1) {
                return;
            }
            System.out.println(isDamaged);
            System.out.println(type);
            System.out.println(data.get(0));
            if (isDamaged && data.get(0) == 1) {
                float toolPercent = (((float) (item.getType().getMaxDurability() - item.getDurability()) / ((float) (item.getType().getMaxDurability())) * 100));
                int toolLeft = (item.getType().getMaxDurability() - item.getDurability());
                System.out.println(item.getDurability());
                if ((data.get(3) == 0 && (toolPercent) <= percent) || (data.get(3) == 1 && (toolLeft <= percent))) {
                    if (!type.contains("shears") && !type.contains("shield") && !type.contains("elytra")) {
                        sendWarning(player, WordUtils.capitalize(item.getType().toString().split("_")[1]), item.getType().getMaxDurability() - item.getDurability() - 1);
                    } else {
                        sendWarning(player, WordUtils.capitalize(item.getType().toString()), item.getType().getMaxDurability() - item.getDurability() - 1);
                    }
                }
            }
        }
    }

    private void sendWarning(Player player, String item, int durability) {
        String subtitle = "";
        if (durability <= 10) { //if the item durability is less than ten, warn the player with remaining durability
            subtitle = ChatColor.GRAY.toString() + ChatColor.BOLD.toString()
                    + main.confighandler.durabilityleft.replaceAll("%durability%", ChatColor.RED.toString() + ChatColor.BOLD.toString() + durability);
            player.playSound(player.getLocation(), Sound.NOTE_SNARE_DRUM, 1, 1);
        } else {
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
        }

        player.sendTitle(ChatColor.RED + main.confighandler.lowdurability.replaceAll("%item%", WordUtils.capitalize(item.toLowerCase())), subtitle);
    }
}
