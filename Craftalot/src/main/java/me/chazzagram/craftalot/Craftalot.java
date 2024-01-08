package me.chazzagram.craftalot;

import me.chazzagram.craftalot.commands.*;
import me.chazzagram.craftalot.expansion.SpigotExpansion;
import me.chazzagram.craftalot.files.CraftlistConfig;
import me.chazzagram.craftalot.listeners.*;
import me.chazzagram.craftalot.playerInfo.playerInfo;
import me.chazzagram.craftalot.playerInfo.wandInfo;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class Craftalot extends JavaPlugin implements Listener {


    private static Craftalot plugin;

    private static Craftalot instance;

    public static Craftalot getInstance(){
        return instance;
    }

    public HashMap<UUID, playerInfo> pointSystem;
    public HashMap<UUID, wandInfo> wandSystem;


    @Override
    public void onEnable() {
        // Plugin startup logic

        this.pointSystem = new HashMap<>();

        this.wandSystem = new HashMap<>();

        instance = this;

        plugin = this;

        messageConsole("My first plugin has started, hello.");

        getServer().getPluginManager().registerEvents(new XPBottleBreakListener(), this);
        getServer().getPluginManager().registerEvents(new ShearSheepListener(), this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new SpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new craftalotGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new edguardListener(this), this);
        getServer().getPluginManager().registerEvents(new wandListener(this), this);

        getCommand("god").setExecutor(new GodCommand());
        getCommand("repeat").setExecutor(new RepeatCommand());
        getCommand("setspawn").setExecutor(new setSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("spawnonjoin").setExecutor(new spawnOnJoinCommand(this));
        getCommand("craftalot").setExecutor(new craftalotCommand(this));

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getPluginManager().registerEvents(this, this);
            new SpigotExpansion(this).register();
        }
        // config
        CraftlistConfig.setup();
        CraftlistConfig.get().addDefault("craftlist", "");
        CraftlistConfig.get().options().copyDefaults();
        CraftlistConfig.save();

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        craftalotCommand.spawnEdguard();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        messageConsole("My first plugin has stopped, bye.");

        craftalotCommand.despawnEdguard();

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
    public void onLeaveBed(PlayerBedLeaveEvent event){

        Player player = event.getPlayer();
        player.sendMessage("You left a bed. Dork. Kill Yourself.");
        player.setHealth(0);
    }



}
