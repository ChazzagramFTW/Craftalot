package me.chazzagram.craftalot.expansion;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.playerInfo.gameRunning;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static me.chazzagram.craftalot.listeners.craftalotGUIListener.convertToDisplayName;

public class SpigotExpansion extends PlaceholderExpansion {

    private final Craftalot plugin;

    public SpigotExpansion(Craftalot plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "Chazzagram";
    }

    @Override
    public String getIdentifier() {
        return "craftalot";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        if(p == null){
            return "";
        }
        if(params.equals("version")){
            return Craftalot.getPlugin().getDescription().getVersion();
        }

        if(params.equals("points")){
            if (plugin.pointSystem.containsKey(p.getUniqueId())) {
                return String.valueOf(plugin.pointSystem.get(p.getUniqueId()).getPoints());
            } else {
                return "§oN/A";
            }
        }

        if (params.equals("to_craft")){
            if (plugin.pointSystem.containsKey(p.getUniqueId())) {
                if(plugin.pointSystem.get(p.getUniqueId()).getItemToCraft() != null) {
                    return convertToDisplayName(plugin.pointSystem.get(p.getUniqueId()).getItemToCraft().getType().toString());
                } else {
                    return "§oN/A";
                }
            } else {
                return "§oN/A";
            }
        }

        if(params.equals("timer")){
            if(gameRunning.isGameRunning()) {
                return LocalTime.of(0, gameRunning.getTimeLeft() / 60, gameRunning.getTimeLeft() % 60).format(DateTimeFormatter.ofPattern("mm:ss"));
            } else {
                return "§cNo Game Running.";
            }
        }

        if(params.equals("player_count")){
            if(gameRunning.isGameRunning()){
                return String.valueOf(plugin.pointSystem.size());
            }
        }
        return null;
    }
}