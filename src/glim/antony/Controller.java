package glim.antony;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class Controller {

    @FXML
    Button browse;

    @FXML
    TextField path;

    public void browse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(browse.getScene().getWindow());
        if (directory != null) {
            System.out.println(directory.getAbsolutePath());
            path.setText(directory.getAbsolutePath());
        }
    }

}
