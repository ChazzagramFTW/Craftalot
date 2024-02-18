package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.playerInfo.gameRunning;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PauseListener implements Listener {

    private final Craftalot plugin;

    public PauseListener(Craftalot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void sneakEvent(PlayerToggleSneakEvent e){

        Player p = e.getPlayer();
        if(gameRunning.isGamePaused()){
            plugin.messagePlayer(p, "You cannot move, the game has been paused!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void changeSpectateEvent(PlayerTeleportEvent e){

        Player p = e.getPlayer();
        if(gameRunning.isGamePaused()) {
            if (e.getCause() == PlayerTeleportEvent.TeleportCause.SPECTATE) {
                plugin.messagePlayer(p, "You cannot move, the game has been paused!");
                e.setCancelled(true);
            }
        }
    }
}
