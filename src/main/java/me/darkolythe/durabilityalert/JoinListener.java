package me.darkolythe.durabilityalert;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        PlayerSaveManager.load(event.getPlayer());
    }
    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        PlayerSaveManager.remove(event.getPlayer());
        DurabilityAlert.getInstance().removePlayerData(event.getPlayer());
    }
}
