package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.commands.craftalotCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class edguardListener implements Listener {

    private final Craftalot plugin;

    public edguardListener(Craftalot plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void edguardInteractListener(PlayerInteractEntityEvent e){
        // Edguard item check code
        Player p = e.getPlayer();
        if(craftalotGUIListener.gameRunning){
            if(e.getRightClicked() == craftalotCommand.edguard){
                plugin.messagePlayer(p, "Interacted with edguard.");
            }
        }

    }
}
