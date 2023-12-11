package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.files.CraftlistConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class craftalotGUIListener implements Listener {
    Inventory guiCraftlist = Bukkit.createInventory(null, 36, "§eCraftlist GUI");
    Inventory guiSettings = Bukkit.createInventory(null, 36, "§9Settings GUI");

    ItemStack itemSelected;
    public Player settingsUser;
    public String selectedLocation;
    public String currentSetting;
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
                guiSettings.setItem(14, menuItems[7]);
                guiSettings.setItem(15, menuItems[8]);
                switch(plugin.getConfig().getString("craftalot.time-of-day")){
                    case "day":
                        guiSettings.setItem(20, menuItems[3]);
                        break;
                    case "night":
                        guiSettings.setItem(20, menuItems[4]);
                        break;
                    default:
                        p.sendMessage("Invalid configuration for 'time of day', toggle the setting to fix.");
                        break;
                }
                switch(plugin.getConfig().getString("craftalot.player-visibility")){
                    case "true":
                        guiSettings.setItem(21, menuItems[5]);
                        break;
                    case "false":
                        guiSettings.setItem(21, menuItems[6]);
                        break;
                    default:
                        p.sendMessage("Invalid configuration for 'player visibility', toggle the setting to fix.");
                        break;
                }
                p.openInventory(guiSettings);
            }

        } else if(e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")){
            CraftlistConfig.get().set("craftlist.item" + e.getSlot(), guiCraftlist.getItem(e.getSlot()));
            CraftlistConfig.save();
        } else if (e.getView().getTitle().equalsIgnoreCase("§9Settings GUI")) {
            e.setCancelled(true);
            ItemStack[] menuItems = getMenuItems();
            switch (e.getSlot()) {
                case 10:
                    break;
                case 11:
                    if (plugin.getConfig().getString("craftalot.time-of-day").equals("day")) {
                        plugin.getConfig().set("craftalot.time-of-day", "night");
                        plugin.saveConfig();
                        guiSettings.setItem(20, menuItems[4]);

                    } else if (plugin.getConfig().getString("craftalot.time-of-day").equals("night")) {
                        plugin.getConfig().set("craftalot.time-of-day", "day");
                        plugin.saveConfig();
                        guiSettings.setItem(20, menuItems[3]);
                    } else {
                        p.sendMessage("Configuration was invalid, corrected to default value.");
                        plugin.getConfig().set("craftalot.time-of-day", "day");
                        plugin.saveConfig();
                        guiSettings.setItem(20, menuItems[3]);
                    }
                    break;
                case 12:
                    if (plugin.getConfig().getString("craftalot.player-visibility").equals("true")) {
                        plugin.getConfig().set("craftalot.player-visibility", false);
                        plugin.saveConfig();
                        guiSettings.setItem(21, menuItems[6]);

                    } else if (plugin.getConfig().getString("craftalot.player-visibility").equals("false")) {
                        plugin.getConfig().set("craftalot.player-visibility", true);
                        plugin.saveConfig();
                        guiSettings.setItem(21, menuItems[5]);
                    } else {
                        p.sendMessage("Configuration was invalid, corrected to default value.");
                        plugin.getConfig().set("craftalot.player-visibility", true);
                        plugin.saveConfig();
                        guiSettings.setItem(21, menuItems[5]);
                    }
                    break;
                case 14:
                    if (e.getClick().isLeftClick()) {
                        currentSetting = "craftalot.lobby-location";
                        selectedLocation = "x";
                        settingsUser = p;
                        p.closeInventory();
                        p.sendMessage("§aPlease enter the X coordinate:");
                    } else if (e.getClick().isRightClick()){
                        plugin.getConfig().set("craftalot.lobby-location", p.getLocation());
                        p.closeInventory();
                        p.sendMessage("§aLobby Location set to §fcurrent position.");
                    }
                    break;
                case 15:
                    if (e.getClick().isLeftClick()) {
                        currentSetting = "craftalot.game-begin-location";
                        selectedLocation = "x";
                        settingsUser = p;
                        p.closeInventory();
                        p.sendMessage("§aPlease enter the X coordinate:");
                    } else if (e.getClick().isRightClick()){
                        plugin.getConfig().set("craftalot.game-begin-location", p.getLocation());
                        p.closeInventory();
                        p.sendMessage("§aGame Start Location set to §fcurrent position.");
                    }
                    break;
            }
        }
    }

    private static ItemStack[] getMenuItems() {
        ItemStack time_limit = new ItemStack(Material.CLOCK);
        ItemStack day_night = new ItemStack(Material.CLOCK);
        ItemStack player_visibility = new ItemStack(Material.ENDER_EYE);
        ItemStack day = new ItemStack(Material.ORANGE_STAINED_GLASS_PANE);
        ItemStack night = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
        ItemStack valid = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemStack invalid = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemStack lobbyloc = new ItemStack(Material.COMPASS);
        ItemStack gamebeginloc = new ItemStack(Material.COMPASS);

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

        ItemMeta day_meta = day.getItemMeta();
        day_meta.setDisplayName("§7Current Setting:");
        ArrayList<String> day_lore = new ArrayList<>();
        day_lore.add("§6DAY");
        day_meta.setLore(day_lore);
        day.setItemMeta(day_meta);

        ItemMeta night_meta = night.getItemMeta();
        night_meta.setDisplayName("§7Current Setting:");
        ArrayList<String> night_lore = new ArrayList<>();
        night_lore.add("§9NIGHT");
        night_meta.setLore(night_lore);
        night.setItemMeta(night_meta);

        ItemMeta valid_meta = valid.getItemMeta();
        valid_meta.setDisplayName("§7Current Setting:");
        ArrayList<String> valid_lore = new ArrayList<>();
        valid_lore.add("§aVALID");
        valid_meta.setLore(valid_lore);
        valid.setItemMeta(valid_meta);

        ItemMeta invalid_meta = invalid.getItemMeta();
        invalid_meta.setDisplayName("§7Current Setting:");
        ArrayList<String> invalid_lore = new ArrayList<>();
        invalid_lore.add("§cINVALID");
        invalid_meta.setLore(invalid_lore);
        invalid.setItemMeta(invalid_meta);

        ItemMeta lobbyloc_meta = lobbyloc.getItemMeta();
        lobbyloc_meta.setDisplayName("§7Lobby Location");
        ArrayList<String> lobbyloc_lore = new ArrayList<>();
        lobbyloc_lore.add("§fLeft Click to manually set position.");
        lobbyloc_lore.add("§fRight Click to set to current position.");
        lobbyloc_meta.setLore(lobbyloc_lore);
        lobbyloc.setItemMeta(lobbyloc_meta);

        ItemMeta gamebeginloc_meta = gamebeginloc.getItemMeta();
        gamebeginloc_meta.setDisplayName("§7Game Start Location");
        ArrayList<String> gamebeginloc_lore = new ArrayList<>();
        gamebeginloc_lore.add("§fLeft Click to manually set position.");
        gamebeginloc_lore.add("§fRight Click to set to current position.");
        gamebeginloc_meta.setLore(gamebeginloc_lore);
        gamebeginloc.setItemMeta(gamebeginloc_meta);

        return new ItemStack[]{time_limit, day_night, player_visibility, day, night, valid, invalid, lobbyloc, gamebeginloc};
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

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        String message = e.getMessage();
        if(p == settingsUser){
            if(currentSetting.equals("craftalot.lobby-location")){
                switch(selectedLocation){
                    case "x":
                        plugin.getConfig().set("craftalot.lobby-location.x", message);
                        p.sendMessage("§aPlease enter the Y coordinate:");
                        selectedLocation = "y";
                        break;
                    case "y":
                        plugin.getConfig().set("craftalot.lobby-location.y", message);
                        p.sendMessage("§aPlease enter the Z coordinate:");
                        selectedLocation = "z";
                        break;
                    case "z":
                        plugin.getConfig().set("craftalot.lobby-location.z", message);
                        p.sendMessage("§aPlease enter the world name:");
                        selectedLocation = "world";
                        break;
                    case "world":
                        plugin.getConfig().set("craftalot.lobby-location.world", message);
                        p.sendMessage("§aPlease enter the pitch direction:");
                        selectedLocation = "pitch";
                        break;
                    case "pitch":
                        plugin.getConfig().set("craftalot.lobby-location.pitch", message);
                        p.sendMessage("§aPlease enter the yaw direction.");
                        selectedLocation = "yaw";
                        break;
                    case "yaw":
                        plugin.getConfig().set("craftalot.lobby-location.yaw", message);
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            plugin.saveConfig();
                            p.openInventory(guiSettings);
                        });
                        selectedLocation = "x";
                        settingsUser = null;
                        break;
                }
            } else if(currentSetting.equals("craftalot.game-begin-location")){
                switch(selectedLocation){
                    case "x":
                        plugin.getConfig().set("craftalot.game-begin-location.x", message);
                        p.sendMessage("§aPlease enter the Y coordinate:");
                        selectedLocation = "y";
                        break;
                    case "y":
                        plugin.getConfig().set("craftalot.game-begin-location.y", message);
                        p.sendMessage("§aPlease enter the Z coordinate:");
                        selectedLocation = "z";
                        break;
                    case "z":
                        plugin.getConfig().set("craftalot.game-begin-location.z", message);
                        p.sendMessage("§aPlease enter the world name:");
                        selectedLocation = "world";
                        break;
                    case "world":
                        plugin.getConfig().set("craftalot.game-begin-location.world", message);
                        p.sendMessage("§aPlease enter the pitch direction:");
                        selectedLocation = "pitch";
                        break;
                    case "pitch":
                        plugin.getConfig().set("craftalot.game-begin-location.pitch", message);
                        p.sendMessage("§aPlease enter the yaw direction.");
                        selectedLocation = "yaw";
                        break;
                    case "yaw":
                        plugin.getConfig().set("craftalot.game-begin-location.yaw", message);
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            plugin.saveConfig();
                            p.openInventory(guiSettings);
                        });
                        selectedLocation = "x";
                        settingsUser = null;
                        break;
                }
            }
            e.setCancelled(true);
        }
    }

}
