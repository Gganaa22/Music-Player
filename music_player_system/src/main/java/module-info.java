module com.music_player {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.music_player to javafx.fxml;
    exports com.music_player;
}
