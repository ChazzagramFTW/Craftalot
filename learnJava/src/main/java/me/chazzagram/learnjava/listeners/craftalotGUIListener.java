package me.chazzagram.learnjava.listeners;

import me.chazzagram.learnjava.LearnJava;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class craftalotGUIListener implements Listener {
    Inventory gui = Bukkit.createInventory(null, 36, "§eCraftlist GUI");

    ItemStack itemSelected;
    private final LearnJava plugin;

    public craftalotGUIListener(LearnJava plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e){

        Player p = (Player) e.getWhoClicked();

        if(e.getView().getTitle().equalsIgnoreCase("§6Craftalot GUI")){
            e.setCancelled(true);
            if (e.getCurrentItem().getType().equals(Material.PAPER)){
                p.sendMessage("§7Loading interface..");
                p.closeInventory();
                for (int i = 0; i <= 32; i++){
                    itemSelected = plugin.getConfig().getItemStack("craftalot.craftlist.item" + i);
                    if(itemSelected != null){
                        gui.setItem(i, itemSelected);
                    }

                }
                p.openInventory(gui);


            }

        }

        if(e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")){
            plugin.getConfig().set("craftalot.craftlist.item" + e.getSlot(), gui.getItem(e.getSlot()));
            plugin.saveConfig();
        }
    }

    @EventHandler
    public void invEvent(InventoryInteractEvent e){

        if(e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")){
            for (int i = 0; i <= 32; i++) {
                plugin.getConfig().set("craftalot.craftlist.item" + i, gui.getItem(i));
                plugin.saveConfig();
            }
        }
    }

   @EventHandler
    public void invEvent(InventoryCloseEvent e){

        if(e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")){
            for (int i = 0; i <= 32; i++) {
                plugin.getConfig().set("craftalot.craftlist.item" + i, gui.getItem(i));
                plugin.saveConfig();
            }
        }
    }


}
