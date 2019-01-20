package com.qq44920040.Minecarft.Utile;

import java.sql.*;
import java.util.UUID;

public class DBUtile  {
    private static Connection connection = null;
    private static Statement statement = null;
    public DBUtile(String DatabasePath){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+DatabasePath+".db");
            statement = connection.createStatement();
            String sql = "CREATE TABLE IF NOT EXISTS Dungeon " +
                    "(UUid        VARCHAR(200)," +
                    "WorldDungeonName        VARCHAR(100), " +
                    "PexexpireTime DATETIME)";
            statement.executeUpdate(sql);
            statement.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
    }

    public static int GetSqliteData(UUID playeruuid,String worldNmae,String RefreshTime){
        int rowCount = 0;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String sql = "select * from Dungeon where UUid='"+playeruuid+"' and WorldDungeonName='"+worldNmae+"' and PexexpireTime<=datetime('now','localtime','+"+RefreshTime+" hour')";
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                rowCount++;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return rowCount;
    }

    public static void AddSQLiteData(UUID playeruuid, String WorldDungeonName){
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String sql =  "INSERT INTO Dungeon(UUid,WorldDungeonName,PexexpireTime)values('"+playeruuid+"','"+WorldDungeonName+"',datetime('now','localtime'))";
            statement.executeUpdate(sql);
            connection.commit();
            statement.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }


}
