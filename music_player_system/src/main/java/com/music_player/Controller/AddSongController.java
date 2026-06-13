package com.music_player.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import com.music_player.Database.SongDAO;
import com.music_player.Model.Song;

public class AddSongController {

    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private TextField txtArtist;

    @FXML
    private TextField txtFileName;

    @FXML
    private TextField txtGenre;

    @FXML
    private TextField txtTitle;

    //hereglegchiin songoson mp3 file iig hadgalna
    private File selectedFile;

    @FXML
    void handleBrowse(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        
        // Zovhon mp3 file songoh bolomjtoi bolgoh shuultuur
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP3 Files (*.mp3)", "*.mp3");
        fileChooser.getExtensionFilters().add(extFilter);
        
        // file songoh tsonhiig neeh
        Stage stage = (Stage) btnBrowse.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            // Songoson file iin zovhon neriig textField deer haruulna
            txtFileName.setText(selectedFile.getName());
            
            // Herev duunii ner ni talbar hooson  baival file iin neriig ni shuud duunii ner bolgoj boglono
            if (txtTitle.getText().isEmpty()) {
                String nameWithoutExtension = selectedFile.getName().replace(".mp3", "");
                txtTitle.setText(nameWithoutExtension);
            }
        }
    }

    
    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();

    }

    @FXML
    void handleSave(ActionEvent event) {
        String title = txtTitle.getText().trim();
        String artist = txtArtist.getText().trim();
        String genre = txtGenre.getText().trim();
        String fileName = txtFileName.getText().trim();

     
        if (title.isEmpty() || artist.isEmpty() || fileName.isEmpty()) {
            showAlert("Алдаа", "Дууны нэр, дуучин болон файлыг заавал сонгох ёстой!", AlertType.ERROR);
            return;
        }

        //Shine duunii obiekt uusgeh
        Song newSong = new Song(0, title, artist, genre, fileName);

        // SongDAO ashiglan baaz ruu hadgalna
        boolean success = SongDAO.insertSong(newSong);

        if (success) {
            showAlert("Амжилттай", "Шинэ дуу бааз руу амжилттай нэмэгдлээ.", AlertType.INFORMATION);
            
            // odoogoor jinhen mp3 file aa garaar huulj hiij baigaa
            System.out.println("АНХААРУУЛГА: '" + fileName + "' файлыг өөрийн src/main/resources/songs/ хавтас руу гараар хуулж хийхээ мартав аа!");

            handleCancel(null);
        } else {
            showAlert("Алдаа", "Бааз руу хадгалахад алдаа гарлаа.", AlertType.ERROR);
        }

    }

    private void showAlert(String title, String content, AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
