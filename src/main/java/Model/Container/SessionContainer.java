package Model.Container;

import GUI.Dialogs;
import javafx.scene.control.CheckBox;
import java.util.ArrayList;
import java.util.UUID;

public class SessionContainer {
    private ArrayList<UUID> verbindungsList = new ArrayList<UUID>();
    private String url_defaultSecondPage = "/SecondPane/homepage.fxml";
    private String url_logFilePage_1 = "/SecondPane/logFilePage_1.fxml";
    private String url_logFilePage_2 = "/SecondPane/logFilePage_2.fxml";
    private String url_logFilePage_3 = "/SecondPane/logFilePage_4.fxml";
    private String url_logFilePage_4 = "/SecondPane/logFilePage_4.fxml";

    public SessionContainer() {
    }

    public void addVerbindung(CheckBox checked) {
        if(verbindungsList.size() < 4) {
            this.verbindungsList.add(UUID.fromString(checked.getId()));
        } else {
            checked.setSelected(false);
            Dialogs.warnDialog("Sie dürfen nur maximal 4 Datein gleichzeitig auswählen!", "Warnung");
        }
    }

    public void removeVerbindung(CheckBox checked) {
        this.verbindungsList.remove(UUID.fromString(checked.getId()));
    }

    public void removeVerbindungByUUID(UUID uuid) {
        this.verbindungsList.remove(uuid);
    }

    public String getPageURL() {
        if(verbindungsList.size() == 0) {
            return this.url_defaultSecondPage;
        } else if (verbindungsList.size() == 1) {
            return this.url_logFilePage_1;
        } else if (verbindungsList.size() == 2) {
            return this.url_logFilePage_2;
        } else if (verbindungsList.size() == 3) {
            return this.url_logFilePage_3;
        } else if (verbindungsList.size() == 4){
            return this.url_logFilePage_4;
        }
        return null;
    }

    public ArrayList<UUID> getCheckedVerbindungenUUIDs() {
        return this.verbindungsList;
    }
}
