package me.chazzagram.craftalot.playerInfo;

import me.chazzagram.craftalot.Craftalot;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class edguardInfo implements Listener{

    private static edguardInfo instance;

    private final Craftalot plugin;
    private Entity edguard;
    private BukkitTask timer;
    private boolean edguardSpawned;

    public edguardInfo(Craftalot plugin) {
        this.plugin = plugin;
        this.edguard = null;
        this.timer = null;
        this.edguardSpawned = false;
    }

    public static void initInstance(Craftalot plugin) {
        if (instance == null) {
            instance = new edguardInfo(plugin);
        }
    }


    public static edguardInfo getInstance() {
        return instance;
    }

    public void spawnEdguard() {
        if (edguard == null || edguard.isDead()) {
            edguard = plugin.getConfig().getLocation("craftalot.edguard-location").getWorld().spawnEntity(plugin.getConfig().getLocation("craftalot.edguard-location"), EntityType.VILLAGER);

            edguard.setGravity(false);
            edguard.setCustomName("§aEdguard");
            edguard.setCustomNameVisible(true);
            edguard.setInvulnerable(true);

            timer = new BukkitRunnable() {
                @Override
                public void run() {
                    if (edguard != null && edguardSpawned) {
                        edguard.teleport(plugin.getConfig().getLocation("craftalot.edguard-location"));
                    } else {
                        cancel();
                        edguard.remove();
                        despawnEdguard();
                    }
                }
            }.runTaskTimer(plugin, 0L, 0L);
        }
        edguardSpawned = true;
    }

    public void despawnEdguard() {
        if (edguard != null && edguard.isValid()) {
            edguard.remove();
        }
        if (timer != null && !timer.isCancelled()) {
            timer.cancel();
        }
        edguardSpawned = false;
    }

    public boolean isEdguardSpawned() {
        return edguardSpawned;
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (edguardSpawned) {
            despawnEdguard();
        }
    }
}
