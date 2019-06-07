package Model;

import Model.Container.LogReader;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class logSession {
    private Label label;
    private ListView<String> listView;
    private String id;
    private Verbindung verbindung;
    private LogReader logReader;
    private ControlSubThread controlSubThread;

    public logSession(Label label, ListView<String> listView) {
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
        System.out.println("setLogReader geöffnet");
        this.logReader = logReader;
        if(this.logReader != null) {
            this.logReader.setListView(this.listView);
        }
    }
    public ControlSubThread getControlSubThread() {
        return controlSubThread;
    }
    public void setControlSubThread(ControlSubThread controlSubThread) {
        this.controlSubThread = controlSubThread;
    }

    public void print() {
        System.out.println("logSession{" +
                "label=" + label +
                ", listView=" + listView +
                ", id='" + id + '\'' +
                ", verbindung=" + verbindung +
                ", logReader=" + logReader +
                ", controlSubThread=" + controlSubThread +
                '}');
    }
}
