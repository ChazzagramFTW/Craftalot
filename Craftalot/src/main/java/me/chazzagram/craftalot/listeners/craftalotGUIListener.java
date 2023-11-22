package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.files.CraftlistConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class craftalotGUIListener implements Listener {
    Inventory guiCraftlist = Bukkit.createInventory(null, 36, "§eCraftlist GUI");
    Inventory guiSettings = Bukkit.createInventory(null, 36, "§9Settings GUI");

    ItemStack itemSelected;
    private final Craftalot plugin;

    public craftalotGUIListener(Craftalot plugin){
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
                    itemSelected = CraftlistConfig.get().getItemStack("craftlist.item" + i);
                    if(itemSelected != null){
                        guiCraftlist.setItem(i, itemSelected);
                    }

                }
                p.openInventory(guiCraftlist);


            } else if (e.getCurrentItem().getType().equals(Material.CRAFTING_TABLE)) {
                p.sendMessage("§7Loading interface..");
                p.closeInventory();

                ItemStack[] menuItems = getMenuItems();
                guiSettings.setItem(10, menuItems[0]);
                guiSettings.setItem(11, menuItems[1]);
                guiSettings.setItem(12, menuItems[2]);
                p.openInventory(guiSettings);
            }

        }

        if(e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")){
            CraftlistConfig.get().set("craftlist.item" + e.getSlot(), guiCraftlist.getItem(e.getSlot()));
            CraftlistConfig.save();
        }
    }

    private static ItemStack[] getMenuItems() {
        ItemStack time_limit = new ItemStack(Material.CLOCK);
        ItemStack day_night = new ItemStack(Material.CLOCK);
        ItemStack player_visibility = new ItemStack(Material.ENDER_EYE);

        ItemMeta time_limit_meta = time_limit.getItemMeta();
        time_limit_meta.setDisplayName("§eTime Limit");
        ArrayList<String> time_limit_lore = new ArrayList<>();
        time_limit_lore.add("§fSet how long the game will last!");
        time_limit_meta.setLore(time_limit_lore);
        time_limit.setItemMeta(time_limit_meta);

        ItemMeta day_night_meta = day_night.getItemMeta();
        day_night_meta.setDisplayName("§eTime of Day");
        ArrayList<String> day_night_lore = new ArrayList<>();
        day_night_lore.add("§fSet the time of day.");
        day_night_meta.setLore(day_night_lore);
        day_night.setItemMeta(day_night_meta);

        ItemMeta player_visibility_meta = player_visibility.getItemMeta();
        player_visibility_meta.setDisplayName("§ePlayer Visibility");
        ArrayList<String> player_visibility_lore = new ArrayList<>();
        player_visibility_lore.add("§fDecide if players can see eachother.");
        player_visibility_meta.setLore(player_visibility_lore);
        player_visibility.setItemMeta(player_visibility_meta);

        return new ItemStack[]{time_limit, day_night, player_visibility};
    }

    @EventHandler
    public void invEvent(InventoryInteractEvent e){

        if(e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")){
            for (int i = 0; i <= 32; i++) {
                CraftlistConfig.get().set("craftlist.item" + i, guiCraftlist.getItem(i));
                CraftlistConfig.save();
            }
        }
    }

   @EventHandler
    public void invEvent(InventoryCloseEvent e){

        if(e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")){
            for (int i = 0; i <= 32; i++) {
                CraftlistConfig.get().set("craftlist.item" + i, guiCraftlist.getItem(i));
                CraftlistConfig.save();
            }
        }
    }


}
