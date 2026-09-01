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
import java.util.List;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import com.music_player.Database.PlaylistDAO;
import com.music_player.Database.SongDAO;
import com.music_player.Model.Song;

import javafx.scene.image.ImageView;

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
    private ListView<String> listPlayeList;

    @FXML
    private TableView<Song> tblSongs;

    @FXML
    private TextField txtSearch;

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

    private boolean isShuffle = false;

    private ObservableList<Song> songs = FXCollections.observableArrayList(); 
    private FilteredList<Song> filteredData;


    @FXML 
    private Button btnFavorite;

    @FXML
    private Button btnDelete;

    @FXML 
    private Button btnAllSongs;
    @FXML 
    private Button btnShowFavorites;

    @FXML 
    private Button btnCreatePlaylist;

    @FXML 
    private Button btnAddToPlaylist;

    @FXML 
    private Button btnRemoveFromPlaylist; 

    @FXML
    private Button btnShuffle;

    @FXML 
    private Button btnPrevious;
    @FXML 
    private Button btnNext;

    @FXML 
    private ImageView imgDisc; 

    private javafx.animation.RotateTransition rotateTransition; //erguulah animats hadgalah huvisagch

    @FXML
    private Button btnStop;

    private String currentMode = "ALL";
    
    @FXML 
    private Button btnDeletePlaylist;
    

    @FXML
    public void initialize() {

        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colGenre.setCellValueFactory(new PropertyValueFactory<>("genre"));

        // 1.global jagsaalt ruugaa datag achaallna
            songs.addAll(SongDAO.getAllSongs());

        // 2. Global filteredData-g onooj ogno 
        filteredData = new FilteredList<>(songs, p -> true);

        // 3. txtSearch deer text bich burt ajillah logic 
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(song -> {

                //Ehleed odoogiin gorimd Эхлээд одоогийн горимд (Playlist, Favorite, All) niitsej baigaag shalgana 
                boolean matchesMode = false;


                if ("FAVORITES".equals(currentMode)) {
                    matchesMode = song.isFavorite();
                } else if ("PLAYLIST".equals(currentMode)) {
                    String selectedPlaylist = listPlayeList.getSelectionModel().getSelectedItem();
                    if (selectedPlaylist != null) {
                        List<Integer> allowedSongIds = PlaylistDAO.getSongIdsInPlaylist(selectedPlaylist);
                        matchesMode = allowedSongIds.contains(song.getId());
                    }
                } else {
                    matchesMode = true; // ALL gorim
                }

                // Herev tuhain playlist/durtai duund baihgui bol shuud false 
                if (!matchesMode) return false;

                //Herev textiin hailt hooson bol odoogiin gorimiin duug haruulna 
                if (newValue == null || newValue.isEmpty() || newValue.trim().isEmpty()) {
                    return true;
                }

                //Textiin hailtiig shalgana 
                String lowerCaseFilter = newValue.toLowerCase().trim();
                return song.getTitle().toLowerCase().contains(lowerCaseFilter) ||
                song.getArtist().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // 4. Shuugdsen ogogdliig TableView-tei holboj, Sortloh bolomjiig olgono
        SortedList<Song> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblSongs.comparatorProperty());
        
        tblSongs.setItems(sortedData);

        // TableView deer duu songoh uyd ajillah heseg 
        tblSongs.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != selectedSong) {

                //oor duu songogdson tul ajillaj baisan hogjmiig buren zogsooj tseverlene
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose(); //Sanah oig buren choloolno
                    mediaPlayer = null;
                }

                // UI elementuudiig zogsoono 
                btnPlay.setText("▶"); 
                if (rotateTransition != null) {
                    rotateTransition.pause();
                    imgDisc.setRotate(0); 
                }

                selectedSong = newValue;
                lblSongName.setText(selectedSong.getTitle());
                lblArtist.setText(selectedSong.getArtist());

                // Songoson duu favorite mon esehees hamaarch tovchnii ongiig solino
                if (selectedSong.isFavorite()) {
                    btnFavorite.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a4d5c, #008080, #00ffff);" + "-fx-text-fill: red;");
                } else {
                    btnFavorite.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a4d5c, #008080, #00ffff);" + "-fx-text-fill: white;");
                }
                
                prepareSong(selectedSong.getFileName());
            }
        });

        // Neg tovchluurt Play/pause logic holboh
        btnPlay.setOnAction(event -> handlePlayPause());


        // Daraagiin duunii tovchluur
        btnNext.setOnAction(event -> {
            int currentIndex = tblSongs.getSelectionModel().getSelectedIndex();
            int nextIndex;

            // herev Shuffle idevhtei bol sanamsargui duu ruu shiljine 
            if (isShuffle && tblSongs.getItems().size() > 1) {
                java.util.Random random = new java.util.Random();
                do {
                    nextIndex = random.nextInt(tblSongs.getItems().size());
                } while (nextIndex == currentIndex);
            } else {
                // Shuffle idevhgui bol daraagiin duu ruu shiljine
                nextIndex = currentIndex + 1;
            }

            if (nextIndex < tblSongs.getItems().size()) {
                tblSongs.getSelectionModel().select(nextIndex);
            } else {
                System.out.println("Жагсаалт дууссан тул эхнээс нь эхлүүллээ.");
                tblSongs.getSelectionModel().select(0); 
            }

            if (mediaPlayer != null) {
                mediaPlayer.play();
                btnPlay.setText("⏸"); 
                rotateTransition.play(); 
            }
        });

        // Omnoh duu tovchluur
        btnPrevious.setOnAction(event -> {
            int currentIndex = tblSongs.getSelectionModel().getSelectedIndex();
            
            //Omnoh duunii index 0 ees ih baih ystoi 
            int prevIndex = currentIndex - 1;

            if (prevIndex >= 0) {
                tblSongs.getSelectionModel().select(prevIndex); // TableView deer omnoh duug songoh
            } else {
                int lastIndex = tblSongs.getItems().size() - 1;
                
                if (lastIndex >= 0) {
                    System.out.println("Жагсаалтын эхэнд хүрсэн тул хамгийн сүүлчийн дуу руу шилжлээ.");
                    tblSongs.getSelectionModel().select(lastIndex);
                }
            }

            if (mediaPlayer != null) {
                mediaPlayer.play();
                btnPlay.setText("⏸"); 
                rotateTransition.play(); 
            }
        });

        // Shuffle tovchluuriin logic
        btnShuffle.setOnAction(event -> {
            isShuffle = !isShuffle; // Toloviig solino(true -> false / false -> true)
            if (isShuffle) {
                btnShuffle.setStyle( "-fx-background-color: linear-gradient(to bottom right, #0a4d5c, #008080, #00ffff);" + "-fx-text-fill: black;" + "-fx-font-weight: bold;"); // Idevhtei uyd nogoon
                System.out.println("Санамсаргүй тоглуулах (Shuffle) горим идэвхжлээ.");
            } else {
                btnShuffle.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a4d5c, #008080, #00ffff);" + "-fx-text-fill: white;"); //Idevhgui bol har 
                System.out.println("Дарааллаар тоглуулах горимд шилжлээ.");
            }
        });

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

                songs.clear();
                songs.addAll(SongDAO.getAllSongs());

            } catch(Exception e){
                System.out.println("Шинэ цонх нээхэл алдаа гарлаа: "+ e.getMessage());
                e.printStackTrace();
            }
        });

        
        btnFavorite.setOnAction(event -> handleFavorite());
        btnDelete.setOnAction(event -> handleDelete()); 
        
        // "Бүх дуунууд" tovchiig darahad shuultuuriig arilgaj buh duug haruulna
        btnAllSongs.setOnAction(event -> {
            currentMode = "ALL";
            listPlayeList.getSelectionModel().clearSelection();
            txtSearch.clear();
            filteredData.setPredicate(song -> true);
            System.out.println("Бүх дууны жагсаалт.");
        });

        // "Дуртай дуунууд" tovchiig darahad zovhon favorite=true duunuudiig shuuj haruulna 
        btnShowFavorites.setOnAction(event -> {
            currentMode = "FAVORITES";
            listPlayeList.getSelectionModel().clearSelection();//playlist songoltiig arilgah
            filteredData.setPredicate(song -> {
                return song.isFavorite(); 
            });
            System.out.println("Зөвхөн дуртай дуунуудыг шүүж харууллаа ");
        });



        // Ajillj ehlhd databased baigaa playlistuudiig listView deer gargana
        ObservableList<String> playlists = FXCollections.observableArrayList(com.music_player.Database.PlaylistDAO.getAllPlaylists());
        listPlayeList.setItems(playlists);

        //playlist deer darah uyd husnegtiig shuuj haruulah 
        listPlayeList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentMode = "PLAYLIST";
                txtSearch.clear(); //playlist songoh uyd hailtiin text iig tseverlne

                // Databasees tuhain playlisted hamaarah duunii ID-nuudiig shuuj avna
                java.util.List<Integer> allowedSongIds = com.music_player.Database.PlaylistDAO.getSongIdsInPlaylist(newValue);
                
                //Husnegt deer zovhon ter ID-tai duunuudiig uldeej shuultuur tavina  
                filteredData.setPredicate(song -> {
                    return allowedSongIds.contains(song.getId());
                });
                
                System.out.println("'" + newValue + "' плейлистийн дуунуудыг харуулж байна.");
            }
        });

        // "+ Playlist" tovchluur darahad jijig tsonh(textInputDialog) gargaj ner avna
        btnCreatePlaylist.setOnAction(event -> {
            javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
            dialog.setTitle("Шинэ плейлист");
            dialog.setHeaderText(null);
            dialog.setContentText("Плейлистийн нэр:");

            java.util.Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                if (!name.trim().isEmpty()) {
                    boolean success = com.music_player.Database.PlaylistDAO.createPlaylist(name.trim());
                    if (success) {
                        System.out.println("Плейлист амжилттай үүслээ: " + name);
                        
                        playlists.clear();
                        playlists.addAll(com.music_player.Database.PlaylistDAO.getAllPlaylists());
                    } else {
                        System.out.println("Плейлист үүсгэж чадсангүй (Магадгүй нэр давхардсан).");
                    }
                }
            });
        });


        //  "Add to Playlist" darahad ajillna
        btnAddToPlaylist.setOnAction(event -> {
            // husnegtees songogdson duug avah
            Song selected = tblSongs.getSelectionModel().getSelectedItem();
            if (selected == null) {
                System.out.println("Плейлист рүү нэмэх дуугаа сонгоно уу!");
                
                // duu oruulaagui bol alert haruulna
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Анхааруулга");
                alert.setHeaderText(null);
                alert.setContentText("Уучлаарай, эхлээд хүснэгтээс плейлист рүү нэмэх дуугаа сонгоно уу.");
                alert.showAndWait();
                return;
            }

            // Database ees odoo baigaa buh playlist iig unshij avah 
            java.util.List<String> allPlaylists = com.music_player.Database.PlaylistDAO.getAllPlaylists();

            if (allPlaylists.isEmpty()) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Мэдээлэл");
                alert.setHeaderText(null);
                alert.setContentText("Одоогоор ямар нэгэн плейлист үүсээгүй байна. Эхлээд зүүн талын '+ Playlist' товчоор плейлист үүсгэнэ үү.");
                alert.showAndWait();
                return;
            }

            //Playlist songoh tsonh (ChoiceDialog) uusgene
            javafx.scene.control.ChoiceDialog<String> dialog = new javafx.scene.control.ChoiceDialog<>(allPlaylists.get(0), allPlaylists);
            dialog.setTitle("Плейлист сонгох");
            dialog.setHeaderText("'" + selected.getTitle() + "' дууг аль плейлист рүү нэмэх вэ?");
            dialog.setContentText("Плейлистүүд:");

            // hereglegchiin songoltiig huleeh
            java.util.Optional<String> result = dialog.showAndWait();
            result.ifPresent(playlistName -> {
                // Songoson playlist ruu duug database ruu hadgalah
                boolean success = com.music_player.Database.PlaylistDAO.addSongToPlaylist(playlistName, selected.getId());
                if (success) {
                    javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                    alert.setTitle("Амжилттай");
                    alert.setHeaderText(null);
                    alert.setContentText("'" + selected.getTitle() + "' дуу '" + playlistName + "' плейлист рүү амжилттай нэмэгдлээ.");
                    alert.showAndWait();

                    //Herev hereglegch odoo yg ter nemsen playlist deeree zogsoj baival tableView-iig shuud shinechilne 
                    String currentSelectedPlaylist = listPlayeList.getSelectionModel().getSelectedItem();
                    if (currentSelectedPlaylist != null && currentSelectedPlaylist.equals(playlistName)) {
                        final java.util.List<Integer> allowedSongIds = com.music_player.Database.PlaylistDAO.getSongIdsInPlaylist(playlistName);
                        filteredData.setPredicate(song -> allowedSongIds.contains(song.getId()));}
                    } else {
                    System.out.println("Дууг нэмэхэд алдаа гарлаа эсвэл бааз руу орж чадсангүй.");
                }
            });
        });


        // "Remove from Playlist" darahad ajillana 
        btnRemoveFromPlaylist.setOnAction(event -> {
            // Husnegtees songogdson duug avah 
            Song selected = tblSongs.getSelectionModel().getSelectedItem();
            // Zuun taliin jagsaaltaas songogdson playlist iig avah
            String currentSelectedPlaylist = listPlayeList.getSelectionModel().getSelectedItem();

            if (selected == null) {
                System.out.println("Плейлистээс хасах дуугаа сонгоно уу!");
                return;
            }

            if (currentSelectedPlaylist == null) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
                alert.setTitle("Анхааруулга");
                alert.setHeaderText(null);
                alert.setContentText("Та эхлээд зүүн талын жагсаалтаас плейлистээ сонгоод, дараа нь хасах дуугаа сонгоно уу.");
                alert.showAndWait();
                return;
            }

            //Ustgah uu gej asuuh alert
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Плейлистээс хасах");
            alert.setHeaderText(null);
            alert.setContentText("'" + selected.getTitle() + "' дууг '" + currentSelectedPlaylist + "' плейлистээс хасахдаа итгэлтэй байна уу?");

            java.util.Optional<javafx.scene.control.ButtonType> confirmResult = alert.showAndWait();
            if (confirmResult.isPresent() && confirmResult.get() == javafx.scene.control.ButtonType.OK) {
                // Database ees hasah function iig duudah 
                boolean success = com.music_player.Database.PlaylistDAO.removeSongFromPlaylist(currentSelectedPlaylist, selected.getId());
                
                if (success) {
                    System.out.println("Дууг плейлистээс амжилттай хаслаа.");
                    
                    //TableView iig shinechilne
                    final java.util.List<Integer> allowedSongIds = com.music_player.Database.PlaylistDAO.getSongIdsInPlaylist(currentSelectedPlaylist);
                    filteredData.setPredicate(song -> allowedSongIds.contains(song.getId()));
                } else {
                    System.out.println("Дууг хасахад алдаа гарлаа.");
                }
            }
        });

        // Ergelddg animatsiin tohirgoo
        rotateTransition = new javafx.animation.RotateTransition(javafx.util.Duration.seconds(5), imgDisc); // 5 secondnd 1 buten ergene
        rotateTransition.setByAngle(360); // 360 gradus ergene
        rotateTransition.setCycleCount(javafx.animation.Animation.INDEFINITE); // Hyzgaargui urgeljilne
        rotateTransition.setInterpolator(javafx.animation.Interpolator.LINEAR); // hurd ni jigd

        // Zurgiin hemjeeg todorhoi zaaj ogoh
        imgDisc.setFitWidth(150);
        imgDisc.setFitHeight(150);
        imgDisc.setPreserveRatio(false); // Dorvoljin helbertei bolgoj shahah

        // Yg gold ni 75 radiustai toirog uusgeh 
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(75, 75, 75);
        imgDisc.setClip(clip);


        // Бүрэн зогсоох (Stop) товчлуурын логик
        btnStop.setOnAction(event -> handleStop());

        btnDeletePlaylist.setOnAction(event -> handleDeletePlaylist());
        
    }

        private void prepareSong(String fileName) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
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

            // Duu duusahad daraagiin duu avtomataar togluulh
            mediaPlayer.setOnEndOfMedia(() -> {
                // Odoo idevhtei baigaa duunii indexiig TableView ees olj avah 
                int currentIndex = tblSongs.getSelectionModel().getSelectedIndex();
                int nextIndex = currentIndex + 1;


                // Herev Shuffle idevhtei baival sanamsargui index songono 
                if (isShuffle && tblSongs.getItems().size() > 1) {
                    java.util.Random random = new java.util.Random();
                    do {
                        nextIndex = random.nextInt(tblSongs.getItems().size());
                    } while (nextIndex == currentIndex); //Yg odoo togloj baigaa duug dahin davtahgui baih hamgaallt 
                } else {
                    // Shuffle idevhgui bol daraallr
                    nextIndex = currentIndex + 1;
                }

                if (nextIndex < tblSongs.getItems().size()) {
                    tblSongs.getSelectionModel().select(nextIndex);
                    
                    
                    if (mediaPlayer != null) {
                        mediaPlayer.play();
                        btnPlay.setText("⏸"); 
                        System.out.println("Дараагийн дуу автоматаар эхэллээ: " + selectedSong.getTitle());
                    }
                } else {
                    btnPlay.setText("▶");
                    System.out.println("Жагсаалтын бүх дуу дууслаа.");
                }
            });

            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
            
            // Shine duu sonsmogts tovchluur "▶" tolovtei baina
            btnPlay.setText("▶");
            if (rotateTransition != null) {
            rotateTransition.pause();
            }

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

            rotateTransition.pause();
        } 
        // Herev duug zogsson esvel belen baival togluulna 
        else {
            mediaPlayer.play();
            btnPlay.setText("⏸"); // Tovchluuriin dursiig Pause bolgono
            System.out.println("Тоглож байна: " + selectedSong.getTitle());

            rotateTransition.play();
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


    // Durtai duu bolgoh 
    private void handleFavorite() {
        if (selectedSong == null) {
            System.out.println("Дуу сонгоогүй байна.");
            return;
        }

       
        boolean newFavoriteStatus = !selectedSong.isFavorite();
        
        boolean success = SongDAO.updateFavoriteStatus(selectedSong.getId(), newFavoriteStatus);
        if (success) {
            selectedSong.setFavorite(newFavoriteStatus); 
            
            if (newFavoriteStatus) {
                btnFavorite.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a4d5c, #008080, #00ffff);" + "-fx-text-fill: red;"); // Durtai bol ulaan bolno
                System.out.println("Дуртай дуугаар тэмдэглэлээ");
            } else {
                btnFavorite.setStyle("-fx-background-color: linear-gradient(to bottom right, #0a4d5c, #008080, #00ffff);" +"-fx-text-fill: white;"); // Tsutslbal har blno
                System.out.println("Дуртай дуунаас хаслаа.");
            }
        }
    }

    //Duu ustgah function
    private void handleDelete() {
        if (selectedSong == null) {
            System.out.println("Устгах дуу сонгогдоогүй байна.");
            return;
        }

        // Hereglegchees uneheer ustgah esehiig asuuh anhaaruulh tsonh
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Дуу устгах");
        alert.setHeaderText(null);
        alert.setContentText("Та '" + selectedSong.getTitle() + "' дууг устгахдаа итгэлтэй байна уу?");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            
            // Herev ustgah gej bui duu odoo togloj baival hogjmiig zogsoono
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer = null;
                btnPlay.setText("▶");
            }

            // Database ees ustgah
            boolean success = SongDAO.deleteSong(selectedSong.getId());
            if (success) {
                System.out.println("Дуу амжилттай устлаа.");
                selectedSong = null;
                lblSongName.setText("Дуу сонгогдоогүй");
                lblArtist.setText("");
                tblSongs.getSelectionModel().clearSelection();
                
                //global jagsaaltaa shinechlene
                songs.clear();
                songs.addAll(SongDAO.getAllSongs());

                //Shuultuuriig anhnii baidald ni oruulj buh duug haruulna 
                filteredData.setPredicate(song -> true);
                listPlayeList.getSelectionModel().clearSelection();
            }else{
                System.out.println("Дууг датабэйсээс устгаж чадсангүй.");
            }
        }
    }

    private void handleStop() {
        if (mediaPlayer == null) return;

        // Hogjmiig buren zogsoono 
        mediaPlayer.stop();

        //UI elementuudiig anhnii (teg) tolovt oruulna 
        btnPlay.setText("▶"); 
        progressSlider.setValue(0); 
        
        // Hugatsaas 00:00 bolgono
        if (mediaPlayer.getTotalDuration() != null) {
            lblTime.setText(formatTime(javafx.util.Duration.ZERO, mediaPlayer.getTotalDuration()));
        }

        //Disc nii animationiig zogsoono 
        if (rotateTransition != null) {
            rotateTransition.pause();
            imgDisc.setRotate(0); 
        }

        System.out.println("Дууг бүрэн зогсоолоо.");
    }

    // Playlist Ustgah
    @FXML
    private void handleDeletePlaylist() {
        // Zuun taliis ListView-ees songoson playlist iin neriig avah 
        String selectedPlaylist = listPlayeList.getSelectionModel().getSelectedItem();

        if (selectedPlaylist == null) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("Анхааруулга");
            alert.setHeaderText(null);
            alert.setContentText("Устгах плейлистээ зүүн талын жагсаалтаас сонгоно уу!");
            alert.showAndWait();
            return;
        }

        
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Плейлист устгах");
        alert.setHeaderText(null);
        alert.setContentText("Та '" + selectedPlaylist + "' плейлистийг устгахдаа итгэлтэй байна уу?");

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            
            // Database-ees ustgah 
            boolean success = com.music_player.Database.PlaylistDAO.deletePlaylist(selectedPlaylist);
            
            if (success) {
                System.out.println("Плейлист амжилттай устлаа: " + selectedPlaylist);
                
                
                listPlayeList.getItems().remove(selectedPlaylist);
                listPlayeList.getSelectionModel().clearSelection();

                currentMode = "ALL";
                filteredData.setPredicate(song -> true);
            } else {
                System.out.println("Плейлист устгахад алдаа гарлаа.");
            }
        }
    }
    
}


    






