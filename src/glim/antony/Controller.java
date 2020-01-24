package glim.antony;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    @FXML
    Button browse;

    @FXML
    Button find;

    @FXML
    TextField pathField;

    @FXML
    TextField extensionField;

    @FXML
    TextArea searchArea;

    @FXML
    TextArea fileTextArea;

    @FXML
    TreeView directoriesTree;

    private Image closeFolder = new Image(getClass().getResourceAsStream("resources/folder-icon.png"));
    private Image textDocument = new Image(getClass().getResourceAsStream("resources/text-icon.png"));

    private File rootDirectory;
    private String extension = ".log";
    private String searchString = "";

    public void browse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(browse.getScene().getWindow()); //todo window does not work correctly
        if (directory != null) {
            pathField.setText(directory.getAbsolutePath());
            rootDirectory = directory;
        }
    }

    public void find() {
        extension = extensionField.getText();
        searchString = searchArea.getText();
        showDirectoriesTree();
    }

    public void displayFile(MouseEvent mouseClick){
        if (mouseClick.getClickCount() == 2){
            TreeItem<Path> item = (TreeItem<Path>)directoriesTree.getSelectionModel().getSelectedItem();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(item.getValue().toFile())))) {
                fileTextArea.clear();
                String strLine;
                while ((strLine = reader.readLine()) != null) {
                    fileTextArea.appendText(strLine + "\n");
                }
                fileTextArea.selectRange(fileTextArea.getText().indexOf(searchString), fileTextArea.getText().indexOf(searchString) + searchString.length()); //todo delete this
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showDirectoriesTree() {
        TreeItem<Path> item = new TreeItem<>(rootDirectory.toPath(), new ImageView(closeFolder));
        item.setExpanded(true);
        try {
            getFileTreeByRecursion(Paths.get(pathField.getText()), item);
        } catch (IOException e) {
            e.printStackTrace();
        }
        directoriesTree.setRoot(item);
    }


    public void getFileTreeByRecursion(Path root, TreeItem<Path> item) throws IOException {
        File folder = root.toFile();
        for (File file : folder.listFiles()) {
            TreeItem<Path> treeItem = new TreeItem<>(file.toPath());
            treeItem.setExpanded(true);
            if (file.isDirectory()) {
                treeItem.setGraphic(new ImageView(closeFolder));
                item.getChildren().add(treeItem);
                getFileTreeByRecursion(file.toPath(), treeItem);
            } else if (file.toString().endsWith(extension) && isFileContains(file.toPath(), searchString)){
                treeItem.setGraphic(new ImageView(textDocument));
                item.getChildren().add(treeItem);
            }
        }
    }

    //This is work correctly
//    public List<Path> getFiles(Path path, String extension, String searchString) {
//        List<Path> files = new ArrayList<>();
//        try (Stream<Path> walk = Files.walk(path)) {
//            files = walk
//                    .filter(f -> f.toString().endsWith(extension))
//                    .filter(f -> isFileContains(f, searchString))
//                    .collect(Collectors.toList());
//            files.forEach(System.out::println); //todo delete this
//            return files;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return files;
//    }


    private boolean isFileContains(Path path, String searchString) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile())))) {
            String strLine;
            while ((strLine = reader.readLine()) != null) {
                if (strLine.contains(searchString)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
