package me.chazzagram.craftalot.commands;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.files.CraftlistConfig;
import me.chazzagram.craftalot.playerInfo.gameRunning;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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

    static public Entity edguard;
    public static Inventory gui = Bukkit.createInventory(null, 27, "§6Craftalot GUI");
    static int count = 0;
    static public boolean schedule = false;

    private static Craftalot plugin;

    public craftalotCommand(Craftalot plugin) {
        this.plugin = plugin;
    }

    public static void spawnEdguard(){

        if(plugin.getConfig().getLocation("craftalot.edguard-location") != null) {
            edguard = Bukkit.getWorld(plugin.getConfig().getString("craftalot.edguard-location.world")).spawnEntity(plugin.getConfig().getLocation("craftalot.edguard-location"), EntityType.VILLAGER);

            edguard.setGravity(false);
            edguard.setCustomName("§aEdguard");
            edguard.setCustomNameVisible(true);
            edguard.setInvulnerable(true);

            schedule = true;
            count = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                if (!schedule) {
                    Bukkit.getScheduler().cancelTask(count);
                } else {
                    edguard.teleport(plugin.getConfig().getLocation("craftalot.edguard-location"));
                }
            }, 20L, 0);
        }
    }

    public static void despawnEdguard(){
        if(edguard != null) {
            schedule = false;
            edguard.remove();
        } else {
            System.out.println("[Craftalot] Edguard does not exist, despawn is not required.");
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player p) {

            if (args.length == 0) {
                plugin.messagePlayer(p, "Type '/craftalot help' for the full command list.");

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
                        plugin.messagePlayer(p, "§8Craftalot Plugin Version: " + PlaceholderAPI.setPlaceholders(p, "%craftalot_version%"));
                        break;
                    case "help":
                        p.sendMessage(
                                """
                                §7--- Craftalot Commands ---
                                §6/ca help: §fThis page! Congrats!
                                §7§oUsage: /craftalot help {1,2,3..}
                                §6/ca wand: §fWand tool for region setup.
                                §7§oUsage: /craftalot wand
                                §6/ca edguard: §fCommand to control the item collector Edguard.
                                §7§oUsage: /craftalot edguard {spawn,despawn,movehere}
                                §6/ca reload: §fReloads the craftlist in the config.\s
                                §7§oUsage: /craftalot reload
                                §6/ca version: §fCheck what version the plugin is running on.
                                §7§oUsage: /craftalot version
                                """
                        );
                        break;
                    case "edguard":
                        if(args.length > 1) {
                            switch (args[1].toLowerCase()) {
                                case "spawn":

                                    if (!schedule){

                                        plugin.getConfig().set("craftalot.edguard-location", p.getLocation());
                                        plugin.saveConfig();

                                        spawnEdguard();

                                        plugin.messagePlayer(p, "Edguard has been spawned at your location!");
                                    } else {
                                        plugin.messagePlayer(p, "Edguard already exists in the world! Use '/ca edguard movehere' to teleport him to your location.");
                                    }

                                    break;
                                case "despawn":
                                    if (schedule) {
                                        despawnEdguard();
                                        plugin.messagePlayer(p, "Edguard has been despawned!");
                                    } else {
                                        plugin.messagePlayer(p, "Edguard does not currently exist in the world! Use '/ca edguard spawn' to summon him to your location!");
                                    }
                                    break;
                                case "movehere":
                                    if (schedule) {
                                        plugin.getConfig().set("craftalot.edguard-location", p.getLocation());
                                        plugin.saveConfig();
                                        edguard.teleport(p.getLocation());
                                        plugin.messagePlayer(p, "Edguard has been teleported to your location!");
                                    } else {
                                        plugin.messagePlayer(p, "Edguard does not currently exist in the world! Use '/ca edguard spawn' to summon him to your location!");
                                    }
                                    break;
                                default:
                                    plugin.messagePlayer(p, "§6Incorrect Arguement!\n§8§oUsage: /craftalot edguard {spawn,despawn,movehere}");
                                    break;
                            }
                        } else {
                            plugin.messagePlayer(p, "§6Missing Arguements!\n§8§oUsage: /craftalot edguard {spawn,despawn,movehere}");
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

                        CraftlistConfig.get().set("craftlist.item1", craftlist);
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
                            plugin.messagePlayer(p, "A game is not currently running, use /ca gui to start a game.");
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
                            plugin.messagePlayer(p, "A game is not currently running, use /ca gui to start a game.");
                        }
                        break;

                    default:
                        plugin.messagePlayer(p, "Invalid argument, type '/craftalot help' for the full command list.");
                        break;
                }

            }
        }
        return true;
    }
}
