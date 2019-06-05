package GUI.SecondPaneController.LogFilePages;

import GUI.DefaultGUIController;
import Model.Container.LogReader;
import Model.Verbindung;
import ProgrammStart.Main;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import java.util.UUID;

public class LogFilePage_1_Controller extends LogFilePage_Controller {
    LogReader logReader;

    @FXML GridPane gridPane;
    @FXML ListView<String> log_1;

    @FXML
    private void initialize() {
        System.out.println("[GUI] logFilePage_1 geladen");
        buildLogs();
    }

    protected void buildLogs() {
        for (UUID uuid: DefaultGUIController.getSessionContainer().getCheckedVerbindungenUUIDs()) {
            Verbindung verbindung = DefaultGUIController.getModelContainer().getVerbindungByUUID(uuid);
            if(verbindung.getBetriebssystem().equals("Linux")) {
                // Textfeld für das erste Logfeld
                log_1.setEditable(false);
                log_1.setPrefHeight(gridPane.getPrefHeight() - 2);
                log_1.setPrefWidth(gridPane.getPrefWidth() - 2);

                // interner Abstand
                gridPane.setPadding(new Insets(1, 1, 1, 1));
                // Außenabstand
                gridPane.setVgap(0);
                gridPane.setHgap(0);

                log_1.getItems().add("Test " + "\r\n");

                Main.getExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        LogReader.readLinux(log_1, verbindung.getLogpath(), verbindung.getHost(), verbindung.getBenutzername(), verbindung.getPasswort());
                        Thread.currentThread().stop();
                    }
                });
            }
        }
    }
}
