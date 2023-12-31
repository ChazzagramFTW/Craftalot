package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.commands.craftalotCommand;
import me.chazzagram.craftalot.files.CraftlistConfig;
import me.chazzagram.craftalot.playerInfo.playerInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import me.chazzagram.craftalot.playerInfo.gameRunning;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.w3c.dom.Text;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;

import static me.chazzagram.craftalot.commands.craftalotCommand.edguard;

public class craftalotGUIListener implements Listener {
    private final Inventory guiCraftlist;
    private final Inventory guiSettings;
    private final Inventory guiGameControl;

    private Player settingsUser;
    private String selectedLocation;
    private String currentSetting;
    private final Craftalot plugin;

    public craftalotGUIListener(Craftalot plugin) {
        this.plugin = plugin;
        this.guiCraftlist = Bukkit.createInventory(null, 36, "§eCraftlist GUI");
        this.guiSettings = Bukkit.createInventory(null, 36, "§9Settings GUI");
        this.guiGameControl = Bukkit.createInventory(null, 36, "§9Game Control GUI");
        this.selectedLocation = "x";
        this.currentSetting = "";


    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        if (e.getView().getTitle().equalsIgnoreCase("§6Craftalot GUI")) {
            e.setCancelled(true);
            ItemStack[] menuItems = getMenuItems();
            switch(e.getSlot()){
                case 12:
                    p.sendMessage("§7Loading interface..");
                    p.closeInventory();
                    for (int i = 0; i <= 32; i++) {
                        ItemStack itemSelected = CraftlistConfig.get().getItemStack("craftlist.item" + i);
                        if (itemSelected != null) {
                            guiCraftlist.setItem(i, itemSelected);
                        }

                    }
                    guiCraftlist.setItem(35, menuItems[10]);
                    p.openInventory(guiCraftlist);
                    break;
                case 13:
                    p.sendMessage("§7Loading interface..");
                    p.closeInventory();

                    guiGameControl.setItem(27, menuItems[12]);
                    guiGameControl.setItem(35, menuItems[10]);

                    p.openInventory(guiGameControl);
                    break;
                case 14:
                    p.sendMessage("§7Loading interface..");
                    p.closeInventory();

                    guiSettings.setItem(10, menuItems[0]);
                    guiSettings.setItem(11, menuItems[1]);
                    guiSettings.setItem(12, menuItems[2]);
                    guiSettings.setItem(14, menuItems[7]);
                    guiSettings.setItem(15, menuItems[8]);
                    guiSettings.setItem(16, menuItems[9]);
                    guiSettings.setItem(31, menuItems[10]);
                    switch (plugin.getConfig().getString("craftalot.time-of-day")) {
                        case "day":
                            guiSettings.setItem(20, menuItems[3]);
                            break;
                        case "night":
                            guiSettings.setItem(20, menuItems[4]);
                            break;
                        default:
                            plugin.messagePlayer(p, "Invalid configuration for 'time of day', toggle the setting to fix.");
                            break;
                    }
                    switch (plugin.getConfig().getString("craftalot.player-visibility")) {
                        case "true":
                            guiSettings.setItem(21, menuItems[5]);
                            break;
                        case "false":
                            guiSettings.setItem(21, menuItems[6]);
                            break;
                        default:
                            plugin.messagePlayer(p, "Invalid configuration for 'player visibility', toggle the setting to fix.");
                            break;
                    }
                    p.openInventory(guiSettings);
                    break;
            }

        } else if (e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")) {
            if(e.getSlot() == 35) {
                p.closeInventory();
                for (int i = 0; i <= 32; i++) {
                    CraftlistConfig.get().set("craftlist.item" + i, guiCraftlist.getItem(i));
                    CraftlistConfig.save();
                }
                p.openInventory(craftalotCommand.gui);
                e.setCancelled(true);
            } else {
                ItemStack item = guiCraftlist.getItem(e.getSlot());
                CraftlistConfig.get().set("craftlist.item" + e.getSlot(), item);
                CraftlistConfig.save();
            }
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
                        plugin.messagePlayer(p, "Configuration was invalid, corrected to default value.");
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
                        plugin.messagePlayer(p, "Configuration was invalid, corrected to default value.");
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
                        plugin.messagePlayer(p, "§aPlease enter the X coordinate:");
                    } else if (e.getClick().isRightClick()) {
                        plugin.getConfig().set("craftalot.lobby-location", p.getLocation());
                        p.closeInventory();
                        plugin.messagePlayer(p, "§aLobby Location set to §fcurrent position.");
                    }
                    break;
                case 15:
                    if (e.getClick().isLeftClick()) {
                        currentSetting = "craftalot.game-begin-location";
                        selectedLocation = "x";
                        settingsUser = p;
                        p.closeInventory();
                        plugin.messagePlayer(p, "§aPlease enter the X coordinate:");
                    } else if (e.getClick().isRightClick()) {
                        plugin.getConfig().set("craftalot.game-begin-location", p.getLocation());
                        p.closeInventory();
                        plugin.messagePlayer(p, "§aGame Start Location set to §fcurrent position.");
                    }
                    break;
                case 16:
                    if (e.getClick().isLeftClick()) {
                        currentSetting = "craftalot.edguard-location";
                        selectedLocation = "x";
                        settingsUser = p;
                        p.closeInventory();
                        plugin.messagePlayer(p, "§aPlease enter the X coordinate:");
                    } else if (e.getClick().isRightClick()) {
                        if (craftalotCommand.schedule) {
                            plugin.getConfig().set("craftalot.edguard-location", p.getLocation());
                            plugin.saveConfig();
                            edguard.teleport(p.getLocation());
                            plugin.messagePlayer(p, "§aEdguard has been teleported to your §fcurrent position.");
                        } else {
                            plugin.messagePlayer(p, "§aEdguard does not currently exist in the world! Use '/ca edguard spawn' to summon him to your location!");
                        }
                    }
                    break;
                case 31:
                    p.closeInventory();
                    p.openInventory(craftalotCommand.gui);
                    break;
            }
        } else if (e.getView().getTitle().equalsIgnoreCase("§9Game Control GUI")) {
            e.setCancelled(true);
            switch (e.getSlot()) {
                case 27:
                    ItemStack[] menuItems = getMenuItems();
                    if(e.getCurrentItem().equals(menuItems[12])){
                        guiGameControl.setItem(27, menuItems[11]);

                        ArrayList<Player> onlinePlayers = new ArrayList<>(p.getServer().getOnlinePlayers());
                        boolean blacklisted = false;
                        for (Player player : onlinePlayers) {
                            for (String name : plugin.getConfig().getStringList("craftalot.blacklisted-players")) {
                                if (player.getName().equals(name)) {
                                    blacklisted = true;
                                }
                            }
                            if (!blacklisted) {
                                plugin.pointSystem.put(player.getUniqueId(), new playerInfo("noTeam", 0, randomItem()));
                                plugin.messagePlayer(p, player.getName() + " has been added to team: " + plugin.pointSystem.get(player.getUniqueId()).getTeamName() + " and has " + plugin.pointSystem.get(player.getUniqueId()).getPoints() + " points, and has to craft item: " + plugin.pointSystem.get(player.getUniqueId()).getItemToCraft().getType());
                            }
                            blacklisted = false;
                        }
                    }
                    gameRunning.setGameRunning(true);

                    new gameRunning(true, plugin.getConfig().getInt("craftalot.time-limit-in-seconds"), plugin) {

                        @Override
                        public void count(int current) {
                            if(isGamePaused()){
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§lPAUSED: " + LocalTime.of(0, current / 60, current % 60).format(DateTimeFormatter.ofPattern("mm:ss"))));
                            } else {
                                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§e§lTime Left: §f§l" + LocalTime.of(0, current / 60, current % 60).format(DateTimeFormatter.ofPattern("mm:ss"))));
                            }
                        }

                    }.startTimer();

                    break;
                case 35:
                    p.closeInventory();
                    p.openInventory(craftalotCommand.gui);
                    e.setCancelled(true);
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
        ItemStack edguardloc = new ItemStack(Material.COMPASS);
        ItemStack goback = new ItemStack(Material.ARROW);

        ItemStack gameRunning = new ItemStack(Material.FILLED_MAP);
        ItemStack gameWaiting = new ItemStack(Material.MAP);

        // Settings
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

        ItemMeta edguardloc_meta = edguardloc.getItemMeta();
        edguardloc_meta.setDisplayName("§7Edguard Location");
        ArrayList<String> edguardloc_lore = new ArrayList<>();
        edguardloc_lore.add("§fLeft Click to manually set position.");
        edguardloc_lore.add("§fRight Click to set to current position.");
        edguardloc_meta.setLore(edguardloc_lore);
        edguardloc.setItemMeta(edguardloc_meta);

        ItemMeta goback_meta = goback.getItemMeta();
        goback_meta.setDisplayName("§e§oGo Back");
        ArrayList<String> goback_lore = new ArrayList<>();
        goback_lore.add("§fReturn to previous interface.");
        goback_meta.setLore(goback_lore);
        goback.setItemMeta(goback_meta);

        // Game Control
        ItemMeta gameRunning_meta = gameRunning.getItemMeta();
        gameRunning_meta.setDisplayName("§7§oGame Running.");
        ArrayList<String> gameRunning_lore = new ArrayList<>();
        gameRunning_lore.add("§fUse pause/stop to change this.");
        gameRunning_meta.setLore(gameRunning_lore);
        gameRunning.setItemMeta(gameRunning_meta);

        ItemMeta gameWaiting_meta = gameWaiting.getItemMeta();
        gameWaiting_meta.setDisplayName("§e§oStart Game");
        ArrayList<String> gameWaiting_lore = new ArrayList<>();
        gameWaiting_lore.add("§fPlayer's will be teleported to lobby.");
        gameWaiting_meta.setLore(gameWaiting_lore);
        gameWaiting.setItemMeta(gameWaiting_meta);

        return new ItemStack[]{time_limit, day_night, player_visibility, day, night, valid, invalid, lobbyloc, gamebeginloc, edguardloc, goback, gameRunning, gameWaiting};
    }

    public ItemStack randomItem(){
        Random rand = new Random();
        int slot;
        do {
            slot = rand.nextInt(32);
        } while (CraftlistConfig.get().getItemStack("craftlist.item" + slot) == null);
        return CraftlistConfig.get().getItemStack("craftlist.item" + slot);
    }

    @EventHandler
    public void invEvent(InventoryInteractEvent e) {

        if (e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")) {
            for (int i = 0; i <= 32; i++) {
                CraftlistConfig.get().set("craftlist.item" + i, guiCraftlist.getItem(i));
                CraftlistConfig.save();
            }
        }
    }

    @EventHandler
    public void invEvent(InventoryCloseEvent e) {

        if (e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")) {
            for (int i = 0; i <= 32; i++) {
                CraftlistConfig.get().set("craftlist.item" + i, guiCraftlist.getItem(i));
                CraftlistConfig.save();
            }
        }
    }

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        if (p == settingsUser) {
            switch (selectedLocation) {
                case "x":
                    plugin.getConfig().set(currentSetting + ".x", message);
                    plugin.messagePlayer(p, "§a✔");
                    plugin.messagePlayer(p, "§ePlease enter the Y coordinate:");
                    selectedLocation = "y";
                    break;
                case "y":
                    plugin.getConfig().set(currentSetting + ".y", message);
                    plugin.messagePlayer(p, "§a✔");
                    plugin.messagePlayer(p, "§ePlease enter the Z coordinate:");
                    selectedLocation = "z";
                    break;
                case "z":
                    plugin.getConfig().set(currentSetting + ".z", message);
                    plugin.messagePlayer(p, "§a✔");
                    plugin.messagePlayer(p, "§ePlease enter the world name:");
                    selectedLocation = "world";
                    break;
                case "world":
                    plugin.getConfig().set(currentSetting + ".world", message);
                    plugin.messagePlayer(p, "§a✔");
                    plugin.messagePlayer(p, "§ePlease enter the pitch direction.");
                    selectedLocation = "pitch";
                    break;
                case "pitch":
                    plugin.getConfig().set(currentSetting + ".pitch", message);
                    plugin.messagePlayer(p, "§a✔");
                    plugin.messagePlayer(p, "§ePlease enter the yaw direction.");
                    selectedLocation = "yaw";
                    break;
                case "yaw":
                    plugin.getConfig().set(currentSetting + ".yaw", message);
                    plugin.messagePlayer(p, "§a✔");
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        plugin.saveConfig();
                        p.openInventory(guiSettings);
                    });
                    selectedLocation = "x";
                    settingsUser = null;
                    break;
            }
            e.setCancelled(true);
        }
    }
}

