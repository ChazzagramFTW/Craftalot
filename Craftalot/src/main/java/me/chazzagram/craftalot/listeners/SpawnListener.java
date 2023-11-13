package me.chazzagram.craftalot.listeners;

import me.chazzagram.craftalot.Craftalot;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class SpawnListener implements Listener {

    private final Craftalot plugin;

    public SpawnListener(Craftalot plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){

        Player p = e.getPlayer();

        if(plugin.getConfig().get("spawnOnJoin") == "true"){

            Location location = plugin.getConfig().getLocation("spawn");

            if (location != null){
                p.teleport(location);
                p.sendMessage("Teleporting to spawn.");
            }

        }
        e.setJoinMessage("Welcome to the server you big dummy.");
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e){
        Location location = plugin.getConfig().getLocation("spawn");
        if(location != null){
            e.setRespawnLocation(location);
        }
    }
}
