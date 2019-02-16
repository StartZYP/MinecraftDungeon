package com.qq44920040.Minecarft.Listener;

import com.qq44920040.Minecarft.Dungeon;
import com.qq44920040.Minecarft.Model.DungeonWorld;
import com.qq44920040.Minecarft.Utile.DBUtile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;



public class PlayerListener implements Listener {

    private DungeonWorld findgoworld(String GotoWorldName){
        for (DungeonWorld world: Dungeon.Worlds){
            if (world.getWorldName().equalsIgnoreCase(GotoWorldName)){
                return world;
            }
        }
        return null;
    }

    @EventHandler
    public void PlayerLeave(PlayerQuitEvent event){
        UUID playerUUID = event.getPlayer().getUniqueId();
        Dungeon.PlayerDungeonTeamTime.remove(playerUUID);
        event.getPlayer().teleport(Bukkit.getWorld(Dungeon.MainWorld).getSpawnLocation());
    }


    @EventHandler
    public void PlayerChangeWorld(PlayerChangedWorldEvent playerChangedWorldEvent){
        Player p = playerChangedWorldEvent.getPlayer();
        DungeonWorld tempworld = findgoworld(p.getLocation().getWorld().getName());
        UUID PlayerUid =p.getUniqueId();
        if (p.isOp()){
            return;
        }else if (tempworld==null&&Dungeon.PlayerDungeonTeamTime.containsKey(PlayerUid)){
            p.sendMessage(Dungeon.LeaveDungeon);
            Dungeon.PlayerDungeonTeamTime.remove(PlayerUid);
        }else {
            try{
                if (p.getLevel()<Integer.parseInt(tempworld.getWorldLevel())){
                    p.sendMessage(Dungeon.PlayerLevelinsufficient.replace("[PlayerName]",p.getName()).replace("[WorldLevel]",tempworld.getWorldLevel()));
                    p.teleport(Bukkit.getWorld(Dungeon.MainWorld).getSpawnLocation());
                }else {
                    int WorldNumber = Integer.parseInt(tempworld.getWorldNum());
                    String WorldName = tempworld.getWorldName();
                    for (int i=1;i<=100;i++){
                        if (p.hasPermission("Dungeon."+WorldName+".Num."+i)){
                            WorldNumber+=i;
                            break;
                        }
                    }
                    int EnterNum = DBUtile.GetSqliteData(PlayerUid,WorldName);
                    if (!(EnterNum<WorldNumber)){
                        p.sendMessage(Dungeon.PlayerNotNum.replace("[PlayerName]",p.getName()));
                        p.teleport(Bukkit.getWorld(Dungeon.MainWorld).getSpawnLocation());
                    }else {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        calendar.add(Calendar.MINUTE,Integer.parseInt(tempworld.getWorldWatingTime()));
                        Dungeon.PlayerDungeonTeamTime.put(p.getUniqueId(),calendar.getTime());
                        EnterNum++;
                        String EnterMsg =Dungeon.EnterMsg.replace("[DungeonName]",tempworld.getWorldName()).replace("[DungeonNum]",String.valueOf(EnterNum));
                        DBUtile.AddSQLiteData(PlayerUid,tempworld.getWorldName());
                        p.sendMessage(EnterMsg);
                    }
                }
            }catch (NullPointerException e){

            }
        }
    }
}
