package Model.Container;

import GUI.DefaultGUIController;
import GUI.Dialogs;
import Model.logSession;
import Model.Verbindung;
import ProgrammStart.Main;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.UUID;

public class SessionContainer {
    private ArrayList<UUID> verbindungsList = new ArrayList<UUID>();
    private String url_defaultSecondPage = "/SecondPane/homepage.fxml";
    private String url_logFilePage = "/SecondPane/logFilePage.fxml";
    private ArrayList<ObservableList<String>> ausgaben = new ArrayList<ObservableList<String>>();
    private GridPane logFilePage;
    private logSession logSession1;
    private logSession logSession2;
    private logSession logSession3;
    private logSession logSession4;
    private LogReader logReader1;
    private LogReader logReader2;
    private LogReader logReader3;
    private LogReader logReader4;

    public SessionContainer() {
    }

    public void addVerbindung(CheckBox checked) {
        if (verbindungsList.size() < 4) {
            this.verbindungsList.add(UUID.fromString(checked.getId()));
            this.openLog(DefaultGUIController.getModelContainer().getVerbindungByUUID(UUID.fromString(checked.getId())));
        } else {
            checked.setSelected(false);
            Dialogs.warnDialog("Sie dürfen nur maximal 4 Datein gleichzeitig auswählen!", "Warnung");
        }
    }

    public String getPageURL() {
        if (this.verbindungsList.size() == 0) {
            return this.url_defaultSecondPage;
        } else {
            return this.url_logFilePage;
        }
    }

    public void removeVerbindung(CheckBox checked) {
        this.closeLog(UUID.fromString(checked.getId()));
        this.verbindungsList.remove(UUID.fromString(checked.getId()));
        buildListViews();
    }

    public void removeVerbindungByUUID(UUID uuid) {
        System.out.println("removeVerbindungByUUID aufgerufen");
        this.closeLog(uuid);
        this.verbindungsList.remove(uuid);
        buildListViews();
    }

    public ArrayList<UUID> getCheckedVerbindungenUUIDs() {
        return this.verbindungsList;
    }

    public void openLog(Verbindung verbindung) {
        buildListViews();
        System.out.println("openLog geöffnet");
        int pos = this.verbindungsList.indexOf(verbindung.getUuid());
        System.out.println("POS = " + pos);
        if (pos == 0) {
            openSession(logSession1, verbindung);
        } else if (pos == 1) {
            openSession(logSession2, verbindung);
        } else if (pos == 2) {
            openSession(logSession3, verbindung);
        } else if (pos == 3) {
            openSession(logSession4, verbindung);
        }
    }

    private void openSession(logSession logSession, Verbindung verbindung) {
        logSession.setVerbindung(verbindung);
        logSession.setId(verbindung.getUuid().toString());
        logSession.getLabel().setText(verbindung.getBezeichnung());
        logSession.getListView().getItems().add("Hier steht nachher die Logausgabe der Verbindung");
        logSession.getListView().getItems().add("Bezeichnung: " + verbindung.getBezeichnung() + " - UUID: " + verbindung.getUuid());
        startNewLogReader(logSession);
    }

    private void startNewLogReader(logSession logSession) {
        /*logSession.setControlSubThread(new ControlSubThread(logSession.getListView(), 10));
        logSession.getControlSubThread().start();
        if(logSession.getVerbindung().getBetriebssystem().equals("Linux")) {
            logSession.getControlSubThread().readLinux(logSession.getVerbindung().getLogpath(), logSession.getVerbindung().getHost(), logSession.getVerbindung().getBenutzername(), logSession.getVerbindung().getPasswort());
        } else {
            logSession.getControlSubThread().readWindows(logSession.getVerbindung().getLogpath(), logSession.getVerbindung().getHost(), logSession.getVerbindung().getBenutzername(), logSession.getVerbindung().getPasswort());
        }*/
        Main.getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                logSession.setLogReader(new LogReader(logSession.getListView()));
                if(logSession.getVerbindung().getBetriebssystem().equals("Linux")) {
                    logSession.getLogReader().readLinux(logSession.getVerbindung().getLogpath(), logSession.getVerbindung().getHost(), logSession.getVerbindung().getBenutzername(), logSession.getVerbindung().getPasswort());
                } else {
                    logSession.getLogReader().readWindows(logSession.getVerbindung().getLogpath(), logSession.getVerbindung().getHost(), logSession.getVerbindung().getBenutzername(), logSession.getVerbindung().getPasswort());
                }
            }
        });
    }

    public void closeLog(UUID uuid) {
        System.out.println("closeLog geöffnet");
        int pos = this.verbindungsList.indexOf(uuid);
        System.out.println("POS = " + pos);
        if (pos == 0) {
            System.out.println("logSession1:");
            logSession1.print();
            System.out.println("logSession2:");
            logSession2.print();
            System.out.println("logSession3:");
            logSession3.print();
            copySession(logSession2, logSession1);
            copySession(logSession3, logSession2);
            copySession(logSession4, logSession3);
            clearSession(logSession4);
        } else if (pos == 1) {
            copySession(logSession3, logSession2);
            copySession(logSession4, logSession3);
            clearSession(logSession4);
        } else if (pos == 2) {
            copySession(logSession4, logSession3);
            clearSession(logSession4);
        } else if (pos == 3) {
            clearSession(logSession4);
        }
    }

    private void copySession(logSession logSession_old, logSession logSession_new) {
        clearSession(logSession_new);
        logSession_new.setId(logSession_old.getId());
        logSession_new.setVerbindung(logSession_old.getVerbindung());
        logSession_new.getLabel().setText(logSession_old.getLabel().getText());
        logSession_new.getListView().getItems().addAll(logSession_old.getListView().getItems());
        //logSession_new.setControlSubThread(logSession_old.getControlSubThread());
        logSession_new.getListView().toString();
        logSession_new.setLogReader(logSession_old.getLogReader());
    }

    private void clearSession(logSession logSession) {
        if(logSession.getLogReader() != null) {
            logSession.getLogReader().stop();
        }
        logSession.getLabel().setText("");
        logSession.getListView().getItems().clear();
        //logSession.getControlSubThread().interrupt();
    }

    private void buildListViews() {
        if (this.getCheckedVerbindungenUUIDs().size() == 1) {
            System.out.println("ANZAHL = 1");
            buildListViews_1();
        } else if (this.getCheckedVerbindungenUUIDs().size() == 2) {
            System.out.println("ANZAHL = 2");
            buildListViews_2();
        } else if (this.getCheckedVerbindungenUUIDs().size() == 3) {
            System.out.println("ANZAHL = 3");
            buildListViews_3();
        } else {
            System.out.println("ANZAHL = 4");
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

    public void safeLogs() {
        System.out.println("safeLogs aufgerufen");
        this.ausgaben.clear();
        if (!logSession1.getListView().equals(null)) {
            this.ausgaben.add(logSession1.getListView().getItems());
            this.ausgaben.add(logSession2.getListView().getItems());
            this.ausgaben.add(logSession3.getListView().getItems());
            this.ausgaben.add(logSession4.getListView().getItems());
        }
        for (ObservableList<String> listView : this.ausgaben) {
            listView.toString();
        }
    }

    public void rebuildLogs() {
        System.out.println("rebuildLogs aufgerufen " + this.ausgaben.size());
        buildListViews();
        for (ObservableList<String> listView : this.ausgaben) {
            listView.toString();
        }
        if (this.ausgaben.size() >= 1) {
            logSession1.getListView().getItems().setAll(this.ausgaben.get(0));
        }
        if (this.ausgaben.size() >= 2) {
            logSession2.getListView().getItems().setAll(this.ausgaben.get(1));
        }
        if (this.ausgaben.size() >= 3) {
            logSession3.getListView().getItems().setAll(this.ausgaben.get(2));
        }
        if (this.ausgaben.size() >= 4) {
            logSession4.getListView().getItems().setAll(this.ausgaben.get(3));
        }
    }

    public String getUrl_defaultSecondPage() {
        return url_defaultSecondPage;
    }

    public String getUrl_logFilePage() {
        return url_logFilePage;
    }

    public GridPane getLogFilePage() {
        return logFilePage;
    }
    public void setLogFilePage(GridPane logFilePage) {
        this.logFilePage = logFilePage;
    }
    public void setLogSession1(logSession logSession1) {
        this.logSession1 = logSession1;
    }
    public void setLogSession2(logSession logSession2) {
        this.logSession2 = logSession2;
    }
    public void setLogSession3(logSession logSession3) {
        this.logSession3 = logSession3;
    }
    public void setLogSession4(logSession logSession4) {
        this.logSession4 = logSession4;
    }
}
