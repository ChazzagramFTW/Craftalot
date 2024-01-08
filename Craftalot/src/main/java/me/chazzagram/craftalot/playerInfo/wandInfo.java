package me.chazzagram.craftalot.playerInfo;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class wandInfo {

    Location corner1;

    Location corner2;

    public wandInfo(Location corner1, Location corner2){
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public Location getCorner1() { return corner1; }
    public Location getCorner2() { return corner2; }

    public void setCorner1(Location corner1) {
        this.corner1 = corner1;
    }
    public void setCorner2(Location corner2) {
        this.corner2 = corner2;
    }
}
