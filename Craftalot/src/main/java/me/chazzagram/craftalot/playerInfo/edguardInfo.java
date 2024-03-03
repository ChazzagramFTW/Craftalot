//package me.chazzagram.craftalot.playerInfo;
//
//import me.chazzagram.craftalot.Craftalot;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.EntityType;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.server.PluginDisableEvent;
//import org.bukkit.event.server.PluginEnableEvent;
//import org.bukkit.event.server.ServerLoadEvent;
//import org.bukkit.scheduler.BukkitRunnable;
//import org.bukkit.scheduler.BukkitTask;
//
//public class edguardInfo implements Listener{
//
//    private static edguardInfo instance = null;
//
//    private final Craftalot plugin;
//    public Entity edguard;
//    public BukkitTask timer;
//    private boolean edguardSpawned;
//
//    public edguardInfo(Craftalot plugin) {
//        this.plugin = plugin;
//        this.edguard = null;
//        this.timer = null;
//        this.edguardSpawned = false;
//    }
//
//    public static void initInstance(Craftalot plugin) {
//
//            instance = new edguardInfo(plugin);
//    }
//
//
//    public static edguardInfo getInstance() {
//        return instance;
//    }
//
//    public void spawnEdguard() {
//        if (edguard == null || edguard.isDead()) {
//            edguard = plugin.getConfig().getLocation("craftalot.edguard-location").getWorld().spawnEntity(plugin.getConfig().getLocation("craftalot.edguard-location"), EntityType.VILLAGER);
//
//            edguard.setGravity(false);
//            edguard.setCustomName("§aEdguard");
//            edguard.setCustomNameVisible(true);
//            edguard.setInvulnerable(true);
//
//            edguardSpawned = true;
//
//            timer = new BukkitRunnable() {
//                @Override
//                public void run() {
//                    if (edguard != null && edguardSpawned) {
//                        edguard.teleport(plugin.getConfig().getLocation("craftalot.edguard-location"));
//                    }
//                }
//            }.runTaskTimer(plugin, 0L, 0L);
//        }
//    }
//
//    public void despawnEdguard() {
//        if (edguard != null) {
//            edguard.remove();
//            edguard = null;
//        }
//        if (timer != null) {
//            timer.cancel();
//        }
//        edguardSpawned = false;
//    }
//
//    public boolean isEdguardSpawned() {
//        return edguardSpawned;
//    }
//
//
//    @EventHandler
//    public void onPluginDisable(PluginDisableEvent event) {
//        if (event.getPlugin() == plugin) {
//            if(!Bukkit.getOnlinePlayers().isEmpty()) {
//                if(plugin.getEdguardInfo().isEdguardSpawned()) {
//                    despawnEdguard();
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    public void onPluginEnable(PluginEnableEvent event) {
//        if (event.getPlugin() == plugin) {
//            if(!Bukkit.getOnlinePlayers().isEmpty()) {
//                if (plugin.getConfig().getLocation("craftalot.edguard-location") != null) {
//                    if(!plugin.getEdguardInfo().isEdguardSpawned()) {
//                        plugin.getEdguardInfo().spawnEdguard();
//                    }
//                }
//            }
//        }
//    }
//
//}
