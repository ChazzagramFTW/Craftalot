package me.chazzagram.craftalot.commands;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.files.BlacklistConfig;
import me.chazzagram.craftalot.files.CraftlistConfig;
import me.chazzagram.craftalot.files.MaterialsConfig;
import me.chazzagram.craftalot.playerInfo.*;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/*
Plugin Wish List:
- COMPLETE! Create a GUI
- COMPLETE! Add an in-game craft list in the GUI which acts like a chest of items.
- COMPLETE! Set up an Edguard NPC which can be spawned/despawned and moved.
- Create essential placeholders.
- Add a settings toggle in the GUI, toggle/change settings such as time limit, day/night, player visibility, material restocking delay, points etc.
- Add teleportation settings, such as lobby location, map location, waiting location.
- Add game start/pause/end functions.
- Potentially add an item frame recipe board?*

Settings:
- Time Limit
- Spawn Points: Lobby, Map, Waiting
- Day/Night
- Player Visibility
- Material Restock Delay
- Point Distribution

Issues:
- COMPLETE! Edguard's entity does not remain the same when the server is restarted.
- Edguard can be hurt.
- COMPLETE! Edguard can be summoned multiple times.
- /ca list now does not function properly.
- /ca gui creates invalid command message yet still works.
- COMPLETE! /ca reload command.

*/

public class craftalotCommand implements CommandExecutor {

    public static Inventory gui = Bukkit.createInventory(null, 27, "§6Craftalot GUI");
    public static Inventory regionblocks = Bukkit.createInventory(null, 27, "§6Region Blocks");
    static int count = 0;

    boolean regionExists = false;
    static public boolean schedule = false;

    private final Craftalot plugin;

    public craftalotCommand(Craftalot plugin) {
        this.plugin = plugin;
    }

    public boolean checkRegion(Player p){
        if (!plugin.wandSystem.containsKey(p.getUniqueId())) {
            plugin.messagePlayer(p, "You do not currently have a region selected! Use §e/caa wand §7to create a region.");
            return false;
        } else {
            if (plugin.wandSystem.get(p.getUniqueId()).getCorner1() == null || plugin.wandSystem.get(p.getUniqueId()).getCorner2() == null) {
                plugin.messagePlayer(p, "You do not currently have a region selected! Use §e/caa wand §7to create a region.");
                return false;
            } else {
                return true;
            }
        }
    }

//    public static void spawnEdguard(){
//
//        if(plugin.getConfig().getLocation("craftalot.edguard-location") != null) {
//            Entity edguard = plugin.getConfig().getLocation("craftalot.edguard-location").getWorld().spawnEntity(plugin.getConfig().getLocation("craftalot.edguard-location"), EntityType.VILLAGER);
//
//            edguard.setGravity(false);
//            edguard.setCustomName("§aEdguard");
//            edguard.setCustomNameVisible(true);
//            edguard.setInvulnerable(true);
//
//            schedule = true;
//            count = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
//                if (!schedule) {
//                    edguard.remove();
//                    Bukkit.getScheduler().cancelTask(count);
//
//                } else {
//                    edguard.teleport(plugin.getConfig().getLocation("craftalot.edguard-location"));
//                }
//            }, 20L, 0);
//
//            for(Entity entities : plugin.getConfig().getLocation("craftalot.edguard-location").getChunk().getEntities()){
//                if(!entities.getUniqueId().equals(edguard.getUniqueId()) && entities.getType().equals(EntityType.VILLAGER) && entities.getName().equals("§aEdguard")){
//                    entities.remove();
//                }
//            }
//        }
//    }

//    public static void despawnEdguard(){
//        if(schedule && edguard != null) {
//            Bukkit.getScheduler().cancelTask(count);
//            schedule = false;
//            edguard.remove();
//            edguard = null;
//        } else {
//            System.out.println("[Craftalot] Edguard does not exist, despawn is not required.");
//        }
//    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {


        if (commandSender instanceof Player p) {

            if (args.length == 0) {
                plugin.messagePlayer(p, "Type '/craftalotadmin help' for the full command list.");

            } else {
                switch (args[0].toLowerCase()) {
                    case "list":
                        List<String> craftableItems = plugin.getConfig().getStringList("craftlist");
                        p.sendMessage("§6List of items to be crafted:");
                        for (String craftableItem : craftableItems) {
                            p.sendMessage("§a- " + craftableItem);
                        }
                        p.sendMessage("§8§oThis list can be changed in the config.");
                        break;
                    case "version":
                        plugin.messagePlayer(p, "§8Craftalot Plugin Version: " + plugin.getDescription().getVersion());
                        break;
                    case "help":
                        if(args.length > 1) {
                            switch (args[1]) {
                                case "1":
                                    p.sendMessage(
                                            """
                                                    §7--- Craftalot Commands §f1/3 §7---
                                                    §6/caa help: §fThis page! Congrats!
                                                    §7§oUsage: /craftalotadmin help {1,2,3..}
                                                    §6/caa wand: §fWand tool for region setup.
                                                    §7§oUsage: /craftalotadmin wand
                                                    §6/caa edguard: §fCommand to control the item collector Edguard.
                                                    §7§oUsage: /craftalotadmin edguard {spawn,despawn,respawn,movehere}
                                                    §6/caa gui: §fOpen the gui interface to control the game.\s
                                                    §7§oUsage: /craftalotadmin gui
                                                    §6/caa reload: §fReloads the craftlist in the config.
                                                    §7§oUsage: /craftalotadmin reload
                                                    §fUse §e/caa help {number} §ffor the more pages.
                                                    """
                                    );
                                    break;
                                case "2":
                                    p.sendMessage(
                                            """
                                                    §7--- Craftalot Commands §f2/3 §7---
                                                    §6/caa setregion: §fDefine a materials region.
                                                    §7§oUsage: /craftalotadmin setregion {region-name}
                                                    §6/caa delregion: §fDelete an exisiting materials region.
                                                    §7§oUsage: /craftalotadmin delregion {region-name}
                                                    §6/caa updateregion: §fUpdate region with new coordinates.
                                                    §7§oUsage: /craftalotadmin updatedregion {region-name}
                                                    §6/caa listregions: §fList existing materials regions.\s
                                                    §7§oUsage: /craftalotadmin listregions
                                                    §6/caa setblocks: §fSet the materials within a region.
                                                    §7§oUsage: /craftalotadmin setblocks {region-name}
                                                    §fUse §e/caa help {number} §ffor the more pages.
                                                    """
                                    );
                                    break;
                                case "3":
                                    p.sendMessage(
                                            """
                                                    §7--- Craftalot Commands §f3/3 §7---
                                                    §6/caa blacklist: §fAdd players to the game blacklist.
                                                    §7§oUsage: /craftalotadmin blacklist {online-player}
                                                    §6/caa restock: §fRestock existing regions manually.
                                                    §7§oUsage: /craftalotadmin restock
                                                    §fUse §e/caa help {number} §ffor the more pages.
                                                    """
                                    );
                                    break;
                            }
                        } else {
                            p.sendMessage(
                                    """
                                            §7--- Craftalot Commands §f1/3 §7---
                                            §6/caa help: §fThis page! Congrats!
                                            §7§oUsage: /craftalotadmin help {1,2,3..}
                                            §6/caa wand: §fWand tool for region setup.
                                            §7§oUsage: /craftalotadmin wand
                                            §6/caa edguard: §fCommand to control the item collector Edguard.
                                            §7§oUsage: /craftalotadmin edguard {spawn,despawn,movehere}
                                            §6/caa gui: §fOpen the gui interface to control the game.\s
                                            §7§oUsage: /craftalotadmin gui
                                            §6/caa reload: §fReloads the craftlist in the config.
                                            §7§oUsage: /craftalotadmin reload
                                            §fUse §e/caa help {number} §ffor the more pages.
                                            """
                            );
                        }
                        break;
                    case "edguard":
                        if(args.length > 1) {
                            switch (args[1].toLowerCase()) {
                                case "spawn":
                                    if (!plugin.edguardSpawned){


                                        plugin.getConfig().set("craftalot.edguard-location", p.getLocation());
                                        plugin.saveConfig();

//                                        spawnEdguard();

                                        if(!plugin.edguardSpawned) {
                                            plugin.edguard = plugin.getConfig().getLocation("craftalot.edguard-location").getWorld().spawnEntity(plugin.getConfig().getLocation("craftalot.edguard-location"), EntityType.VILLAGER);

                                            plugin.edguard.setGravity(false);
                                            plugin.edguard.setCustomName("§aEdguard");
                                            plugin.edguard.setCustomNameVisible(true);
                                            plugin.edguard.setInvulnerable(true);

                                            plugin.edguardSpawned = true;

                                            plugin.timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (plugin.edguard != null && plugin.edguardSpawned) {
                                                        plugin.edguard.teleport(plugin.getConfig().getLocation("craftalot.edguard-location"));
                                                    }
                                                }
                                            }, 20L, 0L);
                                        }

                                        plugin.messagePlayer(p, "Edguard has been spawned at your location!");
                                    } else {
                                        plugin.messagePlayer(p, "Edguard already exists in the world! Use '/caa edguard movehere' to teleport him to your location.");
                                    }

                                    break;
                                case "despawn":
                                    if (plugin.edguardSpawned) {
                                        plugin.edguard.remove();
                                        Bukkit.getScheduler().cancelTask(plugin.timer);
                                        plugin.edguardSpawned = false;
                                        plugin.getConfig().set("craftalot.edguard-location", null);
                                        plugin.messagePlayer(p, "Edguard has been despawned!");
                                    } else {
                                        plugin.messagePlayer(p, "Edguard does not currently exist in the world! Use '/caa edguard spawn' to summon him to your location!");
                                    }
                                    break;
                                case "respawn":
                                    if (plugin.edguardSpawned) {
                                        plugin.edguard.remove();
                                        Bukkit.getScheduler().cancelTask(plugin.timer);
                                        plugin.edguardSpawned = false;

                                        if(plugin.getConfig().getLocation("craftalot.edguard-location") != null) {

                                            if (!plugin.edguardSpawned) {
                                                plugin.edguard = plugin.getConfig().getLocation("craftalot.edguard-location").getWorld().spawnEntity(plugin.getConfig().getLocation("craftalot.edguard-location"), EntityType.VILLAGER);

                                                plugin.edguard.setGravity(false);
                                                plugin.edguard.setCustomName("§aEdguard");
                                                plugin.edguard.setCustomNameVisible(true);
                                                plugin.edguard.setInvulnerable(true);

                                                plugin.edguardSpawned = true;

                                                plugin.timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (plugin.edguard != null && plugin.edguardSpawned) {
                                                            plugin.edguard.teleport(plugin.getConfig().getLocation("craftalot.edguard-location"));
                                                        }
                                                    }
                                                }, 20L, 0L);
                                            }
                                            plugin.messagePlayer(p, "Edguard has been respawned at your location!");
                                        } else {
                                            plugin.messagePlayer(p, "There is no edguard location setup, use /caa edguard spawn to spawn edguard.");
                                        }
                                    } else {
                                        if (plugin.getConfig().getLocation("craftalot.edguard-location") == null) {
                                            plugin.messagePlayer(p, "Edguard does not currently exist in the world! Use '/caa edguard spawn' to summon him to your location!");
                                        } else {
                                            if (!plugin.edguardSpawned) {
                                                plugin.edguard = plugin.getConfig().getLocation("craftalot.edguard-location").getWorld().spawnEntity(plugin.getConfig().getLocation("craftalot.edguard-location"), EntityType.VILLAGER);

                                                plugin.edguard.setGravity(false);
                                                plugin.edguard.setCustomName("§aEdguard");
                                                plugin.edguard.setCustomNameVisible(true);
                                                plugin.edguard.setInvulnerable(true);

                                                plugin.edguardSpawned = true;

                                                plugin.timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (plugin.edguard != null && plugin.edguardSpawned) {
                                                            plugin.edguard.teleport(plugin.getConfig().getLocation("craftalot.edguard-location"));
                                                        }
                                                    }
                                                }, 20L, 0L);
                                            }

                                            plugin.messagePlayer(p, "Edguard has been respawned at your location!");
                                        }
                                    }
                                    break;
                                case "movehere":

                                    if (plugin.edguardSpawned) {
                                        plugin.getConfig().set("craftalot.edguard-location", p.getLocation());
                                        plugin.saveConfig();
//                                        edguard.teleport(p.getLocation());
                                        plugin.messagePlayer(p, "Edguard has been teleported to your location!");
                                    } else {
                                        plugin.messagePlayer(p, "Edguard does not currently exist in the world! Use '/caa edguard spawn' to summon him to your location!");
                                    }
                                    break;
                                default:
                                    plugin.messagePlayer(p, "§6Incorrect Arguement!\n§8§oUsage: /craftalotadmin edguard {spawn,despawn,movehere}");
                                    break;
                            }
                        } else {
                            plugin.messagePlayer(p, "§6Missing Arguements!\n§8§oUsage: /craftalotadmin edguard {spawn,despawn,movehere}");
                            break;
                        }
                        break;

                    case "gui":

                        ItemStack craftlist = new ItemStack(Material.PAPER);
                        ItemStack game = new ItemStack(Material.VILLAGER_SPAWN_EGG);
                        ItemStack settings = new ItemStack(Material.CRAFTING_TABLE);

                        ItemMeta craftlist_meta = craftlist.getItemMeta();
                        craftlist_meta.setDisplayName("§eCraftlist");
                        ArrayList<String> craftlist_lore = new ArrayList<>();
                        craftlist_lore.add("§fEdit the craft list.");
                        craftlist_meta.setLore(craftlist_lore);
                        craftlist.setItemMeta(craftlist_meta);

                        CraftlistConfig.save();

                        ItemMeta game_meta = game.getItemMeta();
                        game_meta.setDisplayName("§eGame Control");
                        ArrayList<String> game_lore = new ArrayList<>();
                        game_lore.add("§fManage gameplay.");
                        game_meta.setLore(game_lore);
                        game.setItemMeta(game_meta);

                        ItemMeta settings_meta = settings.getItemMeta();
                        settings_meta.setDisplayName("§eSettings");
                        ArrayList<String> settings_lore = new ArrayList<>();
                        settings_lore.add("§fPlugin configuration.");
                        settings_meta.setLore(settings_lore);
                        settings.setItemMeta(settings_meta);

                        ItemStack[] menuItems = {craftlist, game, settings};
                        gui.setItem(12, menuItems[0]);
                        gui.setItem(13, menuItems[1]);
                        gui.setItem(14, menuItems[2]);


                        p.openInventory(gui);
                        break;

                    case "reload":
                        CraftlistConfig.reload();
                        plugin.messagePlayer(p, "Craftlist.yml reloaded.");
                        break;


                    case "wand":
                        ItemStack wand = new ItemStack(Material.STICK);

                        ItemMeta wand_meta = wand.getItemMeta();
                        wand_meta.setDisplayName("§e§lCraftalot Wand");
                        ArrayList<String> wand_lore = new ArrayList<>();
                        wand_lore.add("§fUsed for region setup.");
                        wand_meta.setLore(wand_lore);
                        wand.setItemMeta(wand_meta);

                        plugin.wandSystem.put(p.getUniqueId(), new wandInfo(null, null));

                        Inventory playerInv = p.getInventory();
                        playerInv.addItem(wand);
                        break;

                    case "pause":
                        if(gameRunning.isGameRunning()) {
                            if (!gameRunning.isGamePaused()) {
                                gameRunning.setGamePaused(true);
                            } else {
                                plugin.messagePlayer(p, "The game is already paused!");
                            }
                        } else {
                            plugin.messagePlayer(p, "A game is not currently running, use /caa gui to start a game.");
                        }
                        break;

                    case "unpause":
                        if(gameRunning.isGameRunning()) {
                            if (gameRunning.isGamePaused()) {
                                gameRunning.setGamePaused(false);
                            } else {
                                plugin.messagePlayer(p, "The game is already playing!");
                            }
                        } else {
                            plugin.messagePlayer(p, "A game is not currently running, use /caa gui to start a game.");
                        }
                        break;

                    case "blacklist":
                        if (args.length > 1){
                            if (Bukkit.getServer().getPlayer(args[1]) != null) {
                                if (plugin.getConfig().getKeys(true).isEmpty()) {
                                    List<String> blackList = new ArrayList<>();
                                    blackList.add(Bukkit.getPlayer(args[1]).getUniqueId().toString());
                                    BlacklistConfig.get().set("blacklisted-players", blackList);
                                    BlacklistConfig.save();
                                } else {
                                    boolean blacklisted = false;
                                    List<String> blackList = BlacklistConfig.get().getStringList("blacklisted-players");
                                    for (String uuidString : blackList) {
                                        UUID uuid = UUID.fromString(uuidString);
                                        if (uuid.equals(Bukkit.getPlayer(args[1]).getUniqueId())) {
                                            plugin.messagePlayer(p, "§6'" + args[1] + "' §7is already on the blacklist.");
                                            blacklisted = true;
                                            break;
                                        }
                                    }
                                    if (!blacklisted) {
                                        plugin.messagePlayer(p, "§6'" + args[1] + "' §7is now on the blacklist.");
                                        blackList.add(Bukkit.getPlayer(args[1]).getUniqueId().toString());
                                        BlacklistConfig.get().set("blacklisted-players", blackList);
                                        BlacklistConfig.save();
                                    }
                                }
                            } else {
                                plugin.messagePlayer(p, "Player §6'" + args[1] + "' §7is not online, to be blacklisted the player must be online on the server. Alternatively, you can grab the UUID of the player and paste it into the §6'blacklist.yml' §7config file.");
                            }
                        }
                        break;

                    case "setregion":
                        if(checkRegion(p)) {
                            if (args.length > 1) {
                                if (MaterialsConfig.get().getConfigurationSection("materials") == null) {
                                    plugin.messagePlayer(p, "Materials region '§6" + args[1] + "§7' has been created.");
                                    MaterialsConfig.get().set("materials." + args[1] + ".corner1", plugin.wandSystem.get(p.getUniqueId()).getCorner1());
                                    MaterialsConfig.get().set("materials." + args[1] + ".corner2", plugin.wandSystem.get(p.getUniqueId()).getCorner2());
                                    MaterialsConfig.save();
                                } else {
                                    regionExists = false;
                                    for (String key : MaterialsConfig.get().getConfigurationSection("materials").getKeys(false)) {
                                        if (args[1].toLowerCase().equals(key)) {
                                            plugin.messagePlayer(p, "A region with this name already exists!");
                                            regionExists = true;
                                            break;
                                        }
                                    }
                                    if (!regionExists) {
                                        plugin.messagePlayer(p, "Materials region '§6" + args[1] + "§7' has been created.");
                                        MaterialsConfig.get().set("materials." + args[1] + ".corner1", plugin.wandSystem.get(p.getUniqueId()).getCorner1());
                                        MaterialsConfig.get().set("materials." + args[1] + ".corner2", plugin.wandSystem.get(p.getUniqueId()).getCorner2());
                                        MaterialsConfig.save();
                                    }
                                    regionExists = false;
                                }
                            } else {
                                plugin.messagePlayer(p, "§6Missing Arguements!\n§8§oUsage: /craftalotadmin setregion {region-name}");

                            }
                        }
                        break;

                    case "delregion":
                        if(args.length > 1) {
                            if (MaterialsConfig.get().getConfigurationSection("materials") == null) {
                                plugin.messagePlayer(p, "Currently no material regions exist.");
                            } else {
                                regionExists = false;
                                for (String key : MaterialsConfig.get().getConfigurationSection("materials").getKeys(false)) {
                                    if (args[1].toLowerCase().equals(key)) {
                                        plugin.messagePlayer(p, "Region '§6" + args[1] + "' §7has been deleted.");
                                        MaterialsConfig.get().set("materials." + args[1], null);
                                        MaterialsConfig.save();
                                        regionExists = true;
                                        break;
                                    }
                                }
                                if (!regionExists){
                                    plugin.messagePlayer(p, "A materials region named '§6" + args[1] + "§7' does not currently exist.");
                                    MaterialsConfig.save();
                                }
                                regionExists = false;
                            }
                        } else {
                            plugin.messagePlayer(p, "§6Missing Arguements!\n§8§oUsage: /craftalotadmin delregion {region-name}");

                        }
                        break;

                    case "updateregion":
                        if(checkRegion(p)) {
                            if (args.length > 1) {
                                if (MaterialsConfig.get().getConfigurationSection("materials") == null) {
                                    plugin.messagePlayer(p, "No material regions currently exist! Use §e/caa setregion {region-name} §7to create one.");
                                } else {
                                    regionExists = false;
                                    for (String key : MaterialsConfig.get().getConfigurationSection("materials").getKeys(false)) {
                                        if (args[1].toLowerCase().equals(key)) {
                                            plugin.messagePlayer(p, "Materials region '§6" + args[1] + "§7' has been updated with a new region.");
                                            MaterialsConfig.get().set("materials." + args[1] + ".corner1", plugin.wandSystem.get(p.getUniqueId()).getCorner1());
                                            MaterialsConfig.get().set("materials." + args[1] + ".corner2", plugin.wandSystem.get(p.getUniqueId()).getCorner2());
                                            MaterialsConfig.save();
                                            regionExists = true;
                                            break;
                                        }
                                    }
                                    if (!regionExists) {
                                        plugin.messagePlayer(p, "No material regions named '§6" + args[1] + "§7' currently exists. Use §e/caa setregion {region-name} §7to create one.");
                                    }
                                    regionExists = false;
                                }
                            } else {
                                plugin.messagePlayer(p, "§6Missing Arguements!\n§8§oUsage: /craftalotadmin updateregion {region-name}");

                            }
                        }
                        break;

                    case "listregions":
                        if (MaterialsConfig.get().getConfigurationSection("materials") == null) {
                            plugin.messagePlayer(p, "Currently no material regions exist.");
                        } else {
                            plugin.messagePlayer(p, "Regions list:");
                            ConfigurationSection section = MaterialsConfig.get().getConfigurationSection("materials.");
                            for (String key : section.getKeys(false)) {
                                p.sendMessage("- §6" + key);
                            }
                        }
                        break;

                    case "setblocks":
                        if (args.length > 1) {
                            if (MaterialsConfig.get().getConfigurationSection("materials") == null) {
                                plugin.messagePlayer(p, "No material regions currently exist! Use §e/caa setregion {region-name} §7to create one.");
                            } else {
                                regionExists = false;
                                for (String key : MaterialsConfig.get().getConfigurationSection("materials").getKeys(false)) {
                                    if (args[1].toLowerCase().equals(key)) {
                                        regionblocks.clear();
                                        plugin.selectedRegion.put(p.getUniqueId(), key);
                                        List<String> regionContents = MaterialsConfig.get().getStringList("materials." + args[1] + ".blocks");
                                        for (String regionContent : regionContents) {
                                            Material itemSelected = Material.matchMaterial(regionContent);
                                            regionblocks.addItem(new ItemStack(itemSelected,1));
                                        }

                                        p.openInventory(regionblocks);
                                        regionExists = true;
                                        break;
                                    }
                                }
                                if (!regionExists) {
                                    plugin.messagePlayer(p, "No material regions named '§6" + args[1] + "§7' currently exists. Use §e/caa setregion {region-name} §7to create one.");
                                }
                                regionExists = false;
                            }
                        } else {
                            plugin.messagePlayer(p, "§6Missing Arguements!\n§8§oUsage: /craftalotadmin setblocks {region-name}");

                        }
                        break;

                    case "restock":
                        restockRegions();
                        break;



                    default:
                        plugin.messagePlayer(p, "Invalid argument, type '/craftalotadmin help' for the full command list.");
                        break;
                }

            }
        }
        return true;
    }

    public void restockRegions(){
        if (MaterialsConfig.get().getConfigurationSection("materials") == null) {
            plugin.messageConsole("Currently no material regions exist to be restocked.");
        } else {
            Random r = new Random();
            ConfigurationSection section = MaterialsConfig.get().getConfigurationSection("materials.");
            for (String key : section.getKeys(false)) {
                World world = MaterialsConfig.get().getLocation("materials." + key + ".corner1").getWorld();
                Location loc1 = MaterialsConfig.get().getLocation("materials." + key + ".corner1"); // Location 1
                Location loc2 = MaterialsConfig.get().getLocation("materials." + key + ".corner2"); // Location 2

                List<String> keyBlocks = MaterialsConfig.get().getStringList("materials." + key + ".blocks");

                if(keyBlocks.isEmpty()) {
                    plugin.messageConsole("Region " + key + " has no blocks, use /caa setblocks " + key + " to add blocks.");
                    continue;
                }

                int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
                int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
                int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

                int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
                int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
                int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            world.getBlockAt(x, y, z).setType(Material.matchMaterial(keyBlocks.get(r.nextInt(keyBlocks.size()))));
                        }
                    }
                }
                plugin.messageConsole("Region " + key + " has been restocked successfully.");
            }
        }
        ArrayList<UUID> onlinePlayers = new ArrayList<>(plugin.pointSystem.keySet());
        for (UUID uuid : onlinePlayers) {
            Player player = Bukkit.getPlayer(uuid);
            plugin.messagePlayer(player, "Material regions have been restocked!");
        }

    }

}
