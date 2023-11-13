package me.chazzagram.craftalot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RepeatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if(commandSender instanceof Player p){

            if(args.length == 0){
                p.sendMessage("You did not provide any arguements.");
                p.sendMessage("Example: /repeat <message here>");
            }else if(args.length == 1){
                String word = args[0];

                p.sendMessage("Message: " + word);
            }else{
                StringBuilder builder = new StringBuilder();

                for (String arg : args) {
                    builder.append(arg);
                    builder.append(" ");
                }

                String finalMessage = builder.toString();
                finalMessage = finalMessage.stripTrailing();
                p.sendMessage("Message (Spaces Included): " + finalMessage);
            }
        }


        return true;
    }
}
