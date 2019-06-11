package Controller.SecondPaneController;

import Controller.DefaultGUIController;
import Model.Container.LogReader;
import Model.Data.LogSession;
import ProgrammStart.Main;
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
        System.out.println("[Controller] logFilePage geladen");
        DefaultGUIController.getSessionContainer().setLogFilePage(logFilePage);
        DefaultGUIController.getSessionContainer().setLogSession1(new LogSession(log_1_label, log_1));
        DefaultGUIController.getSessionContainer().setLogSession2(new LogSession(log_2_label, log_2));
        DefaultGUIController.getSessionContainer().setLogSession3(new LogSession(log_3_label, log_3));
        DefaultGUIController.getSessionContainer().setLogSession4(new LogSession(log_4_label, log_4));
        Main.executor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultGUIController.getSessionContainer().setLogReader1(new LogReader(log_1));
            }
        });
        Main.executor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultGUIController.getSessionContainer().setLogReader2(new LogReader(log_2));
            }
        });
        Main.executor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultGUIController.getSessionContainer().setLogReader3(new LogReader(log_3));
            }
        });
        Main.executor.execute(new Runnable() {
            @Override
            public void run() {
                DefaultGUIController.getSessionContainer().setLogReader4(new LogReader(log_4));
            }
        });
    }
}
