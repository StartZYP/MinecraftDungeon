package com.qq44920040.Minecarft.Model;

public class DungeonWorld {
    public DungeonWorld(String Name,String Num,String WatingTime,String Level,String RefreshTime){
        this.WorldName = Name;
        this.WorldNum = Num;
        this.WorldLevel = Level;
        this.WorldRefreshTime = RefreshTime;
        this.WorldWatingTime = WatingTime;
    }

    private String WorldName;
    private String WorldNum;
    private String WorldWatingTime;
    private String WorldLevel;
    private String WorldRefreshTime;



    public String getWorldName() {
        return this.WorldName;
    }

    public String getWorldNum() {
        return this.WorldNum;
    }

    public String getWorldWatingTime() {
        return this.WorldWatingTime;
    }

    public String getWorldLevel() { return this.WorldLevel; }


    public String getWorldRefreshTime() {
        return this.WorldRefreshTime;
    }

}
