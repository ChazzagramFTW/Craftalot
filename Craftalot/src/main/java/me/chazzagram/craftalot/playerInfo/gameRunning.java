package me.chazzagram.craftalot.playerInfo;

import me.chazzagram.craftalot.Craftalot;
import me.chazzagram.craftalot.commands.craftalotCommand;
import me.chazzagram.craftalot.listeners.craftalotGUIListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class gameRunning {

    private static boolean gameRunning = false;
    private static boolean gamePaused = false;


    protected static BukkitTask timer;

    protected final Craftalot plugin;
    private final craftalotGUIListener listenerInstance;
    private final craftalotCommand commandListener;

    public abstract void count(int current);

    private static int time;

    public gameRunning(boolean gameRunning, int time, Craftalot plugin){
        this.gameRunning = gameRunning;
        this.plugin = plugin;
        this.time = time;
        this.listenerInstance = new craftalotGUIListener(plugin);
        this.commandListener = new craftalotCommand(plugin);
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

    public static int getTimeLeft() { return time; }

    public final void startTimer(){
            timer = new BukkitRunnable() {

            @Override
            public void run() {
                count(time);
                if (time-- <= 0) {
                    listenerInstance.stopGame();
                    cancel();
                }
                if(!gameRunning){
                    cancel();
                }
                if(time % Integer.parseInt(plugin.getConfig().getString("craftalot.material-restock-delay")) == 0){
                    commandListener.restockRegions();
                }
                if (gamePaused) {
                    time++;
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
