package me.chazzagram.learnjava.commands;

import me.chazzagram.learnjava.LearnJava;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class spawnOnJoinCommand implements CommandExecutor {

    private final LearnJava plugin;

    public spawnOnJoinCommand(LearnJava plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(plugin.getConfig().getString("spawnOnJoin") == "true"){
            plugin.getConfig().set("spawnOnJoin", "false");
            commandSender.sendMessage("Players won't be sent to spawn upon joining.");
        } else {
            plugin.getConfig().set("spawnOnJoin", "true");
            commandSender.sendMessage("Players will now be sent to spawn upon joining.");
        }


        return true;
    }
}
