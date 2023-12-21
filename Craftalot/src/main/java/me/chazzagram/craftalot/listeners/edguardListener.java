package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.commands.craftalotCommand;
import me.chazzagram.craftalot.files.CraftlistConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class edguardListener implements Listener {

    public HashMap<UUID, Material> playerCraft;

    private final Craftalot plugin;

    craftalotGUIListener guiListener;

    public edguardListener(Craftalot plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void edguardInteractListener(PlayerInteractEntityEvent e){
        boolean gameRunning = guiListener.isGameRunning();
        // Edguard item check code
        Player p = e.getPlayer();
        if(gameRunning) {
            if (e.getRightClicked() == craftalotCommand.edguard) {
                for (ItemStack item : p.getInventory().getContents()) {
                    if (item.isSimilar(CraftlistConfig.get().getItemStack("craftlist.item1"))) {
                        p.sendMessage("Item found.");
                    }
                }
            }
        }

    }
}
