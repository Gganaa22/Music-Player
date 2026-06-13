package com.music_player.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import java.net.URL;

import com.music_player.Database.SongDAO;
import com.music_player.Model.Song;

public class MainController {

    @FXML
    private Button btnAddSong;

    @FXML
    private TableColumn<Song, String> colArtist;

    @FXML
    private TableColumn<Song, String> colGenre;

    @FXML
    private TableColumn<Song, String> colTitle;

    @FXML
    private ListView<?> listPlayeList;

    @FXML
    private TableView<Song> tblSongs;

    @FXML
    private TextField txtSearch;


    @FXML
    private Button btnPause;

    @FXML
    private Button btnPlay;

    @FXML
    private Label lblArtist;

    @FXML
    private Label lblSongName;

    @FXML
    private Slider volumeSlider;

    // Global huvisagch
    private MediaPlayer mediaPlayer;
    private Song selectedSong;

    @FXML
    public void initialize() {

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        ObservableList<Song> songs = FXCollections.observableArrayList(SongDAO.getAllSongs());

        tblSongs.setItems(songs);

        // TableView deer duu songoh uyd ajillah heseg 
        tblSongs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedSong = newValue;
                lblSongName.setText(selectedSong.getTitle());
                lblArtist.setText(selectedSong.getArtist());
                
                prepareSong(selectedSong.getFileName());
            }
        });

        // Neg tovchluurt Play/pause logic holboh
        btnPlay.setOnAction(event -> handlePlayPause());

        // Volume Slider-iin utga oorchlogdoh buriig mederch duunii changiig tohiruulh
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (mediaPlayer != null) {
                
                mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
            }
        });
    }

        private void prepareSong(String fileName) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            URL resource = getClass().getResource("/songs/" + fileName);
                    if (resource == null) {
                        System.out.println("Файл олдсонгүй: " + fileName);
                        return;
                    }

            Media media = new Media(resource.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            // Duu duusahad tovchluuriin textiig butsaagaad "▶" bolgoh 
            mediaPlayer.setOnEndOfMedia(() -> {
                btnPlay.setText("▶");
            });

            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
            
            // Shine duu sonsmogts tovchluur "▶" tolovtei baina
            btnPlay.setText("▶");

        } catch (Exception e) {
            System.out.println("Дууг бэлдэхэд алдаа гарлаа: " + e.getMessage());
        }
    }

        private void handlePlayPause() {
        if (mediaPlayer == null) {
            System.out.println("Тоглуулах дуу сонгогдоогүй байна.");
            return;
        }

        // Herev duu odoo togloj baival tur zogsono 
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            btnPlay.setText("▶"); // Товчлуурын дүрсийг Play болгоно
            System.out.println("Түр зогслоо.");
        } 
        // Herev duug zogsson esvel belen baival togluulna 
        else {
            mediaPlayer.play();
            btnPlay.setText("⏸"); // Tovchluuriin dursiig Pause bolgono
            System.out.println("Тоглож байна: " + selectedSong.getTitle());
        }
    }
}


    






