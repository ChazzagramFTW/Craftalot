package me.chazzagram.craftalot.playerInfo;

import org.bukkit.Location;

public class settingsInfo {

    String setting;

    String arg1;

    public settingsInfo(String setting, String arg1){
        this.setting = setting;
        this.arg1 = arg1;
    }

    public String getSetting() { return setting; }
    public String getArg1() { return arg1; }

    public void setSetting(String setting) {
        this.setting = setting;
    }
    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }
}
