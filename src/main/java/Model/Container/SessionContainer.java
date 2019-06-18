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
    /* Verweise auf FXML Nodes
         labelList = Liste aus Labeln (Überschriften)
         listviewList = Liste aus ListViews (Textinhalten)
         logFilePage = GridView (die komplette Seite)
     */
    private ArrayList<Label> labelList = new ArrayList<>();
    private ArrayList<ListView<String>> listviewList = new ArrayList<>();
    private GridPane logFilePage;
    // logSessionList = Alle aktuell aktiven LogSessions
    private ArrayList<LogSession> logSessionList = new ArrayList<>();
    private String url_defaultSecondPage = "/SecondPane/homepage.fxml";
    private String url_logFilePage = "/SecondPane/logFilePage.fxml";
    // ausgaben = Liste zum Speichern von ListViews, wenn die GUI neu gebaut werden muss
    private ArrayList<ObservableList<String>> ausgaben = new ArrayList<>();

    /**
     * Konstruktor
     */
    public SessionContainer() {
    }

    /**
     * Wird aufgerufen, wenn eine CheckBox angeklickt wird
     * Überprüft, ob es bereits 4 aktive Verbindungen gibt
     * Wenn nein, dann wird die Verbindung mithilfe der UUID der CheckBox geholt und weitergegeben
     * @param checked angeklickte CheckBox
     * @see ModelContainer#getVerbindungByUUID(UUID)
     * @see #createNewLogSession(Verbindung)
     */
    public void addVerbindung(CheckBox checked) {
        if (logSessionList.size() < 4) {
            Verbindung verbindung = DefaultGUIController.modelContainer.getVerbindungByUUID(UUID.fromString(checked.getId()));
            this.createNewLogSession(verbindung);
        } else {
            checked.setSelected(false);
            Dialogs.warnDialog("Sie dürfen nur maximal 4 Datein gleichzeitig auswählen!", "Warnung");
        }
    }

    /**
     * Überprüft die Anzahl der aktiven Verbindungen und gibt den entsprechende Pfad der FXML-Seite zurück
     * @return Pfad zur FXML-Seite
     */
    public String getPageURL() {
        if (this.logSessionList.size() == 0) {
            return this.url_defaultSecondPage;
        } else {
            return this.url_logFilePage;
        }
    }

    /**
     * Schließt eine Ausgabe/Verbindung und verschiebt dahinterstehende Verbindungen dementsprechend in der Oberfläche
     * @see #getLogSessionByID(String)
     * @see #reset_log(int)
     * @see #buildListViews()
     */
    public void removeVerbindung(Verbindung verbindung) {
        LogSession logSession = getLogSessionByID(verbindung.getUuid().toString());
        if(logSession != null) {
            int pos = this.logSessionList.indexOf(logSession);
            if(logSession.getLogReader() != null) {
                logSession.getLogReader().stop();
            }
            logSessionList.remove(logSession);

            // Wenn die 1. Stelle gelöscht wurde
            if(pos == 0) {
                // und die neue Anzahl >= 1 ist
                if(logSessionList.size() >= 1) {
                    reset_log(0);
                } if(logSessionList.size() >= 2) {
                    reset_log(1);
                } if(logSessionList.size() >= 3) {
                    reset_log(2);
                }
                // Wenn die 2. Stelle gelöscht wurde
            } else if(pos == 1) {
                if(logSessionList.size() >= 2) {
                    reset_log(1);
                } if(logSessionList.size() >= 3) {
                    reset_log(2);
                }
                // Wenn die 3. Stelle gelöscht wurde
            } else if(pos == 2) {
                if(logSessionList.size() >= 3) {
                    reset_log(2);
                }
            }
            listviewList.get(logSessionList.size()).getItems().clear();
            labelList.get(logSessionList.size()).setText("");
            buildListViews();
        }
    }

    /**
     * Nachdem eine Verbindung geschlossen wurde, müssen LogSessions verschoben werden
     * Mithilfe dieser Methode wird die Stelle "number_of_log" zurückgesetzt
     * @param number_of_log Stelle der zurückzusetzenden Ausgabe
     */
    private void reset_log(int number_of_log) {
        logSessionList.get(number_of_log).getLogReader().setListView(listviewList.get(number_of_log));
        listviewList.get(number_of_log).getItems().setAll(logSessionList.get(number_of_log).getListView().getItems());
        logSessionList.get(number_of_log).setListView(listviewList.get(number_of_log));
        labelList.get(number_of_log).setText(logSessionList.get(number_of_log).getLabel().getText());
        logSessionList.get(number_of_log).setLabel(labelList.get(number_of_log));
    }

    /**
     * Methode zum Ermitteln einer LogSession mithilfe ihrer ID
     * @param id zu suchende ID
     * @return gesuchte LogSession
     */
    private LogSession getLogSessionByID(String id) {
        for (LogSession lS: logSessionList) {
            if(lS.getId().equals(id)) {
                return lS;
            }
        }
        return null;
    }

    /**
     * Methode zum Ermitteln der UUIDs aller aktiven Verbindungen
     * @return Liste mit UUIDs der aktiven Verbindungen
     */
    public ArrayList<UUID> getCheckedVerbindungenUUIDs() {
        ArrayList<UUID> list = new ArrayList<>();
        for (LogSession lS: this.logSessionList) {
            list.add(lS.getVerbindung().getUuid());
        }
        return list;
    }

    /**
     * Öffnet eine neue Verbindung
     * Setzt in der LogSession die entsprechend zugehörigen FXML-Oberflächenteile
     * Baut einen neuen LogReader
     * @param verbindung herzustellende Verbindungen
     * @see LogReader#readLinux(Verbindung)
     * @see LogReader#readWindows(Verbindung)
     * @see #buildListViews()
     */
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
        logSession.getListView().getItems().add("Versuche Verbindung herzustellen...");

        LogReader logReader = new LogReader(logSession.getListView());
        logReader.setOutput(verbindung.getBezeichnung());

        if(verbindung.getBetriebssystem().equals("Linux")) {
            logReader.readLinux(verbindung);
        } else if(verbindung.getBetriebssystem().equals("Windows")) {
            logReader.readWindows(verbindung);
        } else {
            logSession.getListView().getItems().add("Ausgewähltes Betriebssystem wird (noch) nicht unterstützt!");
        }

        logSession.setLogReader(logReader);
        this.logSessionList.add(logSession);
        buildListViews();
    }

    /**
     * Methode, welche die Anzahl der aktiven Logs überprüft und die entsprechende Methode aufruft
     * @see #buildListViews_1()
     * @see #buildListViews_2()
     * @see #buildListViews_3()
     */
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
    /**
     * Setzt alle Höhen und Breiten der GridPane-Felder entsprechend für 1 geöffnetes Log
     */
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
    /**
     * Setzt alle Höhen und Breiten der GridPane-Felder entsprechend für 2 gleichzeitig geöffnete Logs
     */
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
    /**
     * Setzt alle Höhen und Breiten der GridPane-Felder entsprechend für 3 oder 4 gleichzeitig geöffnete Logs
     */
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

    /**
     * Methode, welche vor dem Programmneustart aufgerufen wird
     * Legt alle Ausgaben der ListViews in einer Liste ab
     */
    public void safeLogs() {
        System.out.println("[INFO] Inhalte werden gesichert");
        this.ausgaben.clear();
        for (LogSession lS: logSessionList) {
            ausgaben.add(lS.getListView().getItems());
        }
    }

    /**
     * Methode, welche nach dem Programmneustart aufgerufen wird
     * Liest alle in der Liste gespeicherten Ausgaben und schreibt Sie in die Oberfläche
     */
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
