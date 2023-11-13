package me.chazzagram.learnjava.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player p){
            if(p.hasPermission("lj.god")){

                if(args.length == 0){
                    if(p.isInvulnerable()){
                        p.setInvulnerable(false);
                        p.sendMessage(ChatColor.RED + "Godmode disabled.");
                    }else{
                        p.setInvulnerable(true);
                        p.sendMessage(ChatColor.GREEN + "Godmode enabled.");
                    }
                }else if(args.length == 1){
                    String playerName = args[0];
                    Player target = Bukkit.getServer().getPlayerExact(playerName);
                    if(target == null){
                        p.sendMessage("Player is not online.");
                    }else{
                        if(target.isInvulnerable()){
                            target.setInvulnerable(false);
                            p.sendMessage("§6You have §cdisabled §6" + target.getDisplayName() + "'s god mode.");
                            target.sendMessage(ChatColor.RED + "Godmode disabled.");
                        }else{
                            target.setInvulnerable(true);
                            p.sendMessage("§6You have §aenabled §6" + target.getDisplayName() + "'s god mode.");
                            target.sendMessage(ChatColor.GREEN + "Godmode enabled.");
                        }
                    }
                }
            }else{
                p.sendMessage("You do not have permission to run this command.");
            }
        }


        return true;
    }
}
