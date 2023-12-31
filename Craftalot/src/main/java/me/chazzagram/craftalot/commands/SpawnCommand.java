package me.chazzagram.craftalot.commands;

import me.chazzagram.craftalot.Craftalot;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final Craftalot plugin;

    public SpawnCommand(Craftalot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (commandSender instanceof Player p) {
            Location location = plugin.getConfig().getLocation("spawn");

            if (location != null){
                p.teleport(location);
                p.sendMessage("Teleporting to spawn.");
            } else {
                System.out.println("No spawn point is set. Use /setspawn to set it.");
            }


        }

        return true;
    }
}
