package me.chazzagram.craftalot.commands;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.listeners.craftalotGUIListener;
import me.chazzagram.craftalot.playerInfo.gameRunning;
import me.chazzagram.craftalot.playerInfo.playerInfo;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class playerCommand implements CommandExecutor {

    private static Craftalot plugin;

    public playerCommand(Craftalot plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (commandSender instanceof Player p) {

            if (args.length == 0) {
                plugin.messagePlayer(p, "Type '/craftalot help' for the full command list.");

            } else {
                switch (args[0].toLowerCase()) {
                    case "join":
                        if(gameRunning.isGameRunning()) {
                            if (!plugin.pointSystem.containsKey(p.getUniqueId())) {
                                plugin.messagePlayer(p, "You have joined the game!");
                                plugin.pointSystem.put(p.getUniqueId(), new playerInfo("noTeam", 0, null, p.getInventory().getContents()));
                                p.getInventory().clear();
                                plugin.messagePlayer(p,"Your info:\nTeam: " + plugin.pointSystem.get(p.getUniqueId()).getTeamName() + "\nPoints: " + plugin.pointSystem.get(p.getUniqueId()).getPoints());
                                p.teleport(plugin.getConfig().getLocation("craftalot.lobby-location"));
                            } else {
                                plugin.messagePlayer(p, "You are already playing, use /ca leave to leave the game.");
                            }
                        } else {
                            plugin.messagePlayer(p, "There is currently no game being played.");
                        }
                        break;
                    case "leave":
                        if(gameRunning.isGameRunning()) {
                            if (plugin.pointSystem.containsKey(p.getUniqueId())) {
                                plugin.messagePlayer(p, "You have left the game.");
                                p.getInventory().clear();
                                p.getInventory().setContents(plugin.pointSystem.get(p.getUniqueId()).getInventoryContent());
                                plugin.pointSystem.remove(p.getUniqueId());
                                p.setGameMode(GameMode.SURVIVAL);
                                p.teleport(plugin.getConfig().getLocation("craftalot.lobby-location"));
                            } else {
                                plugin.messagePlayer(p, "You are not playing, use /ca join to join the game.");
                            }
                        } else {
                            plugin.messagePlayer(p, "There is currently no game being played.");
                        }
                        break;
                }
            }
        }
        return true;
    }
}
