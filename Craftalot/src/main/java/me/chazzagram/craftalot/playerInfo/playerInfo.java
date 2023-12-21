package me.chazzagram.craftalot.playerInfo;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class playerInfo {
    String teamName;
    int points;

    ItemStack itemToCraft;

    public playerInfo(String teamName, int points, ItemStack itemToCraft){
        this.teamName = teamName;
        this.points = points;
        this.itemToCraft = itemToCraft;
    }

    public String getTeamName(){
        return teamName;
    }

    public int getPoints() { return points; }

    public ItemStack getItemToCraft(){ return itemToCraft; }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setItemToCraft(ItemStack itemToCraft) {
        this.itemToCraft = itemToCraft;
    }
}
