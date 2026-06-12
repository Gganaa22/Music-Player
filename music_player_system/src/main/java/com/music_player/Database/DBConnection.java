package com.music_player.Database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection{

    private static final String URL = "jdbc:mysql://localhost:3306/gbeat_db";
    private static final String USER = "root";
    private static final String PASSWORD ="Admin123.";

    public static Connection getConnection(){
        try{
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch(Exception e){
            System.out.println("DB Connection Error:"+e.getMessage());
            return null;
        }
    }
}