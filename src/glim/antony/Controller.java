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
        TreeItem<String> rootItem = getTreeItem(rootDirectory.getAbsolutePath(), getFiles(pathField.getText(), extensionField.getText(), searchArea.getText()));
//        if (rootDirectory != null) rootItem.setValue(rootDirectory.getName());
//        rootItem.getChildren().addAll(directories);

        directoriesTree.setRoot(rootItem);
    }

    public List<TreeItem<String>> buildDirectoriesTree() {
        List<String> list = getFiles(pathField.getText(), extensionField.getText(), searchArea.getText());
        List<TreeItem<String>> treeItems = new ArrayList<>();
        list.forEach(f -> treeItems.add(new TreeItem<>(f)));
        return treeItems;
    }

    public TreeItem<String> getTreeItem(String root, List<String> files) {
        String[] tokens = root.split("/");
        String rootName = tokens[tokens.length - 1];
        TreeItem<String> item = new TreeItem<>(rootName);
        item.getChildren().addAll(findAllInDirectories(root, files));
        return item;
    }

    public List<TreeItem<String>> findAllInDirectories(String root, List<String> files) {
        List<TreeItem<String>> treeItems = new ArrayList<>();
        Set<String> set = new HashSet<>();
        for (String f : files) {
            String s = f.replace(root + "/", "");
            set.add(s.split("/")[0]);
        }
        for (String s : set) {
            treeItems.add(new TreeItem<>(s));
        }
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

    private boolean isFileContains(String file, String searchString) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));) {
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
