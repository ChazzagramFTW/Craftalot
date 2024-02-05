package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class wandListener implements Listener {

    private final Craftalot plugin;

    public wandListener(Craftalot plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void wandClickEvent(PlayerInteractEvent e){
        Player p = (Player) e.getPlayer();
        if (plugin.wandSystem.containsKey(p.getUniqueId())) {
            if (e.getHand().equals(EquipmentSlot.HAND) && p.getInventory().getItemInMainHand().equals(getWand())) {
                switch (e.getAction()) {
                    case LEFT_CLICK_BLOCK:
                        plugin.wandSystem.get(p.getUniqueId()).setCorner1(e.getClickedBlock().getLocation());
                        plugin.messagePlayer(p, "§aSet first location §e(" + e.getClickedBlock().getLocation().getBlockX() + " " + e.getClickedBlock().getLocation().getBlockY() + " " + e.getClickedBlock().getLocation().getBlockZ() + ", in " + e.getClickedBlock().getLocation().getWorld().getName() + ")");
                        e.setCancelled(true);
                        break;
                    case RIGHT_CLICK_BLOCK:
                        plugin.wandSystem.get(p.getUniqueId()).setCorner2(e.getClickedBlock().getLocation());
                        plugin.messagePlayer(p, "§aSet second location §e(" + e.getClickedBlock().getLocation().getBlockX() + " " + e.getClickedBlock().getLocation().getBlockY() + " " + e.getClickedBlock().getLocation().getBlockZ() + ", in " + e.getClickedBlock().getLocation().getWorld().getName() + ")");
                        break;
                }
            }
        }
    }

    public static ItemStack getWand(){
        ItemStack wand = new ItemStack(Material.STICK);

        ItemMeta wand_meta = wand.getItemMeta();
        wand_meta.setDisplayName("§e§lCraftalot Wand");
        ArrayList<String> wand_lore = new ArrayList<>();
        wand_lore.add("§fUsed for region setup.");
        wand_meta.setLore(wand_lore);
        wand.setItemMeta(wand_meta);

        return new ItemStack(wand);
    }

}
