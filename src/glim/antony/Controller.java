package glim.antony;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
    TreeView directoriesTree;

    private File rootDirectory;

    public void browse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directory = directoryChooser.showDialog(browse.getScene().getWindow()); //todo window does not work correctly
        if (directory != null) {
            pathField.setText(directory.getAbsolutePath());
            rootDirectory = directory;
        }
    }

    public void find(){
        buildDirectoriesTree();
        showDirectoriesTree();
    }

    public void showDirectoriesTree() {
        List<TreeItem<String>> directories = buildDirectoriesTree();
        TreeItem<String> rootItem = new TreeItem<>();
        if (rootDirectory != null) rootItem.setValue(rootDirectory.getName());
        rootItem.getChildren().addAll(directories);
        directoriesTree.setRoot(rootItem);
    }

    public List<TreeItem<String>> buildDirectoriesTree() {
        List<String> list = getFiles(pathField.getText(), extensionField.getText(), searchArea.getText());
        List<TreeItem<String>> treeItems = new ArrayList<>();
        list.forEach(f -> treeItems.add(new TreeItem<>(f)));
        return treeItems;
    }

    public List<String> getFiles(String path, String extension, String searchString) {
        List<String> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {
            files = walk
                    .map(Path::toString)
                    .filter(f -> f.endsWith(extension))
                    .filter(f -> isFileContains(f, searchString))
                    .collect(Collectors.toList());
            files.forEach(System.out::println); //todo delete this
            return files;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    private boolean isFileContains(String file, String searchString){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));){
            String strLine;
            while ((strLine = reader.readLine()) != null){
                if (strLine.contains(searchString)) return true;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

}
