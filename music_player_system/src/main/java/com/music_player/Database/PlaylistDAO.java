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
}