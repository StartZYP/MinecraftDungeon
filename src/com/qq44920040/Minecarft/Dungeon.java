package com.qq44920040.Minecarft;

import com.qq44920040.Minecarft.Listener.PlayerListener;
import com.qq44920040.Minecarft.Model.DungeonWorld;
import com.qq44920040.Minecarft.Utile.DBUtile;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class Dungeon extends JavaPlugin {
    public static Map<UUID, Date> PlayerDungeonTeamTime =new HashMap<>();
    public static ArrayList<DungeonWorld> Worlds = new ArrayList<>();
    public static String MainWorld;
    String InDungeon;
    public static String PlayerLevelinsufficient;
    public static String PlayerNotNum;
    public static String LeaveDungeon;
    public static String EnterMsg;
    private static String InfoMsg;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(),"config.yml");
        if (!(file.exists())){
            saveDefaultConfig();
        }
        Set<String> WorldTempNames = getConfig().getConfigurationSection("Dungeon").getKeys(false);
        for (String WorldName:WorldTempNames){
            System.out.println(WorldName);
            String num = getConfig().getString("Dungeon."+WorldName+".Num");
            String  waiting=getConfig().getString("Dungeon."+WorldName+".WaitingTime");
            String Level = getConfig().getString("Dungeon."+WorldName+".Level");
            System.out.println(num+"\n"+waiting+"\n"+Level+"\n");
            Worlds.add(new DungeonWorld(WorldName,num,waiting,Level));
        }
        PlayerLevelinsufficient = getConfig().getString("MsgConfig.PlayerLevelinsufficient");
        PlayerNotNum = getConfig().getString("MsgConfig.PlayerNotNum");
        InDungeon = getConfig().getString("MsgConfig.InDungeon");
        LeaveDungeon = getConfig().getString("MsgConfig.LeaveMsg");
        EnterMsg = getConfig().getString("MsgConfig.EnterMsg");
        MainWorld = getConfig().getString("MainWorld");
        InfoMsg = getConfig().getString("MsgConfig.InfoMsg");
        System.out.println(Worlds.size());
        if (Worlds.size()>0&&MainWorld.equals("")){
            System.out.println("[Dungeon]加载失败配置项未填写");
        }else {
            System.out.println("[Dungeon]成功加载");
            new DBUtile(getDataFolder().getPath()+ File.separator+"PexTime");
            DungeonTeamThread();
            Bukkit.getPluginManager().registerEvents(new PlayerListener(),this);
        }
        super.onEnable();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("DG")){
            if(args.length==1&&args[0].equalsIgnoreCase("info")&&sender instanceof Player){
                for(DungeonWorld dw:Worlds){
                    int EnterMaxNum=Integer.parseInt(dw.getWorldNum());
                    for (int i=1;i<=100;i++){
                        if (sender.hasPermission("Dungeon."+dw.getWorldName()+".Num."+i)){
                            EnterMaxNum+=i;
                            break;
                        }
                    }
                    int EnterNum = DBUtile.GetSqliteData(((Player) sender).getUniqueId(),dw.getWorldName());
                    sender.sendMessage(InfoMsg.replace("[WorldName]",dw.getWorldName()).replace("[HaveNum]",String.valueOf(EnterMaxNum-EnterNum)));
                }
            }
            if(args.length==1&&args[0].equalsIgnoreCase("reload")){
                saveConfig();
                if (!getDataFolder().exists()) {
                    getDataFolder().mkdir();
                }
                File file = new File(getDataFolder(),"config.yml");
                if (!(file.exists())){
                    saveDefaultConfig();
                }
                Set<String> WorldTempNames = getConfig().getConfigurationSection("Dungeon").getKeys(false);
                for (String WorldName:WorldTempNames){
                    System.out.println(WorldName);
                    String num = getConfig().getString("Dungeon."+WorldName+".Num");
                    String  waiting=getConfig().getString("Dungeon."+WorldName+".WaitingTime");
                    String Level = getConfig().getString("Dungeon."+WorldName+".Level");
                    System.out.println(num+"\n"+waiting+"\n"+Level+"\n");
                    Worlds.add(new DungeonWorld(WorldName,num,waiting,Level));
                }
                PlayerLevelinsufficient = getConfig().getString("MsgConfig.PlayerLevelinsufficient");
                PlayerNotNum = getConfig().getString("MsgConfig.PlayerNotNum");
                InDungeon = getConfig().getString("MsgConfig.InDungeon");
                LeaveDungeon = getConfig().getString("MsgConfig.LeaveMsg");
                EnterMsg = getConfig().getString("MsgConfig.EnterMsg");
                MainWorld = getConfig().getString("MainWorld");
                InfoMsg = getConfig().getString("MsgConfig.InfoMsg");
                System.out.println(Worlds.size());
                if (Worlds.size()>0&&MainWorld.equals("")){
                    System.out.println("[Dungeon]加载失败配置项未填写");
                }
            }
        }
        return super.onCommand(sender, command, label, args);
    }

    private void DungeonTeamThread(){
        new BukkitRunnable(){
            @Override
            public void run() {
                if (!PlayerDungeonTeamTime.isEmpty()){
                    for (Map.Entry<UUID,Date> entry:PlayerDungeonTeamTime.entrySet()){
                        Date PlayerJoinDungeonTime = entry.getValue();
                        Player playertemp = Bukkit.getPlayer(entry.getKey());
                        long JoinTime = PlayerJoinDungeonTime.getTime();
                        long NowTime = new Date().getTime();
                        long LastTime = (JoinTime-NowTime)/1000;
                        if (JoinTime<NowTime){
                            playertemp.teleport(Bukkit.getWorld(MainWorld).getSpawnLocation());
                            PlayerDungeonTeamTime.remove(playertemp.getUniqueId(),PlayerJoinDungeonTime);
                        }else if (LastTime<60){
                            playertemp.sendMessage("§e§l您当前在该副本剩余的时间为: 1分钟");
                        }else if (LastTime<60*5&&LastTime>60*4){
                            playertemp.sendMessage("§e§l您当前在该副本剩余的时间为: 5分钟");
                        }else if (LastTime<60*10&&LastTime>60*9){
                            playertemp.sendMessage("§e§l您当前在该副本剩余的时间为: 10分钟");
                        }
                    }
                }
            }
        }.runTaskTimer(this,0,60*20L);
    }


    @Override
    public void onDisable() {
        super.onDisable();
    }
}
