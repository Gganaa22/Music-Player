package com.music_player.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private Button btnAddSong;

    @FXML
    private ListView<?> listPlayeList;

    @FXML
    private TableView<?> tblSongs;

    @FXML
    private TextField txtSearch;

}
