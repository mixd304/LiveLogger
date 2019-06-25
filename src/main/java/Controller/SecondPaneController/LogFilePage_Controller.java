package Controller.SecondPaneController;

import Controller.DefaultPage_Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
        DefaultPage_Controller.sessionContainer.setListviewList(listviewList);
    }
}
