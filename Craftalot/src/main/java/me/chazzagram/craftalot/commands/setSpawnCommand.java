package me.chazzagram.craftalot.commands;

import me.chazzagram.craftalot.Craftalot;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class setSpawnCommand implements CommandExecutor {

    private final Craftalot plugin;

    public setSpawnCommand(Craftalot plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(sender instanceof Player p){

            Location location = p.getLocation();
            plugin.getConfig().set("spawn", location);
            p.sendMessage("Spawnpoint set.");

            plugin.saveConfig();
        } else {
            System.out.println("You need to be on the server to set the spawnpoint.");
        }

        return true;
    }
}
