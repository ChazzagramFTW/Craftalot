package me.chazzagram.learnjava.commands;

import me.chazzagram.learnjava.LearnJava;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
Plugin Wish List:
- Create a GUI
- Add an in-game craft list in the GUI which acts like a chest of items.
- COMPLETE! Set up an Edguard NPC which can be spawned/despawned and moved.
- Create essential placeholders.
- Add a settings toggle in the GUI, toggle/change settings such as time limit, day/night, player visibility, material restocking delay, points etc.
- Add teleportation settings, such as lobby location, map location, waiting location.
- Add game start/pause/end functions.
- Potentially add an item frame recipe board?*

*/

public class craftalotCommand implements CommandExecutor {

    Entity edguard;
    int count = 0;
    boolean schedule = false;

    private final LearnJava plugin;

    public craftalotCommand(LearnJava plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player p) {

            if (args.length == 0) {
                p.sendMessage("Type '/craftalot help' for the full command list.");

            } else {
                switch (args[0].toLowerCase()) {
                    case "list":
                        List<String> craftableItems = this.plugin.getConfig().getStringList("craftalot.craftlist");
                        p.sendMessage("§6List of items to be crafted:");
                        for (String craftableItem : craftableItems) {
                            p.sendMessage("§a- " + craftableItem);
                        }
                        p.sendMessage("§8§oThis list can be changed in the config.");
                        break;
                    case "version":
                        p.sendMessage("§8Craftalot Plugin Version: " + plugin.getDescription().getVersion());
                        break;
                    case "help":
                        p.sendMessage(
                                "§7--- Craftalot Commands ---" +
                                "\n§6/ca help: §fThis page! Congrats!\n§7§oUsage: /craftalot help {1,2,3..}" +
                                "\n§6/ca list: §fList the craftable items.\n§7§oUsage: /craftalot list" +
                                "\n§6/ca edguard: §fCommand to control the item collector Edguard.\n§7§oUsage: /craftalot edguard {spawn,despawn,movehere}" +
                                "\n§6/ca version: §fCheck what version the plugin is running on.\n§7§oUsage: /craftalot version"
                        );
                        break;
                    case "edguard":
                        if(args.length > 1) {
                            switch (args[1].toLowerCase()) {
                                case "spawn":
                                    edguard = p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);

                                    plugin.getConfig().set("craftalot.edguard-location", p.getLocation());
                                    plugin.saveConfig();

                                    edguard.setGravity(false);
                                    edguard.setCustomName("§aEdguard");
                                    edguard.setCustomNameVisible(true);
                                    edguard.setInvulnerable(true);
                                    PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 99999999, 99999999, false, false);

                                    schedule = true;
                                    count = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!schedule) {
                                                Bukkit.getScheduler().cancelTask(count); } else {
                                                edguard.teleport(plugin.getConfig().getLocation("craftalot.edguard-location"));
                                            }
                                        }
                                    }, 20L, 0);

                                    p.sendMessage("Edguard has been spawned at your location!");
                                    break;
                                case "despawn":
                                    schedule = false;
                                    edguard.remove();
                                    p.sendMessage("Edguard has been despawned!");
                                    break;
                                case "movehere":
                                    plugin.getConfig().set("craftalot.edguard-location", p.getLocation());
                                    plugin.saveConfig();
                                    edguard.teleport(p.getLocation());
                                    p.sendMessage("Edguard has been teleported to your location!");
                                    break;
                                default:
                                    p.sendMessage("§6Incorrect Arguement!\n§8§oUsage: /craftalot edguard {spawn,despawn,movehere}");
                                    break;
                            }
                        } else {
                            p.sendMessage("§6Missing Arguements!\n§8§oUsage: /craftalot edguard {spawn,despawn,movehere}");
                            break;
                        }
                        break;

                    case "gui":
                        Inventory gui = Bukkit.createInventory(p, 27, "§6Craftalot GUI");

                        ItemStack craftlist = new ItemStack(Material.PAPER);
                        ItemStack edguard = new ItemStack(Material.VILLAGER_SPAWN_EGG);
                        ItemStack settings = new ItemStack(Material.CRAFTING_TABLE);

                        ItemMeta craftlist_meta = craftlist.getItemMeta();
                        craftlist_meta.setDisplayName("§eCraftlist");
                        ArrayList<String> craftlist_lore = new ArrayList<>();
                        craftlist_lore.add("§fEdit the craft list.");
                        craftlist_meta.setLore(craftlist_lore);
                        craftlist.setItemMeta(craftlist_meta);

                        plugin.getConfig().set("craftalot.craftlist.item1", craftlist);
                        plugin.saveConfig();

                        ItemMeta edguard_meta = edguard.getItemMeta();
                        edguard_meta.setDisplayName("§eEdguard");
                        ArrayList<String> edguard_lore = new ArrayList<>();
                        edguard_lore.add("§fManage edguard.");
                        edguard_meta.setLore(edguard_lore);
                        edguard.setItemMeta(edguard_meta);

                        ItemMeta settings_meta = settings.getItemMeta();
                        settings_meta.setDisplayName("§eSettings");
                        ArrayList<String> settings_lore = new ArrayList<>();
                        settings_lore.add("§fPlugin configuration.");
                        settings_meta.setLore(settings_lore);
                        settings.setItemMeta(settings_meta);

                        ItemStack[] menuItems = {craftlist, edguard, settings};
                        gui.setContents(menuItems);


                        p.openInventory(gui);




                    default:
                        p.sendMessage("Invalid argument, type '/craftalot help' for the full command list.");
                        break;
                }

            }
        }
        return true;
    }
}
