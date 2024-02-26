package me.chazzagram.craftalot.playerInfo;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class playerInfo {
    String teamName;
    int points;

    ItemStack itemToCraft;
    ItemStack[] inventory;

    public playerInfo(String teamName, int points, ItemStack itemToCraft, ItemStack[] inventory){
        this.teamName = teamName;
        this.points = points;
        this.itemToCraft = itemToCraft;
        this.inventory = inventory;
    }

    public String getTeamName(){
        return teamName;
    }

    public int getPoints() { return points; }

    public ItemStack getItemToCraft(){ return itemToCraft; }
    public ItemStack[] getInventoryContent(){ return inventory; }

    public void setPoints(int points) { this.points = getPoints() + points; }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setItemToCraft(ItemStack itemToCraft) {
        this.itemToCraft = itemToCraft;
    }
    public void setInventoryContent(ItemStack[] inventory) { this.inventory = inventory; }
}
