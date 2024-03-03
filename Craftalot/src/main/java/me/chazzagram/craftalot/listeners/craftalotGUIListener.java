package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.commands.craftalotCommand;
import me.chazzagram.craftalot.files.*;
import me.chazzagram.craftalot.playerInfo.playerInfo;
import me.chazzagram.craftalot.playerInfo.settingsInfo;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import me.chazzagram.craftalot.playerInfo.gameRunning;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static me.chazzagram.craftalot.commands.craftalotCommand.*;

public class craftalotGUIListener implements Listener {
    private final Inventory guiCraftlist;
    private final Inventory guiSettings;
    public final Inventory guiGameControl;
    public final Inventory guiKitConfig;

    static public List<Entity> spawnedEntities = new ArrayList<>();

    public int countdownTime;

    private int task;
    private final Craftalot plugin;

    public HashMap<UUID, settingsInfo> currentSetting;

    public craftalotGUIListener(Craftalot plugin) {
        this.plugin = plugin;
        this.task = 0;
        this.countdownTime = 0;
        this.guiCraftlist = Bukkit.createInventory(null, 36, "§eCraftlist GUI");
        this.guiSettings = Bukkit.createInventory(null, 36, "§9Settings GUI");
        this.guiGameControl = Bukkit.createInventory(null, 36, "§9Game Control GUI");
        this.guiKitConfig = Bukkit.createInventory(null, 9, "§bKit Configuration");
        this.currentSetting = new HashMap<>();


    }

    @EventHandler
    public void clickEvent(InventoryClickEvent e) {


        Player p = (Player) e.getWhoClicked();

        if (e.getView().getTitle().equalsIgnoreCase("§6Craftalot GUI")) {
            if (e.getClickedInventory() != null && e.getClickedInventory().equals(gui)) {
                e.setCancelled(true);
                ItemStack[] menuItems = getMenuItems();
                switch (e.getSlot()) {
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

                        if (!gameRunning.isGameRunning()) {
                            guiGameControl.setItem(27, menuItems[12]);
                        } else {
                            guiGameControl.setItem(27, menuItems[11]);
                        }
                        guiGameControl.setItem(35, menuItems[10]);

                        p.openInventory(guiGameControl);
                        break;
                    case 14:
                        p.sendMessage("§7Loading interface..");
                        p.closeInventory();

                        guiSettings.setItem(10, menuItems[0]);
                        guiSettings.setItem(11, menuItems[17]);
                        guiSettings.setItem(12, menuItems[15]);
                        guiSettings.setItem(14, menuItems[16]);
                        guiSettings.setItem(15, menuItems[2]);
                        guiSettings.setItem(16, menuItems[1]);
                        guiSettings.setItem(19, menuItems[7]);
                        guiSettings.setItem(20, menuItems[8]);
                        guiSettings.setItem(21, menuItems[9]);
                        guiSettings.setItem(31, menuItems[10]);
                        switch (plugin.getConfig().getString("craftalot.time-of-day")) {
                            case "day":
                                guiSettings.setItem(25, menuItems[3]);
                                break;
                            case "night":
                                guiSettings.setItem(25, menuItems[4]);
                                break;
                            default:
                                plugin.messagePlayer(p, "Invalid configuration for 'time of day', toggle the setting to fix.");
                                break;
                        }
                        switch (plugin.getConfig().getString("craftalot.player-visibility")) {
                            case "true":
                                guiSettings.setItem(24, menuItems[5]);
                                break;
                            case "false":
                                guiSettings.setItem(24, menuItems[6]);
                                break;
                            default:
                                plugin.messagePlayer(p, "Invalid configuration for 'player visibility', toggle the setting to fix.");
                                break;
                        }
                        p.openInventory(guiSettings);
                        break;
                }
            }

        } else if (e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")) {
            if (e.getClickedInventory() != null && e.getClickedInventory().equals(guiCraftlist)) {
                if (e.getSlot() == 35) {
                    p.closeInventory();
                    for (int i = 0; i <= 32; i++) {
                        CraftlistConfig.get().set("craftlist.item" + i, guiCraftlist.getItem(i));
                        CraftlistConfig.save();
                    }
                    p.openInventory(craftalotCommand.gui);
                    e.setCancelled(true);
                } else if (e.getSlot() >= 0 && e.getSlot() <= 34) {
                    ItemStack item = guiCraftlist.getItem(e.getSlot());
                    CraftlistConfig.get().set("craftlist.item" + e.getSlot(), item);
                    CraftlistConfig.save();
                }
            }
        } else if (e.getView().getTitle().equalsIgnoreCase("§9Settings GUI")) {
            if (e.getClickedInventory() != null && e.getClickedInventory().equals(guiSettings)) {
                e.setCancelled(true);
                ItemStack[] menuItems = getMenuItems();
                switch (e.getSlot()) {
                    case 10:
                        currentSetting.put(p.getUniqueId(), new settingsInfo("craftalot.time-limit-in-seconds", null));
                        p.closeInventory();
                        plugin.messagePlayer(p, "§aPlease enter the time limit in seconds:");
                        break;
                    case 11:
                        currentSetting.put(p.getUniqueId(), new settingsInfo("craftalot.material-restock-delay", null));
                        p.closeInventory();
                        plugin.messagePlayer(p, "§aPlease enter the delay in seconds between region restocks:");
                        break;
                    case 12:
                        currentSetting.put(p.getUniqueId(), new settingsInfo("craftalot.points.per-craft", null));
                        p.closeInventory();
                        plugin.messagePlayer(p, "§aPlease enter the amount of points a player earns per craft:");
                        break;
                    case 16:
                        if (plugin.getConfig().getString("craftalot.time-of-day").equals("day")) {
                            plugin.getConfig().set("craftalot.time-of-day", "night");
                            plugin.saveConfig();
                            guiSettings.setItem(25, menuItems[4]);
                            if(gameRunning.isGameRunning()){
                                plugin.getConfig().getLocation("craftalot.game-begin-location").getWorld().setTime(18000);                            }

                        } else if (plugin.getConfig().getString("craftalot.time-of-day").equals("night")) {
                            plugin.getConfig().set("craftalot.time-of-day", "day");
                            plugin.saveConfig();
                            guiSettings.setItem(25, menuItems[3]);
                            if(gameRunning.isGameRunning()){
                                plugin.getConfig().getLocation("craftalot.game-begin-location").getWorld().setTime(6000);
                            }
                        } else {
                            plugin.messagePlayer(p, "Configuration was invalid, corrected to default value.");
                            plugin.getConfig().set("craftalot.time-of-day", "day");
                            plugin.saveConfig();
                            guiSettings.setItem(25, menuItems[3]);
                            if(gameRunning.isGameRunning()){
                                plugin.getConfig().getLocation("craftalot.game-begin-location").getWorld().setTime(6000);                            }
                        }
                        break;
                    case 15:
                        if (plugin.getConfig().getString("craftalot.player-visibility").equals("true")) {
                            plugin.getConfig().set("craftalot.player-visibility", false);
                            plugin.saveConfig();
                            guiSettings.setItem(24, menuItems[6]);
                            if(gameRunning.isGameRunning()) {
                                for(UUID uuid : plugin.pointSystem.keySet()) {
                                    Player player = Bukkit.getPlayer(uuid);
                                    for (UUID uuid2 : plugin.pointSystem.keySet()) {
                                        Player player2 = Bukkit.getPlayer(uuid2);
                                        if (player != player2) {
                                            player.hidePlayer(plugin, player2);
                                        }
                                    }
                                }
                            }

                        } else if (plugin.getConfig().getString("craftalot.player-visibility").equals("false")) {
                            plugin.getConfig().set("craftalot.player-visibility", true);
                            plugin.saveConfig();
                            guiSettings.setItem(24, menuItems[5]);
                            if(gameRunning.isGameRunning()) {
                                for(UUID uuid : plugin.pointSystem.keySet()) {
                                    Player player = Bukkit.getPlayer(uuid);
                                    for (UUID uuid2 : plugin.pointSystem.keySet()) {
                                        Player player2 = Bukkit.getPlayer(uuid2);
                                        if (player != player2) {
                                            player.showPlayer(plugin, player2);
                                        }
                                    }
                                }
                            }
                        } else {
                            plugin.messagePlayer(p, "Configuration was invalid, corrected to default value.");
                            plugin.getConfig().set("craftalot.player-visibility", true);
                            plugin.saveConfig();
                            guiSettings.setItem(24, menuItems[5]);
                            if(gameRunning.isGameRunning()) {
                                for(UUID uuid : plugin.pointSystem.keySet()) {
                                    Player player = Bukkit.getPlayer(uuid);
                                    for (UUID uuid2 : plugin.pointSystem.keySet()) {
                                        Player player2 = Bukkit.getPlayer(uuid2);
                                        if (player != player2) {
                                            player.showPlayer(plugin, player2);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case 19:
                        plugin.getConfig().set("craftalot.lobby-location", p.getLocation());
                        p.closeInventory();
                        plugin.messagePlayer(p, "§aLobby Location set to §fcurrent position.");
                        plugin.saveConfig();
                        break;
                    case 20:
                        plugin.getConfig().set("craftalot.game-begin-location", p.getLocation());
                        p.closeInventory();
                        plugin.messagePlayer(p, "§aGame Start Location set to §fcurrent position.");
                        plugin.saveConfig();
                        break;
                    case 21:
                        if (plugin.edguardSpawned) {
                            plugin.getConfig().set("craftalot.edguard-location", p.getLocation());
                            plugin.messagePlayer(p, "§aEdguard has been teleported to your §fcurrent position.");
                        } else {
                            plugin.messagePlayer(p, "§aEdguard does not currently exist in the world! Use '/ca edguard spawn' to summon him to your location!");
                        }
                        plugin.saveConfig();
                        break;
                    case 14:
                        // Code
                        p.sendMessage("§7Loading interface..");

                        for (int i = 0; i <= 7; i++) {
                            if (KitConfig.get().getItemStack("kit.item" + i) != null) {
                                guiKitConfig.setItem(i, KitConfig.get().getItemStack("kit.item" + i));
                            }
                        }
                        guiKitConfig.setItem(8, menuItems[10]);
                        p.openInventory(guiKitConfig);
                        break;
                    case 31:
                        p.closeInventory();
                        p.openInventory(craftalotCommand.gui);
                        break;
                }
            }
        } else if (e.getView().getTitle().equalsIgnoreCase("§bKit Configuration")) {
            if (e.getClickedInventory() != null && e.getClickedInventory().equals(guiKitConfig)) {
                if (e.getSlot() == 8) {
                    for (int i = 0; i <= 7; i++) {
                        KitConfig.get().set("kit.item" + i, guiKitConfig.getItem(i));
                        KitConfig.save();
                    }
                    p.openInventory(guiSettings);
                    e.setCancelled(true);
                } else if (e.getSlot() >= 0 && e.getSlot() <= 7) {
                    ItemStack item = guiKitConfig.getItem(e.getSlot());
                    CraftlistConfig.get().set("kit.item" + e.getSlot(), item);
                    CraftlistConfig.save();
                }
            }
        } else if (e.getView().getTitle().equalsIgnoreCase("§9Game Control GUI")) {
            if (e.getClickedInventory() != null && e.getClickedInventory().equals(guiGameControl)) {
                e.setCancelled(true);
                switch (e.getSlot()) {

                    // Start Game
                    case 27:
                        if (CraftlistConfig.get().isConfigurationSection("craftlist")) {
                            ConfigurationSection craftlistSection = CraftlistConfig.get().getConfigurationSection("craftlist");
                            if (craftlistSection.getKeys(false).isEmpty()) {
                                plugin.messagePlayer(p, "Game cannot start, the craftlist is empty! Configure this in /ca gui.");
                                break;
                            }
                        }
                        if(CraftlistConfig.get().getKeys(false).isEmpty()){
                            plugin.messagePlayer(p, "Game cannot start, the craftlist is empty! Configure this in /ca gui.");
                            break;
                        }
                        if (plugin.getConfig().get("craftalot.lobby-location") == null) {
                            plugin.messagePlayer(p, "Game cannot start, lobby-location has not been configured.");
                            break;
                        }
                        if (plugin.getConfig().get("craftalot.game-begin-location") == null) {
                            plugin.messagePlayer(p, "Game cannot start, game-begin-location has not been configured.");
                            break;
                        }
                        if (plugin.getConfig().get("craftalot.material-restock-delay") == null) {
                            plugin.messagePlayer(p, "Game cannot start, material-restock-delay has not been configured.");
                            break;
                        }
                        if (!plugin.edguardSpawned) {
                            plugin.messagePlayer(p, "Game cannot start, edguard has not been spawned.");
                            break;
                        }
                        ItemStack[] menuItems = getMenuItems();
                        if (e.getCurrentItem().equals(menuItems[12])) {
                            guiGameControl.setItem(27, menuItems[11]);
                            boolean blacklisted = false;

                            for(Player all : Bukkit.getServer().getOnlinePlayers()){
                                plugin.messagePlayer(all, "A craftalot game is starting in ~30 seconds! Use /ca join to join the game.");
                            }


                            for (UUID uuid : plugin.pointSystem.keySet()) {
                                Player player = Bukkit.getPlayer(uuid);
                                for (String uuidString : BlacklistConfig.get().getStringList("blacklisted-players")) {
                                    UUID blacklistuuid = UUID.fromString(uuidString);
                                    if (blacklistuuid.equals(player.getUniqueId())) {
                                        blacklisted = true;
                                    }
                                }
                                if (!blacklisted) {
                                    plugin.pointSystem.put(player.getUniqueId(), new playerInfo("noTeam", 0, null, player.getInventory().getContents()));
                                    player.getInventory().clear();
                                    plugin.messagePlayer(player,"Your info:\nTeam: " + plugin.pointSystem.get(player.getUniqueId()).getTeamName() + "\nPoints: " + plugin.pointSystem.get(player.getUniqueId()).getPoints());
                                }
                            }
                            gameRunning.setGameRunning(true);
                            int timeLimit = Integer.parseInt(plugin.getConfig().getString("craftalot.time-limit-in-seconds"));
                            countdownTime = 0;
                            task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

                                @Override
                                public void run() {
                                    if (!gameRunning.isGameRunning()) {
                                        stopCountdown();
                                    }
                                    countdownTime++;
                                    switch (countdownTime) {
                                        case 1:

                                            List<String> blackList = BlacklistConfig.get().getStringList("blacklisted-players");
                                            for (UUID uuid : plugin.pointSystem.keySet()) {
                                                Player player = Bukkit.getPlayer(uuid);
                                                plugin.messagePlayer(player, "The game is starting! Teleporting to lobby..");
                                                boolean blacklisted = false;
                                                if (!plugin.getConfig().getKeys(true).isEmpty()) {
                                                    for (String uuidString : blackList) {
                                                        UUID blacklistuuid = UUID.fromString(uuidString);
                                                        if (blacklistuuid.equals(player.getUniqueId())) {
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
                                            for (UUID uuid : plugin.pointSystem.keySet()) {
                                                Player player = Bukkit.getPlayer(uuid);
                                                plugin.messagePlayer(player, "Game will commence in 30 seconds, get ready!");
                                            }
                                            break;
                                        case 25:
                                            for (UUID uuid : plugin.pointSystem.keySet()) {
                                                Player player = Bukkit.getPlayer(uuid);
                                                plugin.messagePlayer(player, "Game will commence in 10 seconds..");
                                            }
                                            break;
                                        case 32:
                                            for (UUID uuid : plugin.pointSystem.keySet()) {
                                                Player player = Bukkit.getPlayer(uuid);
                                                plugin.messagePlayer(player, "Game starting in 3..");
                                            }
                                            break;
                                        case 33:
                                            for (UUID uuid : plugin.pointSystem.keySet()) {
                                                Player player = Bukkit.getPlayer(uuid);
                                                plugin.messagePlayer(player, "Game starting in 2..");
                                            }
                                            break;
                                        case 34:
                                            for (UUID uuid : plugin.pointSystem.keySet()) {
                                                Player player = Bukkit.getPlayer(uuid);
                                                plugin.messagePlayer(player, "Game starting in 1..");
                                            }
                                            break;
                                        case 35:
                                            guiGameControl.setItem(28, menuItems[14]);

                                            List<String> blackList2 = BlacklistConfig.get().getStringList("blacklisted-players");
                                            for (UUID uuid : plugin.pointSystem.keySet()) {
                                                Player player = Bukkit.getPlayer(uuid);
                                                plugin.messagePlayer(player, "Teleporting..");
                                                plugin.messagePlayer(player, "Speak to §a§lEdguard to receive instructions!");
                                                boolean blacklisted = false;
                                                if (!plugin.getConfig().getKeys(true).isEmpty()) {
                                                    for (String uuidString : blackList2) {
                                                        UUID blacklistuuid = UUID.fromString(uuidString);
                                                        if (blacklistuuid.equals(player.getUniqueId())) {
                                                            plugin.messagePlayer(player, "§cYou are exempt from playing, you are on the blacklist. If this is an error contact an administrator.");
                                                            blacklisted = true;
                                                            break;
                                                        }
                                                    }
                                                }
                                                if (!blacklisted) {
                                                    player.teleport(plugin.getConfig().getLocation("craftalot.game-begin-location"));
                                                    for (int i = 0; i <= 7; i++) {
                                                        if (KitConfig.get().getItemStack("kit.item" + i) != null) {
                                                            player.getInventory().setItem(i, KitConfig.get().getItemStack("kit.item" + i));
                                                            if(plugin.getConfig().getBoolean("craftalot.player-visibility")) {
                                                                for (UUID uuid2 : plugin.pointSystem.keySet()) {
                                                                    Player player2 = Bukkit.getPlayer(uuid2);
                                                                    if(player != player2) {
                                                                        player.hidePlayer(plugin, player2);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            if(plugin.getConfig().getString("craftalot.time-of-day").equalsIgnoreCase("day")){ plugin.getConfig().getLocation("craftalot.game-begin-location").getWorld().setTime(6000); }
                                            if(plugin.getConfig().getString("craftalot.time-of-day").equalsIgnoreCase("night")){ plugin.getConfig().getLocation("craftalot.game-begin-location").getWorld().setTime(18000); }

                                            new gameRunning(true, timeLimit, plugin) {

                                                @Override
                                                public void count(int current) {
                                                    if(current == 0){
                                                        guiGameControl.setItem(28, null);
                                                    }

                                                    List<String> blackList = BlacklistConfig.get().getStringList("blacklisted-players");

                                                    for (UUID uuid : plugin.pointSystem.keySet()) {
                                                        Player player = Bukkit.getPlayer(uuid);
                                                        boolean blacklisted = false;
                                                        if (!plugin.getConfig().getKeys(true).isEmpty()) {
                                                            for (String uuidString : blackList) {
                                                                UUID blacklistuuid = UUID.fromString(uuidString);
                                                                if (blacklistuuid.equals(player.getUniqueId())) {
                                                                    blacklisted = true;
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        if (!blacklisted) {
                                                            if (isGamePaused()) {
                                                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§c§lPAUSED: " + LocalTime.of(0, current / 60, current % 60).format(DateTimeFormatter.ofPattern("mm:ss"))));
                                                            } else {
                                                                if(plugin.pointSystem.get(player.getUniqueId()).getItemToCraft() != null) {
                                                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6§lCraft: §a§l" + convertToDisplayName(plugin.pointSystem.get(player.getUniqueId()).getItemToCraft().getType().toString()) + " §f- §e§lTime Left: §f§l" + LocalTime.of(0, current / 60, current % 60).format(DateTimeFormatter.ofPattern("mm:ss"))));
                                                                } else {
                                                                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§e§lTime Left: §f§l" + LocalTime.of(0, current / 60, current % 60).format(DateTimeFormatter.ofPattern("mm:ss"))));
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                            }.startTimer();
                                            stopCountdown();
                                            break;
                                        default:
                                            for (UUID uuid : plugin.pointSystem.keySet()) {
                                                Player player = Bukkit.getPlayer(uuid);
                                                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§6§lGame begins soon.."));
                                            }
                                                break;
                                    }
                                }
                            }, 0, 20);
                        } else {
                            stopGame();
                        }

                        break;

                    // Pause / Resume
                    case 28:
                        menuItems = getMenuItems();
                        if (gameRunning.isGameRunning()) {
                            if (!gameRunning.isGamePaused()) {
                                gameRunning.setGamePaused(true);
                                guiGameControl.setItem(28, menuItems[13]);
                                plugin.messagePlayer(p, "The game is now paused!");


                                List<String> blackList = BlacklistConfig.get().getStringList("blacklisted-players");

                                for (UUID uuid : plugin.pointSystem.keySet()) {
                                    Player player = Bukkit.getPlayer(uuid);
                                    plugin.messagePlayer(player, "All players are now frozen temporarily.");
                                    boolean blacklisted = false;
                                    if (!plugin.getConfig().getKeys(true).isEmpty()) {
                                        for (String uuidString : blackList) {
                                            UUID blacklistuuid = UUID.fromString(uuidString);
                                            if (blacklistuuid.equals(player.getUniqueId())) {
                                                plugin.messagePlayer(player, "§cYou are exempt from being paused, you are on the blacklist. If this is an error contact an administrator.");
                                                blacklisted = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!blacklisted) {
                                        Entity pausePlayer = player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);

                                        pausePlayer.setGravity(false);
                                        pausePlayer.setCustomName(player.getName());
                                        pausePlayer.setCustomNameVisible(false);
                                        pausePlayer.setInvulnerable(true);

                                        player.setGameMode(GameMode.SPECTATOR);
                                        player.setSpectatorTarget(pausePlayer);

                                        spawnedEntities.add(pausePlayer);

                                    }
                                }

                            } else {
                                gameRunning.setGamePaused(false);
                                guiGameControl.setItem(28, menuItems[14]);
                                plugin.messagePlayer(p, "The game is now resumed!");


                                List<String> blackList = BlacklistConfig.get().getStringList("blacklisted-players");

                                for (UUID uuid : plugin.pointSystem.keySet()) {
                                    Player player = Bukkit.getPlayer(uuid);
                                    plugin.messagePlayer(player, "All players are unfrozen!");
                                    boolean blacklisted = false;
                                    if (!plugin.getConfig().getKeys(true).isEmpty()) {
                                        for (String uuidString : blackList) {
                                            UUID blacklistuuid = UUID.fromString(uuidString);
                                            if (blacklistuuid.equals(player.getUniqueId())) {
                                                blacklisted = true;
                                                break;
                                            }
                                        }
                                    }
                                    if (!blacklisted) {
                                        player.setGameMode(GameMode.SURVIVAL);
                                    }
                                }
                                for (Entity ent : spawnedEntities) {
                                    ent.remove();
                                }
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
    }

    public static ItemStack[] getMenuItems() {
        ItemStack time_limit = new ItemStack(Material.CLOCK);
        ItemStack points_per = new ItemStack(Material.SUNFLOWER);
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
        ItemStack setkit = new ItemStack(Material.TRIDENT);
        ItemStack materialdelay = new ItemStack(Material.CLOCK);

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

        ItemMeta points_per_meta = points_per.getItemMeta();
        points_per_meta.setDisplayName("§ePoints-per-craft");
        ArrayList<String> points_per_lore = new ArrayList<>();
        points_per_lore.add("§fSet the amount of points earned per craft.");
        points_per_meta.setLore(points_per_lore);
        points_per.setItemMeta(points_per_meta);

        ItemMeta materialdelay_meta = materialdelay.getItemMeta();
        materialdelay_meta.setDisplayName("§eMaterial Restock Delay");
        ArrayList<String> materialdelay_lore = new ArrayList<>();
        materialdelay_lore.add("§fSet the delay between region restocks.");
        materialdelay_meta.setLore(materialdelay_lore);
        materialdelay.setItemMeta(materialdelay_meta);

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

        ItemMeta setkit_meta = setkit.getItemMeta();
        setkit_meta.setDisplayName("§7Kit Configuration");
        ArrayList<String> setkit_lore = new ArrayList<>();
        setkit_lore.add("§fOpen the kit editor for the game.");
        setkit_lore.add("§fMake sure to have the items prepared!");
        setkit_meta.setLore(setkit_lore);
        setkit.setItemMeta(setkit_meta);

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

        return new ItemStack[]{time_limit, day_night, player_visibility, day, night, valid, invalid, lobbyloc, gamebeginloc, edguardloc, goback, gameRunning, gameWaiting, gamePaused, gameResumed, points_per, setkit, materialdelay};
    }

    public void stopCountdown(){
        Bukkit.getScheduler().cancelTask(task);
    }

    public void stopGame() {
        gameRunning.setGameRunning(false);
        gameRunning.setGamePaused(false);

        List<String> blackList = BlacklistConfig.get().getStringList("blacklisted-players");

        for (UUID uuid : plugin.pointSystem.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            plugin.messagePlayer(player, "§c§lGAME OVER!");
            boolean blacklisted = false;
            if (!plugin.getConfig().getKeys(true).isEmpty()) {
                for (String uuidString : blackList) {
                    UUID blacklistuuid = UUID.fromString(uuidString);
                    if (blacklistuuid.equals(player.getUniqueId())) {
                        blacklisted = true;
                        break;
                    }
                }
            }
            if (!blacklisted) {
                if(plugin.getConfig().getBoolean("craftalot.player-visibility")) {
                    for (UUID uuid2 : plugin.pointSystem.keySet()) {
                        Player player2 = Bukkit.getPlayer(uuid2);
                        if(player != player2) {
                            player.showPlayer(plugin, player2);
                        }
                    }
                }
                plugin.messagePlayer(player, "Your finishing points: §b§l" + plugin.pointSystem.get(player.getUniqueId()).getPoints() + "pts");
                player.getInventory().clear();
                player.getInventory().setContents(plugin.pointSystem.get(player.getUniqueId()).getInventoryContent());
                plugin.pointSystem.remove(player.getUniqueId());
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(plugin.getConfig().getLocation("craftalot.lobby-location"));
            }
        }
        if(!spawnedEntities.isEmpty()) {
            for (Entity ent : spawnedEntities) {
                ent.remove();
            }
        }
        ItemStack[] menuItems = getMenuItems();
        this.guiGameControl.setItem(28, null);
        this.guiGameControl.setItem(27, menuItems[12]);

    }

    @EventHandler
    public void invEvent(InventoryInteractEvent e) {

        if (e.getView().getTitle().equalsIgnoreCase("§eCraftlist GUI")) {
            for (int i = 0; i <= 32; i++) {
                CraftlistConfig.get().set("craftlist.item" + i, guiCraftlist.getItem(i));
                CraftlistConfig.save();
            }
        } else if (e.getView().getTitle().equalsIgnoreCase("§bKit Configuration")){
            for (int i = 0; i <= 7; i++) {
                KitConfig.get().set("kit.item" + i, guiKitConfig.getItem(i));
                KitConfig.save();
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
            plugin.messagePlayer(p, "The materials list for region '§6" + plugin.selectedRegion.get(p.getUniqueId()) + "§7' has been updated.");
        }

        else if (e.getView().getTitle().equalsIgnoreCase("§bKit Configuration")){
            for (int i = 0; i <= 7; i++) {
                KitConfig.get().set("kit.item" + i, guiKitConfig.getItem(i));
                KitConfig.save();
            }
        }
    }


    @EventHandler
    public void playerChatEvent(AsyncPlayerChatEvent e) {
        boolean numberValid = true;
        Player p = e.getPlayer();
        String message = e.getMessage();
        if (currentSetting.containsKey(p.getUniqueId())) {
            switch (currentSetting.get(p.getUniqueId()).getSetting()) {
                case "craftalot.time-limit-in-seconds":
                case "craftalot.points.per-craft":
                case "craftalot.material-restock-delay":
                    try {
                        Integer.parseInt(e.getMessage());
                    } catch (NumberFormatException ex) {
                        numberValid = false;
                    }
                    if(numberValid) {
                        plugin.getConfig().set(currentSetting.get(p.getUniqueId()).getSetting(), message);
                        plugin.messagePlayer(p, "§a✔");
                        currentSetting.remove(p.getUniqueId());
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            plugin.saveConfig();
                            p.openInventory(guiSettings);
                        });
                    } else {
                        plugin.messagePlayer(p, "§c✘");
                        plugin.messagePlayer(p, "Invalid input! Please only enter a numeric value (e.g. 200):");
                    }
                    break;
            }
            e.setCancelled(true);
        }
    }

    public static String convertToDisplayName(String input) {
        String[] words = input.split("_");

        StringBuilder result = new StringBuilder();
        for (String word : words) {
            result.append(capitalizeFirstLetter(word)).append(" ");
        }

        return result.toString().trim();
    }

    private static String capitalizeFirstLetter(String word) {
        return word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
    }
}

