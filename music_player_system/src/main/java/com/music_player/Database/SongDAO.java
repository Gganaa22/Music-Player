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

    public static boolean insertSong(Song song) {
        String sql = "INSERT INTO songs (title, artist, genre, file_name, play_count, favorite) VALUES (?, ?, ?, ?, 0, false)";
        
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            
            ps.setString(1, song.getTitle());
            ps.setString(2, song.getArtist());
            ps.setString(3, song.getGenre());
            ps.setString(4, song.getFileName());
            
            int rowsInserted = ps.executeUpdate();
            return rowsInserted > 0; // Herev database ruu amjilttai orson bl true butsaana
            
        } catch (Exception e) {
            System.out.println("Бааз руу дуу нэмэхэд алдаа гарлаа: " + e.getMessage());
            return false;
        }
    }

    // Duunii favorite toloviig database deer shinechlh function
    public static boolean updateFavoriteStatus(int songId, boolean isFavorite) {
        String sql = "UPDATE songs SET favorite = ? WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, isFavorite);
            ps.setInt(2, songId);
            
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (Exception e) {
            System.out.println("Дуртай дууны төлөв өөрчлөхөд алдаа: " + e.getMessage());
            return false;
        }
    }


    // Duug id-gaar ni database-ees ustgah 
    public static boolean deleteSong(int songId) {
        String sql = "DELETE FROM songs WHERE id = ?";
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, songId);
            
            int rowsDeleted = ps.executeUpdate();
            return rowsDeleted > 0; // Amjilttai ustgasan bol true butsaana
        } catch (Exception e) {
            System.out.println("Дуу устгахад алдаа гарлаа: " + e.getMessage());
            return false;
        }
    }

}