package ViewController.SecondPaneController;

import ViewController.DefaultPage_Controller;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class LogFilePage_Controller {
    @FXML Label log_1_label;
    @FXML Label log_2_label;
    @FXML Label log_3_label;
    @FXML Label log_4_label;
    @FXML ListView<String> log_1;
    @FXML ListView<String> log_2;
    @FXML ListView<String> log_3;
    @FXML ListView<String> log_4;
    @FXML GridPane logFilePage;

    @FXML
    private void initialize() {
        System.out.println("[INIT] logFilePage geladen");
        ArrayList<Label> labelList = new ArrayList<>();
        ArrayList<ListView<String>> listviewList = new ArrayList<>();
        labelList.add(log_1_label);
        labelList.add(log_2_label);
        labelList.add(log_3_label);
        labelList.add(log_4_label);
        listviewList.add(log_1);
        listviewList.add(log_2);
        listviewList.add(log_3);
        listviewList.add(log_4);
        DefaultPage_Controller.sessionContainer.setLogFilePage(logFilePage);
        DefaultPage_Controller.sessionContainer.setLabelList(labelList);
        for (ListView listview: listviewList) {
            addContextMenuToListView(listview);
        }
        DefaultPage_Controller.sessionContainer.setListviewList(listviewList);
    }

    private void addContextMenuToListView(ListView listview) {
        ContextMenu contextMenu = new ContextMenu();

        // MenuItem - Öffnen
        MenuItem menuItem_open = new MenuItem("Öffnen");
        menuItem_open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Zeile - Öffnen geklickt");

            }
        });

        // MenuItem - Kopieren
        MenuItem menuItem_copy = new MenuItem("Kopieren");
        menuItem_copy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Zeile - Kopieren geklickt");
                ObservableList<String> selected = listview.getSelectionModel().getSelectedItems();
                StringBuilder clipboardString = new StringBuilder();
                for(String s: selected) {
                    clipboardString.append(s);
                }
                final ClipboardContent content = new ClipboardContent();
                content.putString(clipboardString.toString());
                Clipboard.getSystemClipboard().setContent(content);
            }
        });

        // TODO: Kontextmenü erweitern - CheckBox (Verbindungen)

        // Alle MenuItems zum ContextMenu hinzufügen
        contextMenu.getItems().add(menuItem_open);
        contextMenu.getItems().add(menuItem_copy);
        listview.setContextMenu(contextMenu);
    }
}
