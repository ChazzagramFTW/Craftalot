package me.chazzagram.craftalot.expansion;

import me.chazzagram.craftalot.Craftalot;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

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

        return null;
    }
}