package GUI.SecondPaneController;

import GUI.DefaultGUIController;
import Model.logSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

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
        System.out.println("[GUI] logFilePage geladen");
        DefaultGUIController.getSessionContainer().setLogFilePage(logFilePage);
        DefaultGUIController.getSessionContainer().setLogSession1(new logSession(log_1_label, log_1));
        DefaultGUIController.getSessionContainer().setLogSession2(new logSession(log_2_label, log_2));
        DefaultGUIController.getSessionContainer().setLogSession3(new logSession(log_3_label, log_3));
        DefaultGUIController.getSessionContainer().setLogSession4(new logSession(log_4_label, log_4));
    }
}
