package me.chazzagram.craftalot.playerInfo;

public class playerInfo {
    String teamName;
    int points;

    public playerInfo(String teamName, int points){
        this.teamName = teamName;
        this.points = points;
    }

    public String getTeamName(){
        return teamName;
    }

    public int getPoints(){
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
