package Model.Data;

import Model.LogReader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;


public class LogSession {
    private Label label;
    private ListView<String> listView;
    private String id;
    private Verbindung verbindung;
    private LogReader logReader;

    public LogSession() {
    }
    public LogSession(Label label, ListView<String> listView) {
        this.label = label;
        this.listView = listView;
    }
    public Label getLabel() {
        return label;
    }
    public void setLabel(Label label) {
        this.label = label;
    }
    public ListView<String> getListView() {
        return listView;
    }
    public void setListView(ListView<String> listView) {
        this.listView = listView;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Verbindung getVerbindung() {
        return verbindung;
    }
    public void setVerbindung(Verbindung verbindung) {
        this.verbindung = verbindung;
    }
    public LogReader getLogReader() {
        return logReader;
    }
    public void setLogReader(LogReader logReader) {
        this.logReader = logReader;
    }
    public void print() {
        System.out.println("LogSession{" +
                "label=" + label +
                ", listView=" + listView +
                ", id='" + id + '\'' +
                ", verbindung=" + verbindung +
                ", logReader=" + logReader +
                '}');
    }

}
