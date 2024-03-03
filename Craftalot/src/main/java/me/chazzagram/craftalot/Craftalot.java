package me.chazzagram.craftalot;

import me.chazzagram.craftalot.commands.*;
import me.chazzagram.craftalot.expansion.SpigotExpansion;
import me.chazzagram.craftalot.files.*;
import me.chazzagram.craftalot.listeners.*;
import me.chazzagram.craftalot.playerInfo.gameRunning;
import me.chazzagram.craftalot.playerInfo.playerInfo;
import me.chazzagram.craftalot.playerInfo.wandInfo;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

import static me.chazzagram.craftalot.listeners.craftalotGUIListener.spawnedEntities;

public final class Craftalot extends JavaPlugin implements Listener {


    private static Craftalot plugin;

    private static Craftalot instance;

    public Entity edguard;

    public static Craftalot getInstance(){
        return instance;
    }

    public HashMap<UUID, playerInfo> pointSystem;
    public HashMap<UUID, wandInfo> wandSystem;

    public HashMap<UUID, String> selectedRegion;

//    private edguardInfo edguardInfo;

    public boolean edguardSpawned;

    public int timer;


    @Override
    public void onEnable() {
        // Plugin startup logic

        this.pointSystem = new HashMap<>();

        this.wandSystem = new HashMap<>();

        this.selectedRegion = new HashMap<>();

//        edguardInfo = new edguardInfo(this);

        instance = this;

        plugin = this;

        messageConsole("My first plugin has started, hello.");


        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new craftalotGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new edguardListener(this), this);
        getServer().getPluginManager().registerEvents(new wandListener(this), this);
        getServer().getPluginManager().registerEvents(new PauseListener(this), this);
//        getServer().getPluginManager().registerEvents(new edguardInfo(this), this);

        getCommand("craftalotadmin").setExecutor(new craftalotCommand(this));
        getCommand("craftalot").setExecutor(new playerCommand(this));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getPluginManager().registerEvents(this, this);
            new SpigotExpansion(this).register();
        }
        // config
        CraftlistConfig.setup();
        CraftlistConfig.get().addDefault("craftlist", "");
        CraftlistConfig.get().options().copyDefaults();
        CraftlistConfig.save();

        MaterialsConfig.setup();
        MaterialsConfig.get().addDefault("materials", "");
        MaterialsConfig.get().options().copyDefaults();
        MaterialsConfig.save();

        BlacklistConfig.setup();
        BlacklistConfig.get().addDefault("blacklisted-players", "");
        BlacklistConfig.get().options().copyDefaults();
        BlacklistConfig.save();

        KitConfig.setup();
        KitConfig.get().addDefault("kit", null);
        KitConfig.get().options().copyDefaults();
        KitConfig.save();

        getConfig().options().copyDefaults();
        saveDefaultConfig();


        if(plugin.getConfig().getLocation("craftalot.edguard-location") != null){
            messageConsole("Edguard needs to be respawned! Use /reload or /caa edguard respawn.");
        }


    }


    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() == plugin) {
            if(!Bukkit.getOnlinePlayers().isEmpty()) {
                if(edguardSpawned) {
                    edguard.remove();
                    Bukkit.getScheduler().cancelTask(timer);
                    edguardSpawned = false;
                }
            }
        }
    }

    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        if (event.getPlugin() == plugin) {
            if(!Bukkit.getOnlinePlayers().isEmpty()) {
                if (plugin.getConfig().getLocation("craftalot.edguard-location") != null) {
                    if(!edguardSpawned) {
                        edguard = plugin.getConfig().getLocation("craftalot.edguard-location").getWorld().spawnEntity(plugin.getConfig().getLocation("craftalot.edguard-location"), EntityType.VILLAGER);

                        edguard.setGravity(false);
                        edguard.setCustomName("§aEdguard");
                        edguard.setCustomNameVisible(true);
                        edguard.setInvulnerable(true);

                        edguardSpawned = true;

                        timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                            @Override
                            public void run() {
                                if (edguard != null && edguardSpawned) {
                                    edguard.teleport(plugin.getConfig().getLocation("craftalot.edguard-location"));
                                }
                            }
                        }, 20L, 0L);
                    }
                }
            }
        }
    }




//        craftalotCommand.spawnEdguard();


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        messageConsole("My first plugin has stopped, bye.");

        if(edguardSpawned){
            edguard.remove();
            Bukkit.getScheduler().cancelTask(timer);
            edguardSpawned = false;
        }



        for (Entity entity : spawnedEntities) {
            entity.remove();
        }



//        craftalotCommand.despawnEdguard();
        if(gameRunning.isGameRunning()){
            if(plugin.getConfig().getBoolean("craftalot.player-visibility")) {
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
            for(UUID uuid : pointSystem.keySet()){
                Player p = Bukkit.getPlayer(uuid);
                plugin.messagePlayer(p, "You have left the game.");
                p.getInventory().clear();
                p.getInventory().setContents(plugin.pointSystem.get(p.getUniqueId()).getInventoryContent());
                plugin.pointSystem.remove(p.getUniqueId());
                p.setGameMode(GameMode.SURVIVAL);
                p.teleport(plugin.getConfig().getLocation("craftalot.lobby-location"));
            }
        }

    }

    public static Craftalot getPlugin() {
        return plugin;
    }

    public void messagePlayer(Player p, String message){
        p.sendMessage(plugin.getConfig().getString("craftalot.prefix") + message);
    }

    public void messageConsole(String message){
        System.out.println("[Craftalot] " + message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /die - kills player
        if (command.getName().equalsIgnoreCase("die")){

            if(sender instanceof Player p){
                p.setHealth(0);
                p.sendMessage(ChatColor.RED + "You have opted to die.");
            }else if(sender instanceof ConsoleCommandSender){
                System.out.println("The command was run by the console.");
            }else if(sender instanceof BlockCommandSender){
                System.out.println("The command was run by a command block.");
            }

        }else if (command.getName().equalsIgnoreCase("blowup")){

            if(sender instanceof Player p){
                p.setHealth(0);
                p.sendMessage(ChatColor.RED + "You have opted to die.");
            }else if(sender instanceof ConsoleCommandSender){
                System.out.println("The command was run by the console.");
            }else if(sender instanceof BlockCommandSender){
                System.out.println("The command was run by a command block.");
            }

        }

        return super.onCommand(sender, command, label, args);

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        if(pointSystem.containsKey(p.getUniqueId())) {
            if(plugin.getConfig().getBoolean("craftalot.player-visibility")) {
                for (UUID uuid2 : plugin.pointSystem.keySet()) {
                    Player player2 = Bukkit.getPlayer(uuid2);
                    if(p != player2) {
                        p.showPlayer(plugin, player2);
                    }
                }
            }
            plugin.messagePlayer(p, "You have left the game.");
            p.getInventory().clear();
            p.getInventory().setContents(plugin.pointSystem.get(p.getUniqueId()).getInventoryContent());
            plugin.pointSystem.remove(p.getUniqueId());
            p.setGameMode(GameMode.SURVIVAL);
            p.teleport(plugin.getConfig().getLocation("craftalot.lobby-location"));
        }
    }


}
