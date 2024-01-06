package me.chazzagram.craftalot.playerInfo;

import me.chazzagram.craftalot.Craftalot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class gameRunning {

    private static boolean gameRunning = false;
    private static boolean gamePaused = false;

    protected static BukkitTask timer;

    protected final Craftalot plugin;

    public abstract void count(int current);

    private static int time;

    public gameRunning(boolean gameRunning, int time, Craftalot plugin){
        this.gameRunning = gameRunning;
        this.plugin = plugin;
        this.time = time;
    }

    public static boolean isGameRunning() {
        return gameRunning;
    }

    public static void setGameRunning(boolean gameRunning) {
        me.chazzagram.craftalot.playerInfo.gameRunning.gameRunning = gameRunning;
    }
    public static boolean isGamePaused() {
        return gamePaused;
    }

    public static void setGamePaused(boolean gamePaused) {
        me.chazzagram.craftalot.playerInfo.gameRunning.gamePaused = gamePaused;
    }

    public final void startTimer(){
        timer = new BukkitRunnable() {

            @Override
            public void run() {
                count(time);
                if (time-- <= 0) cancel();
                if (gamePaused) {
                    time++;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
