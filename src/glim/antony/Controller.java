package glim.antony;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;

import java.io.File;

public class Controller {

    @FXML
    Button browse;

    public void browse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(browse.getScene().getWindow());
        if (directory == null) System.out.println("Nothing selected");
        else System.out.println(directory.getAbsolutePath());
    }

}
