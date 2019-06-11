package Model.Container;

import Controller.DefaultGUIController;
import Controller.Dialogs;
import Model.Data.Verbindung;
import Model.Data.LogSession;
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
    private LogSession logSession1;
    private LogSession logSession2;
    private LogSession logSession3;
    private LogSession logSession4;
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
            openSession(logSession1, logReader1, verbindung);
        } else if (pos == 1) {
            openSession(logSession2, logReader2, verbindung);
        } else if (pos == 2) {
            openSession(logSession3, logReader3, verbindung);
        } else if (pos == 3) {
            openSession(logSession4, logReader4, verbindung);
        }
    }

    private void openSession(LogSession LogSession, LogReader logReader, Verbindung verbindung) {
        LogSession.setVerbindung(verbindung);
        LogSession.setId(verbindung.getUuid().toString());
        LogSession.getLabel().setText(verbindung.getBezeichnung());
        LogSession.getListView().getItems().add("Hier steht nachher die Logausgabe der Verbindung");
        LogSession.getListView().getItems().add("Bezeichnung: " + verbindung.getBezeichnung() + " - UUID: " + verbindung.getUuid());
        startLogReader(logReader, verbindung);
    }

    private void startLogReader(LogReader logReader, Verbindung verbindung) {
        logReader.start();
        if(verbindung.getBetriebssystem().equals("Linux")) {
            logReader.readLinux(verbindung.getLogpath(), verbindung.getHost(), verbindung.getBenutzername(), verbindung.getPasswort());
        } else {
            logReader.readWindows(verbindung.getLogpath(), verbindung.getHost(), verbindung.getBenutzername(), verbindung.getPasswort());
        }

        /*Main.executor.execute(new Runnable() {
            @Override
            public void run() {
                LogSession.setLogReader(new LogReader(LogSession));
                if(LogSession.getVerbindung().getBetriebssystem().equals("Linux")) {
                    LogSession.getLogReader().readLinux(LogSession.getVerbindung().getLogpath(), LogSession.getVerbindung().getHost(), LogSession.getVerbindung().getBenutzername(), LogSession.getVerbindung().getPasswort());
                } else {
                    LogSession.getLogReader().readWindows(LogSession.getVerbindung().getLogpath(), LogSession.getVerbindung().getHost(), LogSession.getVerbindung().getBenutzername(), LogSession.getVerbindung().getPasswort());
                }
            }
        });*/
    }

    public void closeLog(UUID uuid) {
        System.out.println("closeLog geöffnet");
        int pos = this.verbindungsList.indexOf(uuid);
        System.out.println("POS = " + pos);
        if (pos == 0) {
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

    private void copySession(LogSession logSession_old, LogSession logSession_new) {
        clearSession(logSession_new);
        logSession_new.setId(logSession_old.getId());
        logSession_new.setVerbindung(logSession_old.getVerbindung());
        logSession_new.getLabel().setText(logSession_old.getLabel().getText());
        logSession_new.getListView().getItems().addAll(logSession_old.getListView().getItems());
        logSession_new.setLogReader(logSession_old.getLogReader());
    }

    private void clearSession(LogSession LogSession) {
        LogSession.getListView().getItems().clear();
        LogSession.getLabel().setText("");
        if(LogSession.getLogReader() != null) {
            LogSession.getLogReader().stop();
        }
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
    public void setLogSession1(LogSession logSession1) {
        this.logSession1 = logSession1;
    }
    public void setLogSession2(LogSession logSession2) {
        this.logSession2 = logSession2;
    }
    public void setLogSession3(LogSession logSession3) {
        this.logSession3 = logSession3;
    }
    public void setLogSession4(LogSession logSession4) {
        this.logSession4 = logSession4;
    }
    public void setLogReader1(LogReader logReader1) {
        this.logReader1 = logReader1;
    }
    public void setLogReader2(LogReader logReader2) {
        this.logReader2 = logReader2;
    }
    public void setLogReader3(LogReader logReader3) {
        this.logReader3 = logReader3;
    }
    public void setLogReader4(LogReader logReader4) {
        this.logReader4 = logReader4;
    }
}
