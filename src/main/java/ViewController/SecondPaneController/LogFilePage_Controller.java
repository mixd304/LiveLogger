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

import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    ArrayList<Label> labelList;
    ArrayList<ListView<String>> listviewList;

    @FXML
    private void initialize() {
        System.out.println("[INIT] logFilePage geladen");
        labelList = new ArrayList<>();
        listviewList = new ArrayList<>();
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

        // MenuItem - Als Textdatei Speichern
        MenuItem menuItem_safeAsTxt = new MenuItem("Als Textdatei speichern");
        menuItem_safeAsTxt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Zeile - Als Textdatei speichern geklickt");
                contextMenu.hide();

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Speicherort auswählen");
                fileChooser.setFileHidingEnabled(false);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int returnVal = fileChooser.showOpenDialog(null);
                String path = null;
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    path = fileChooser.getSelectedFile().getAbsolutePath();
                }

                if(path == null) {
                    return;
                }

                for (Label l: labelList) {
                    if(l.getId().equals(listview.getId() + "_label")) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH.mm-ss");
                        Date date = new Date();
                        path = path + "/[" + dateFormat.format(date) + "] " + l.getText().trim() + ".txt";
                    }
                }
                File file = new File(path);

                System.out.println("[INFO]   Speichere Logausgaben in Textdatei " + file);
                try {
                    PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true);
                    for(String s: (ObservableList<String>) listview.getItems()) {
                        printWriter.println(s);
                        printWriter.flush();
                    }
                    printWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // TODO: Kontextmenü erweitern - ListView

        // Alle MenuItems zum ContextMenu hinzufügen
        contextMenu.getItems().add(menuItem_open);
        contextMenu.getItems().add(menuItem_copy);
        contextMenu.getItems().add(menuItem_safeAsTxt);
        listview.setContextMenu(contextMenu);
    }
}
