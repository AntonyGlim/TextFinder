package glim.antony;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    public void find() {
        showDirectoriesTree();
    }

    public void showDirectoriesTree() {
//        List<TreeItem<String>> directories = buildDirectoriesTree();
//        if (rootDirectory != null) rootItem.setValue(rootDirectory.getName());
//        rootItem.getChildren().addAll(directories);

        TreeItem<Path> rootItem = new TreeItem<>();
        List<Path> paths = getFiles(Paths.get(pathField.getText()), extensionField.getText(), searchArea.getText());
        List<TreeItem<Path>> treeItems = new ArrayList<>();
        for (Path path : paths) {
            treeItems.add(new TreeItem<>(path));
        }
        rootItem.getChildren().addAll(treeItems);
        directoriesTree.setRoot(rootItem);
    }

//
//    public List<TreeItem<Path>> buildDirectoriesTree() {
//        List<Path> list = getFiles(pathField.getText(), extensionField.getText(), searchArea.getText());
//        List<TreeItem<Path>> treeItems = new ArrayList<>();
//        list.forEach(f -> treeItems.add(new TreeItem<>(f)));
//        return treeItems;
//    }
//
//    public TreeItem<Path> getTreeItem(Path root, List<Path> files) {
//        String[] tokens = root.split("/");
//        String rootName = tokens[tokens.length - 1];
//        TreeItem<Path> item = new TreeItem<Path>(rootName);
//        item.getChildren().addAll(findAllInDirectories(root, files));
//        return item;
//    }
//
//    public List<TreeItem<Path>> findAllInDirectories(String root, List<Path> files) {
//        List<TreeItem<Path>> treeItems = new ArrayList<>();
//        Set<Path> set = new HashSet<>();
//        for (Path f : files) {
//            set.add(f);
//        }
//        for (Path p : set) {
//            treeItems.add(new TreeItem<>(p));
//        }
//        return treeItems;
//    }


    public List<Path> getFiles(Path path, String extension, String searchString) {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(path)) {
            files = walk
                    .filter(f -> f.toString().endsWith(extension))
                    .filter(f -> isFileContains(f, searchString))
                    .collect(Collectors.toList());
            files.forEach(System.out::println); //todo delete this
            return files;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }


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
