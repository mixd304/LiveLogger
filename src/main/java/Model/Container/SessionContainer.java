package Model.Container;

import Controller.DefaultGUIController;
import Controller.Dialogs;
import Model.Data.Verbindung;
import Model.Data.LogSession;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import java.util.ArrayList;
import java.util.UUID;

public class SessionContainer {
    private ArrayList<Label> labelList = new ArrayList<>();
    private ArrayList<ListView<String>> listviewList = new ArrayList<>();
    private GridPane logFilePage;

    private ArrayList<LogSession> logSessionList = new ArrayList<>();
    private String url_defaultSecondPage = "/SecondPane/homepage.fxml";
    private String url_logFilePage = "/SecondPane/logFilePage.fxml";

    private ArrayList<ObservableList<String>> ausgaben = new ArrayList<ObservableList<String>>();

    public SessionContainer() {
    }

    public void addVerbindung(CheckBox checked) {
        if (logSessionList.size() < 4) {
            Verbindung verbindung = DefaultGUIController.modelContainer.getVerbindungByUUID(UUID.fromString(checked.getId()));
            this.createNewLogSession(verbindung);
        } else {
            checked.setSelected(false);
            Dialogs.warnDialog("Sie dürfen nur maximal 4 Datein gleichzeitig auswählen!", "Warnung");
        }
    }

    public String getPageURL() {
        if (this.logSessionList.size() == 0) {
            return this.url_defaultSecondPage;
        } else {
            return this.url_logFilePage;
        }
    }

    public void removeVerbindung(Verbindung verbindung) {
        LogSession logSession = getLogSessionByID(verbindung.getUuid().toString());
        int pos = this.logSessionList.indexOf(logSession);
        if(logSession.getLogReader() != null) {
            logSession.getLogReader().stop();
        }
        logSessionList.remove(logSession);

        // Wenn die 1. Stelle gelöscht wurde
        if(pos == 0) {
            // und die neue Anzahl = 1 ist
            if(logSessionList.size() >= 1) {
                reset_log(0);
            } if(logSessionList.size() >= 2) {
                reset_log(1);
            } if(logSessionList.size() >= 3) {
                reset_log(2);
            }
        } else if(pos == 1) {
            if(logSessionList.size() >= 2) {
                reset_log(1);
            } if(logSessionList.size() >= 3) {
                reset_log(2);
            }
        } else if(pos == 2) {
            if(logSessionList.size() >= 3) {
                reset_log(2);
            }
        }
        listviewList.get(logSessionList.size()).getItems().clear();
        labelList.get(logSessionList.size()).setText("");
        buildListViews();
    }

    private void reset_log(int number_of_log) {
        logSessionList.get(number_of_log).getLogReader().setListView(listviewList.get(number_of_log));
        listviewList.get(number_of_log).getItems().setAll(logSessionList.get(number_of_log).getListView().getItems());
        logSessionList.get(number_of_log).setListView(listviewList.get(number_of_log));
        labelList.get(number_of_log).setText(logSessionList.get(number_of_log).getLabel().getText());
        logSessionList.get(number_of_log).setLabel(labelList.get(number_of_log));
    }

    private LogSession getLogSessionByID(String id) {
        for (LogSession lS: logSessionList) {
            if(lS.getId().equals(id)) {
                return lS;
            }
        }
        return null;
    }

    public ArrayList<UUID> getCheckedVerbindungenUUIDs() {
        ArrayList<UUID> list = new ArrayList<>();
        for (LogSession lS: this.logSessionList) {
            list.add(lS.getVerbindung().getUuid());
        }
        return list;
    }

    public void createNewLogSession(Verbindung verbindung) {
        int pos = this.logSessionList.size();
        LogSession logSession = new LogSession();
        logSession.setId(verbindung.getUuid().toString());
        logSession.setVerbindung(verbindung);
        if (pos == 0) {
            logSession.setLabel(labelList.get(0));
            logSession.setListView(listviewList.get(0));
        } else if (pos == 1) {
            logSession.setLabel(labelList.get(1));
            logSession.setListView(listviewList.get(1));
        } else if (pos == 2) {
            logSession.setLabel(labelList.get(2));
            logSession.setListView(listviewList.get(2));
        } else if (pos == 3) {
            logSession.setLabel(labelList.get(3));
            logSession.setListView(listviewList.get(3));
        }
        logSession.getLabel().setText("  " + verbindung.getBezeichnung());
        logSession.getListView().getItems().add("Hier steht nachher die Logausgabe der Verbindung");

        LogReader logReader = new LogReader(logSession.getListView());
        logReader.setOutput(verbindung.getBezeichnung());

        if(verbindung.getBetriebssystem().equals("Linux")) {
            logReader.readLinux(verbindung.getLogpath(), verbindung.getHost(), verbindung.getBenutzername(), verbindung.getPasswort());
        } else if(verbindung.getBetriebssystem().equals("Windows")) {
            logReader.readWindows(verbindung.getLogpath(), verbindung.getHost(), verbindung.getBenutzername(), verbindung.getPasswort());
        } else {
            logSession.getListView().getItems().add("Ausgewähltes Betriebssystem wird (noch) nicht unterstützt!");
        }

        logSession.setLogReader(logReader);
        this.logSessionList.add(logSession);
        buildListViews();
    }

    private void buildListViews() {
        if(this.logSessionList.size() == 0) {

        } else if (this.logSessionList.size() == 1) {
            buildListViews_1();
        } else if (this.logSessionList.size() == 2) {
            buildListViews_2();
        } else if (this.logSessionList.size() == 3) {
            buildListViews_3();
        } else {
            buildListViews_3();
        }
    }
    private void buildListViews_1() {
        /* Zeile 1 */
        logFilePage.getColumnConstraints().get(0).setPercentWidth(100);
        /* Zeile 2 */
        logFilePage.getColumnConstraints().get(1).setPercentWidth(0);
        /* Spalte 1 */
        /* Spalte 2 */
        logFilePage.getRowConstraints().get(1).setMinHeight(logFilePage.getMinHeight() - 17);
        logFilePage.getRowConstraints().get(1).setPrefHeight(logFilePage.getPrefHeight() - 17);
        /* Spalte 3 */
        logFilePage.getRowConstraints().get(2).setMinHeight(0);
        logFilePage.getRowConstraints().get(2).setPrefHeight(0);
        /* Spalte 4 */
        logFilePage.getRowConstraints().get(3).setMinHeight(0);
        logFilePage.getRowConstraints().get(3).setPrefHeight(0);
    }
    private void buildListViews_2() {
        /* Zeile 1 */
        logFilePage.getColumnConstraints().get(0).setPercentWidth(50);
        /* Zeile 2 */
        logFilePage.getColumnConstraints().get(1).setPercentWidth(50);
        /* Spalte 1 */
        /* Spalte 2 */
        logFilePage.getRowConstraints().get(1).setMinHeight(logFilePage.getMinHeight() - 17);
        logFilePage.getRowConstraints().get(1).setPrefHeight(logFilePage.getPrefHeight() - 17);
        /* Spalte 3 */
        logFilePage.getRowConstraints().get(2).setMinHeight(0);
        logFilePage.getRowConstraints().get(2).setPrefHeight(0);
        /* Spalte 4 */
        logFilePage.getRowConstraints().get(3).setMinHeight(0);
        logFilePage.getRowConstraints().get(3).setPrefHeight(0);
    }
    private void buildListViews_3() {
        /* Zeile 1 */
        logFilePage.getColumnConstraints().get(0).setPercentWidth(50);
        /* Zeile 2 */
        logFilePage.getColumnConstraints().get(1).setPercentWidth(50);
        /* Spalte 1 */
        /* Spalte 2 */
        logFilePage.getRowConstraints().get(1).setMinHeight(logFilePage.getMinHeight() / 2 - 17);
        logFilePage.getRowConstraints().get(1).setPrefHeight(logFilePage.getPrefHeight() / 2 - 17);
        /* Spalte 3 */
        logFilePage.getRowConstraints().get(2).setMinHeight(17);
        logFilePage.getRowConstraints().get(2).setPrefHeight(17);
        /* Spalte 4 */
        logFilePage.getRowConstraints().get(3).setMinHeight(logFilePage.getMinHeight() / 2 - 17);
        logFilePage.getRowConstraints().get(3).setPrefHeight(logFilePage.getPrefHeight() / 2 - 17);
    }

    // Methode welche vor dem Programmneustart aufgerufen wird
    public void safeLogs() {
        System.out.println("[INFO] Inhalte werden gesichert");
        this.ausgaben.clear();
        for (LogSession lS: logSessionList) {
            System.out.println(lS.getListView().getItems().toString());
            ausgaben.add(lS.getListView().getItems());
        }
    }

    // Methode welche nach dem Programmneustart aufgerufen wird
    public void rebuildLogs() {
        System.out.println("[INFO] Inhalte werden neu geladen. Anzahl = " + this.ausgaben.size());
        buildListViews();
        for(int i = 0; i < logSessionList.size(); i++) {
            logSessionList.get(i).setListView(listviewList.get(i));
            logSessionList.get(i).getListView().getItems().setAll(this.ausgaben.get(logSessionList.indexOf(logSessionList.get(i))));

            logSessionList.get(i).getLogReader().setListView(listviewList.get(i));

            logSessionList.get(i).setLabel(labelList.get(i));
            logSessionList.get(i).getLabel().setText(logSessionList.get(i).getVerbindung().getBezeichnung());
        }
    }


    // Getter und Setter
    public String getUrl_defaultSecondPage() {
        return url_defaultSecondPage;
    }
    public String getUrl_logFilePage() {
        return url_logFilePage;
    }
    public void setLabelList(ArrayList<Label> labelList) {
        this.labelList = labelList;
    }
    public void setListviewList(ArrayList<ListView<String>> listviewList) {
        this.listviewList = listviewList;
    }
    public void setLogFilePage(GridPane logFilePage) {
        this.logFilePage = logFilePage;
    }
}
