module com.music_player {
    requires javafx.controls;
    requires javafx.fxml;
    //nemsen
    requires java.sql;

    opens com.music_player to javafx.fxml;
    opens com.music_player.Controller to javafx.fxml;
    opens com.music_player.Model to javafx.fxml;

    exports com.music_player.Model;
    exports com.music_player;
    exports com.music_player.Controller;
}
