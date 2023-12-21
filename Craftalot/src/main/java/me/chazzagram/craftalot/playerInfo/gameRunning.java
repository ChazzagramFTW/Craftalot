package me.chazzagram.craftalot.playerInfo;

import org.bukkit.inventory.ItemStack;

public class gameRunning {

    private static boolean gameRunning = false;

    public gameRunning(boolean gameRunning){
        this.gameRunning = gameRunning;
    }

    public static boolean isGameRunning() {
        return gameRunning;
    }

    public static void setGameRunning(boolean gameRunning) {
        me.chazzagram.craftalot.playerInfo.gameRunning.gameRunning = gameRunning;
    }
}
