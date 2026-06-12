package com.music_player.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.music_player.Model.Song;

public class SongDAO {

    public static List<Song> getAllSongs(){
        List<Song> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM songs";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Song s = new Song();
                s.setId(rs.getInt("id"));
                s.setTitle(rs.getString("title"));
                s.setArtist(rs.getString("artist"));
                s.setGenre(rs.getString("genre"));
                s.setFileName(rs.getString("file_name"));
                s.setPlayCount(rs.getInt("play_count"));
                s.setFavorite(rs.getBoolean("favorite"));

                list.add(s);
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return list;
    }

}