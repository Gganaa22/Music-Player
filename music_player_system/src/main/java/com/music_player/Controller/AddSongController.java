package com.music_player.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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

    @FXML
    void handleBrowse(ActionEvent event) {
        System.out.println("Файл сонгох товч дарагдлаа");
    }

    @FXML
    void handleCancel(ActionEvent event) {

    }

    @FXML
    void handleSave(ActionEvent event) {

    }

}
