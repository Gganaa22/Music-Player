package com.music_player.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDAO {

    // Shine playlist databse ruu nemh
    public static boolean createPlaylist(String name) {
        String sql = "INSERT INTO playlists (playlist_name) VALUES (?)";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Плейлист үүсгэхэд алдаа: " + e.getMessage());
            return false;
        }
    }

    //Buh playlistiin neriig database ees unshina
    public static List<String> getAllPlaylists() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT playlist_name FROM playlists";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("playlist_name"));
            }
        } catch (Exception e) {
            System.out.println("Плейлист уншихад алдаа: " + e.getMessage());
        }
        return list;
    }

// Playlist ruu holboj hadgalah function
    public static boolean addSongToPlaylist(String playlistName, int songId) {
        // Ehleed playlistiin nereer id-g ni olj avna
        String getPlaylistIdSql = "SELECT id FROM playlists WHERE playlist_name = ?";
        String insertSql = "INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)";
        
        try {
            Connection conn = DBConnection.getConnection();
            
            // Playlist id avah 
            PreparedStatement ps1 = conn.prepareStatement(getPlaylistIdSql);
            ps1.setString(1, playlistName);
            ResultSet rs = ps1.executeQuery();
            
            if (rs.next()) {
                int playlistId = rs.getInt("id");
                
                // Dundiin husnegt ruu hadgalah
                PreparedStatement ps2 = conn.prepareStatement(insertSql);
                ps2.setInt(1, playlistId);
                ps2.setInt(2, songId);
                
                return ps2.executeUpdate() > 0;
            }
        } catch (Exception e) {
            System.out.println("Плейлист рүү дуу нэмэхэд алдаа: " + e.getMessage());
        }
        return false;
    }
}