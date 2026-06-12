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
    }

        private void prepareSong(String fileName) {
            
    }

        private void handlePlayPause() {

    }
}


    






