package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.commands.craftalotCommand;
import me.chazzagram.craftalot.files.CraftlistConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import me.chazzagram.craftalot.playerInfo.playerInfo;
import me.chazzagram.craftalot.playerInfo.gameRunning;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class edguardListener implements Listener {

    public HashMap<UUID, Material> playerCraft;

    private Craftalot plugin;

    public edguardListener(Craftalot plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void edguardInteractListener(PlayerInteractEntityEvent e){

        // Edguard item check code
        Player p = e.getPlayer();
        boolean isGameRunning = gameRunning.isGameRunning();
        if(isGameRunning) {
            plugin.messagePlayer(p, "Game Running.");
            if (e.getRightClicked() == craftalotCommand.edguard) {
                plugin.messagePlayer(p, "Edguard Clicked.");
                for (ItemStack item : p.getInventory().getContents()) {
                    plugin.messagePlayer(p, "Item Checked.");
                    if (item != null && item.isSimilar(plugin.pointSystem.get(p.getUniqueId()).getItemToCraft())) {
                        p.sendMessage("Item found.");
                        break;
                    }
                }
            }
        }

    }
}
