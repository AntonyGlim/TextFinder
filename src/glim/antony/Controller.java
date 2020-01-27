package glim.antony;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Controller {

    @FXML
    Button browse;

    @FXML
    Button find;

    @FXML
    Button previous;

    @FXML
    Button next;

    @FXML
    TextField pathField;

    @FXML
    TextField extensionField;

    @FXML
    TextArea searchArea;

    @FXML
    TextFlow textFlow;

    @FXML
    TreeView directoriesTree;

    private Image closeFolder = new Image(getClass().getResourceAsStream("resources/folder-icon.png"));
    private Image textDocument = new Image(getClass().getResourceAsStream("resources/text-icon.png"));

    private File rootDirectory;
    private String extension = ".log";
    private String searchString = "";

    private int startSelectionIndex;
    private int endSelectionIndex;

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
                textFlow.getChildren().clear();
                String strLine;
                while ((strLine = reader.readLine()) != null) {
                    Text text = new Text(strLine + "\n");
                    textFlow.getChildren().add(text);
                }
//                startSelectionIndex = fileTextArea.getText().indexOf(searchString);
//                endSelectionIndex = startSelectionIndex + searchString.length();
//                fileTextArea.selectRange(startSelectionIndex, endSelectionIndex); //todo delete this
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void previous(){
//        fileTextArea.selectRange(fileTextArea.getText());
    }

    @FXML
    private void next(){
//        int newStartIndex = fileTextArea.getText(endSelectionIndex, fileTextArea.getText().length() - 1).indexOf(searchString);
//        fileTextArea.selectRange(newStartIndex, newStartIndex + searchString.length());
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
