package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.commands.craftalotCommand;
import me.chazzagram.craftalot.files.BlacklistConfig;
import me.chazzagram.craftalot.files.CraftlistConfig;
import me.chazzagram.craftalot.files.KitConfig;
import org.bukkit.Bukkit;
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
import java.util.*;

import static me.chazzagram.craftalot.listeners.craftalotGUIListener.convertToDisplayName;

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
            if (Objects.equals(e.getRightClicked().getCustomName(), "§aEdguard")) {
                if (plugin.pointSystem.get(p.getUniqueId()).getItemToCraft() == null){
                    plugin.pointSystem.get(p.getUniqueId()).setItemToCraft(randomItem());
                    p.sendMessage("§7[§bEdguard§7] §eHello young squire! I am Edguard and I require your assistance!");
                    p.sendMessage("§7[§bEdguard§7] §eYou are required to craft: §a§l" + convertToDisplayName(plugin.pointSystem.get(p.getUniqueId()).getItemToCraft().getType().toString()));

                } else {
                    boolean itemFound = false;
                    for (ItemStack item : p.getInventory().getContents()) {
                        if (item != null) {
                            ItemStack toCraft = plugin.pointSystem.get(p.getUniqueId()).getItemToCraft();
                            if (item.isSimilar(plugin.pointSystem.get(p.getUniqueId()).getItemToCraft()) || toCraft.getType() == item.getType()) {
                                p.getInventory().clear();
                                for (int i = 0; i <= 7; i++) {
                                    if (KitConfig.get().getItemStack("kit.item" + i) != null) {
                                        p.getInventory().setItem(i, KitConfig.get().getItemStack("kit.item" + i));
                                    }
                                }
                                plugin.pointSystem.get(p.getUniqueId()).setItemToCraft(randomItem());
                                plugin.pointSystem.get(p.getUniqueId()).setPoints(Integer.parseInt(plugin.getConfig().getString("craftalot.points.per-craft")));
                                p.sendMessage("§7[§e+" + plugin.getConfig().getString("craftalot.points.per-craft") + "pts§7] Item Crafted! Current Points: §b" + plugin.pointSystem.get(p.getUniqueId()).getPoints() + "pts");
                                p.sendMessage("§7[§bEdguard§7] §eThank you young squire! You are now required to craft: §a§l" + convertToDisplayName(plugin.pointSystem.get(p.getUniqueId()).getItemToCraft().getType().toString()));
                                itemFound = true;

                                ArrayList<UUID> onlinePlayers = new ArrayList<>(plugin.pointSystem.keySet());
                                for (UUID uuid : onlinePlayers) {
                                    Player player = Bukkit.getPlayer(uuid);
                                    if (!player.equals(p)) {
                                        plugin.messagePlayer(player, "Player  §e" + p.getName() + "  §7has crafted an item!");
                                    }
                                }
                                break;
                            }
                        }
                    }
                    if (!itemFound) {
                        p.sendMessage("§7[§bEdguard§7] §cYou don't seem to have brought me the right item.. I required: §a§l" + convertToDisplayName(plugin.pointSystem.get(p.getUniqueId()).getItemToCraft().getType().toString()));
                    }
                }
            }
        }

    }

    public ItemStack randomItem(){
        Random rand = new Random();
        int slot;
        do {
            slot = rand.nextInt(32);
        } while (CraftlistConfig.get().getItemStack("craftlist.item" + slot) == null);
        return CraftlistConfig.get().getItemStack("craftlist.item" + slot);
    }
}
