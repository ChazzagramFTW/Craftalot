package me.chazzagram.craftalot.listeners;

import com.google.gson.JsonArray;
import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.commands.craftalotCommand;
import me.chazzagram.craftalot.files.BlacklistConfig;
import me.chazzagram.craftalot.files.CraftlistConfig;
import me.chazzagram.craftalot.files.MaterialsConfig;
import me.chazzagram.craftalot.playerInfo.playerInfo;
import me.chazzagram.craftalot.playerInfo.settingsInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import java.util.*;
import java.util.List;

import static me.chazzagram.craftalot.commands.craftalotCommand.*;

public class craftalotGUIListener implements Listener {
    private final Inventory guiCraftlist;
    private final Inventory guiSettings;
    private final Inventory guiGameControl;

    private int task;
    private Player settingsUser;
    private String selectedLocation;
    private final Craftalot plugin;
    public HashMap<UUID, settingsInfo> currentSetting;

    public craftalotGUIListener(Craftalot plugin) {
        this.plugin = plugin;
        this.task = 0;
        this.guiCraftlist = Bukkit.createInventory(null, 36, "§eCraftlist GUI");
        this.guiSettings = Bukkit.createInventory(null, 36, "§9Settings GUI");
        this.guiGameControl = Bukkit.createInventory(null, 36, "§9Game Control GUI");
        this.selectedLocation = "x";
        this.currentSetting = new HashMap<>();


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

                    if(!gameRunning.isGameRunning()) {
                        guiGameControl.setItem(27, menuItems[12]);
                    } else { guiGameControl.setItem(27, menuItems[11]); }
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
                    currentSetting.put(p.getUniqueId(), new settingsInfo("craftalot.time-limit-in-seconds", null));
                    p.closeInventory();
                    plugin.messagePlayer(p, "§aPlease enter the time limit in seconds:");
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
                    plugin.getConfig().set("craftalot.lobby-location", p.getLocation());
                    p.closeInventory();
                    plugin.messagePlayer(p, "§aLobby Location set to §fcurrent position.");
                    plugin.saveConfig();
                    break;
                case 15:
                    plugin.getConfig().set("craftalot.game-begin-location", p.getLocation());
                    p.closeInventory();
                    plugin.messagePlayer(p, "§aGame Start Location set to §fcurrent position.");
                    plugin.saveConfig();
                    break;
                case 16:
                    if (schedule) {
                        plugin.getConfig().set("craftalot.edguard-location", p.getLocation());
                        edguard.teleport(p.getLocation());
                        plugin.messagePlayer(p, "§aEdguard has been teleported to your §fcurrent position.");
                    } else {
                        plugin.messagePlayer(p, "§aEdguard does not currently exist in the world! Use '/ca edguard spawn' to summon him to your location!");
                    }
                    plugin.saveConfig();
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
                    if (plugin.getConfig().get("craftalot.lobby-location") == null) {
                        plugin.messagePlayer(p, "Game cannot start, lobby-location has not been configured.");
                        break;
                    }
                    if (plugin.getConfig().get("craftalot.game-begin-location") == null) {
                        plugin.messagePlayer(p, "Game cannot start, game-begin-location has not been configured.");
                        break;
                    }
                    if (!schedule) {
                        plugin.messagePlayer(p, "Game cannot start, edguard has not been spawned.");
                        break;
                    }
                    ItemStack[] menuItems = getMenuItems();
                    if(e.getCurrentItem().equals(menuItems[12])){
                        guiGameControl.setItem(27, menuItems[11]);

                        ArrayList<Player> onlinePlayers = new ArrayList<>(p.getServer().getOnlinePlayers());
                        boolean blacklisted = false;
                        for (Player player : onlinePlayers) {
                            for (String uuidString : BlacklistConfig.get().getStringList("blacklisted-players")) {
                                UUID uuid = UUID.fromString(uuidString);
                                if (uuid.equals(player.getUniqueId())) {
                                    blacklisted = true;
                                }
                            }
                            if (!blacklisted) {
                                plugin.pointSystem.put(player.getUniqueId(), new playerInfo("noTeam", 0, randomItem()));
                                plugin.messagePlayer(p, player.getName() + " has been added to team: " + plugin.pointSystem.get(player.getUniqueId()).getTeamName() + " and has " + plugin.pointSystem.get(player.getUniqueId()).getPoints() + " points, and has to craft item: " + plugin.pointSystem.get(player.getUniqueId()).getItemToCraft().getType());
                            }
                        }
                        gameRunning.setGameRunning(true);
                        int timeLimit = Integer.parseInt(plugin.getConfig().getString("craftalot.time-limit-in-seconds"));
                        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                            int time = 0;
                            @Override
                            public void run() {
                                if(!gameRunning.isGameRunning()){
                                    stopCountdown();
                                }
                                time++;
                                switch (time) {
                                    case 1:
                                        List<String> blackList = BlacklistConfig.get().getStringList("blacklisted-players");
                                        for (Player player : onlinePlayers) {
                                            plugin.messagePlayer(player, "The game is starting! Teleporting to lobby..");
                                            boolean blacklisted = false;
                                            if (!plugin.getConfig().getKeys(true).isEmpty()) {
                                                for (String uuidString : blackList) {
                                                    UUID uuid = UUID.fromString(uuidString);
                                                    if (uuid.equals(player.getUniqueId())) {
                                                        plugin.messagePlayer(player, "§cYou are exempt from playing, you are on the blacklist. If this is an error contact an administrator.");
                                                        blacklisted = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (!blacklisted) {
                                                player.teleport(plugin.getConfig().getLocation("craftalot.lobby-location"));
                                            }
                                        }
                                        break;
                                    case 5:
                                        for (Player player : onlinePlayers) {
                                            plugin.messagePlayer(player, "Game will commence in 30 seconds, get ready!");
                                        }
                                        break;
                                    case 25:
                                        for (Player player : onlinePlayers) {
                                            plugin.messagePlayer(player, "Game will commence in 10 seconds..");
                                        }
                                        break;
                                    case 32:
                                        for (Player player : onlinePlayers) {
                                            plugin.messagePlayer(player, "Game starting in 3..");
                                        }
                                        break;
                                    case 33:
                                        for (Player player : onlinePlayers) {
                                            plugin.messagePlayer(player, "Game starting in 2..");
                                        }
                                        break;
                                    case 34:
                                        for (Player player : onlinePlayers) {
                                            plugin.messagePlayer(player, "Game starting in 1..");
                                        }
                                        break;
                                    case 35:
                                        guiGameControl.setItem(28, menuItems[14]);
                                        new gameRunning(true, timeLimit, plugin) {

                                            @Override
                                            public void count(int current) {
                                                if (isGamePaused()) {
                                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§lPAUSED: " + LocalTime.of(0, current / 60, current % 60).format(DateTimeFormatter.ofPattern("mm:ss"))));
                                                } else {
                                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§e§lTime Left: §f§l" + LocalTime.of(0, current / 60, current % 60).format(DateTimeFormatter.ofPattern("mm:ss"))));
                                                }
                                            }

                                        }.startTimer();
                                }
                            }
                        }, 0, 20);

                    } else {
                        gameRunning.setGameRunning(false);
                        gameRunning.setGamePaused(false);
                        plugin.messagePlayer(p, "The game has been stopped.");
                        guiGameControl.setItem(27, menuItems[12]);
                        guiGameControl.setItem(28, null);
                    }

                    break;
                case 28:
                    menuItems = getMenuItems();
                    if(gameRunning.isGameRunning()) {
                        if (!gameRunning.isGamePaused()) {
                            gameRunning.setGamePaused(true);
                            guiGameControl.setItem(28, menuItems[13]);
                            plugin.messagePlayer(p, "The game is now paused!");
                        } else {
                            gameRunning.setGamePaused(false);
                            guiGameControl.setItem(28, menuItems[14]);
                            plugin.messagePlayer(p, "The game is now resumed!");
                        }
                    }
                    e.setCancelled(true);
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
        ItemStack gamePaused = new ItemStack(Material.RED_DYE);
        ItemStack gameResumed = new ItemStack(Material.LIME_DYE);

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
        lobbyloc_lore.add("§fClick to set to current position.");
        lobbyloc_lore.add("§fStand where you wish for this position to be set.");
        lobbyloc_meta.setLore(lobbyloc_lore);
        lobbyloc.setItemMeta(lobbyloc_meta);

        ItemMeta gamebeginloc_meta = gamebeginloc.getItemMeta();
        gamebeginloc_meta.setDisplayName("§7Game Start Location");
        ArrayList<String> gamebeginloc_lore = new ArrayList<>();
        gamebeginloc_lore.add("§fClick to set to current position.");
        gamebeginloc_lore.add("§fStand where you wish for this position to be set.");
        gamebeginloc_meta.setLore(gamebeginloc_lore);
        gamebeginloc.setItemMeta(gamebeginloc_meta);

        ItemMeta edguardloc_meta = edguardloc.getItemMeta();
        edguardloc_meta.setDisplayName("§7Edguard Location");
        ArrayList<String> edguardloc_lore = new ArrayList<>();
        edguardloc_lore.add("§fClick to set to current position.");
        edguardloc_lore.add("§fStand where you wish for this position to be set.");
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
        gameRunning_meta.setDisplayName("§c§oStop Game.");
        ArrayList<String> gameRunning_lore = new ArrayList<>();
        gameRunning_lore.add("§fThe game is currently running.");
        gameRunning_meta.setLore(gameRunning_lore);
        gameRunning.setItemMeta(gameRunning_meta);

        ItemMeta gameWaiting_meta = gameWaiting.getItemMeta();
        gameWaiting_meta.setDisplayName("§e§oStart Game");
        ArrayList<String> gameWaiting_lore = new ArrayList<>();
        gameWaiting_lore.add("§fPlayer's will be teleported to lobby.");
        gameWaiting_meta.setLore(gameWaiting_lore);
        gameWaiting.setItemMeta(gameWaiting_meta);

        ItemMeta gamePaused_meta = gamePaused.getItemMeta();
        gamePaused_meta.setDisplayName("§e§oResume Game");
        ArrayList<String> gamePaused_lore = new ArrayList<>();
        gamePaused_lore.add("§fThe game is currently paused.");
        gamePaused_meta.setLore(gamePaused_lore);
        gamePaused.setItemMeta(gamePaused_meta);

        ItemMeta gameResumed_meta = gameResumed.getItemMeta();
        gameResumed_meta.setDisplayName("§e§oPause Game");
        ArrayList<String> gameResumed_lore = new ArrayList<>();
        gameResumed_lore.add("§fThe game is currently playing.");
        gameResumed_meta.setLore(gameResumed_lore);
        gameResumed.setItemMeta(gameResumed_meta);

        return new ItemStack[]{time_limit, day_night, player_visibility, day, night, valid, invalid, lobbyloc, gamebeginloc, edguardloc, goback, gameRunning, gameWaiting, gamePaused, gameResumed};
    }

    public void stopCountdown(){
        Bukkit.getScheduler().cancelTask(task);
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
        } else if (e.getView().getTitle().equalsIgnoreCase("§6Region Blocks")) {
            Player p = (Player) e.getPlayer();

            List<String> blocks = new ArrayList<>(List.of());
            for (int i = 0; i <= 26; i++) {
                if(regionblocks.getItem(i) != null) {
                    blocks.add(regionblocks.getItem(i).getType().name());
                }
            }
            MaterialsConfig.get().set("materials." + plugin.selectedRegion.get(p.getUniqueId()) + ".blocks", blocks);
            MaterialsConfig.save();
        }
    }

    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        if (currentSetting.containsKey(p.getUniqueId())) {
            switch (currentSetting.get(p.getUniqueId()).getSetting()) {
                case "craftalot.time-limit-in-seconds":
                    plugin.getConfig().set(currentSetting.get(p.getUniqueId()).getSetting(), message);
                    plugin.messagePlayer(p, "§a✔");
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        plugin.saveConfig();
                        p.openInventory(guiSettings);
                    });
                    settingsUser = null;
                    break;
            }
            e.setCancelled(true);
        }
    }
}

