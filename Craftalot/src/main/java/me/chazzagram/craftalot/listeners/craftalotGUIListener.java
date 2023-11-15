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

public class craftalotGUIListener implements Listener {
    Inventory gui = Bukkit.createInventory(null, 36, "§eCraftlist GUI");

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
                        gui.setItem(i, itemSelected);
                    }

                }
                p.openInventory(gui);


            }

        }

        if(e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")){
            CraftlistConfig.get().set("craftlist.item" + e.getSlot(), gui.getItem(e.getSlot()));
            CraftlistConfig.save();
        }
    }

    @EventHandler
    public void invEvent(InventoryInteractEvent e){

        if(e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")){
            for (int i = 0; i <= 32; i++) {
                CraftlistConfig.get().set("craftlist.item" + i, gui.getItem(i));
                CraftlistConfig.save();
            }
        }
    }

   @EventHandler
    public void invEvent(InventoryCloseEvent e){

        if(e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")){
            for (int i = 0; i <= 32; i++) {
                CraftlistConfig.get().set("craftlist.item" + i, gui.getItem(i));
                CraftlistConfig.save();
            }
        }
    }


}
