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

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

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

    @FXML 
    private Slider progressSlider;

    @FXML
    private Label lblTime;

    // Global huvisagch
    private MediaPlayer mediaPlayer;
    private Song selectedSong;

    @FXML
    public void initialize() {

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        // 1.Baazaas buh duug unshij undsen jagsaaltand avna
        ObservableList<Song> songs = FXCollections.observableArrayList(SongDAO.getAllSongs());

        // 2.Undsen jagsaaltiig FilteredList-eer orooj shuuhed belen bolgono
        FilteredList<Song> filteredData = new FilteredList<>(songs, p -> true);

        // 3. txtSearch deer text bich burt ajillah logic 
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(song -> {

                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (song.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } 
                
                else if (song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                }
                
                return false; 
            });
        });

        // 4. Shuugdsen ogogdliig TableView-tei holboj, Sortloh bolomjiig olgono
        SortedList<Song> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblSongs.comparatorProperty());
        
        tblSongs.setItems(sortedData);

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

        // Hereglegch Progress Slider-iig mouse aaar hodolgoh uyed duug ter heseg ruu guigene
        progressSlider.setOnMousePressed(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });

        progressSlider.setOnMouseReleased(event -> {
            if (mediaPlayer != null) {
                // Slider-iin baigaa second ruu shiljuulne
                mediaPlayer.seek(javafx.util.Duration.seconds(progressSlider.getValue()));
                mediaPlayer.play(); 
                btnPlay.setText("⏸");
            }
        });

        // Duu nemeh tovchluur darahad shine tsonh neeh 
        btnAddSong.setOnAction(event ->{
            try{
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/music_player/AddSongView.fxml"));
                javafx.scene.Parent root = loader.load();

                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setTitle("Шинэ дуу нэмэх");
                stage.setScene(new javafx.scene.Scene(root));

                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                stage.showAndWait();

                ObservableList<Song> refreshedSongs = FXCollections.observableArrayList(SongDAO.getAllSongs());
                tblSongs.setItems(refreshedSongs);

            } catch(Exception e){
                System.out.println("Шинэ цонх нээхэл алдаа гарлаа: "+ e.getMessage());
                e.printStackTrace();
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

            // Duu buren achaalgdaj duusaad niit hugatsaa ni todorhoi boloh uyd ajillana
            mediaPlayer.setOnReady(() -> {
                double totalSeconds = mediaPlayer.getTotalDuration().toSeconds();
                progressSlider.setMax(totalSeconds); 
            });

            // Duu togloj baih uyd second tutamd ajillaj baih toologch (Listener)
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (!progressSlider.isValueChanging()) {
                    progressSlider.setValue(newValue.toSeconds()); 
                }
                
                // Text hugatsaag shinechleh
                if (mediaPlayer.getTotalDuration() != null) {
                    lblTime.setText(formatTime(newValue, mediaPlayer.getTotalDuration()));
                }
            });

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
            btnPlay.setText("▶");
            System.out.println("Түр зогслоо.");
        } 
        // Herev duug zogsson esvel belen baival togluulna 
        else {
            mediaPlayer.play();
            btnPlay.setText("⏸"); // Tovchluuriin dursiig Pause bolgono
            System.out.println("Тоглож байна: " + selectedSong.getTitle());
        }
    }

    // Secondiig  00:00 / 00:00 helbert oruulh function
    private String formatTime(javafx.util.Duration elapsed, javafx.util.Duration total) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed % 60;

        int intTotal = (int) Math.floor(total.toSeconds());
        int totalMinutes = intTotal / 60;
        int totalSeconds = intTotal % 60;

        return String.format("%02d:%02d / %02d:%02d", 
                elapsedMinutes, elapsedSeconds, totalMinutes, totalSeconds);
    }
}


    






