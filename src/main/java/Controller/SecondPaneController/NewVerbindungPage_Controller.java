package Controller.SecondPaneController;

import Controller.Dialogs;
import Controller.DefaultGUIController;
import Model.Data.Ordner;
import Model.Data.Verbindung;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class NewVerbindungPage_Controller {
    @FXML Pane newVerbindungPage;
    // alle Eingabefelder
    @FXML ComboBox<String> ordner;
    @FXML TextField bezeichnung;
    @FXML TextField host;
    @FXML TextField port;
    @FXML TextField benutzername;
    @FXML PasswordField passwort;
    @FXML CheckBox safePasswort;
    @FXML TextField keyfile;
    @FXML TextField logpath;
    @FXML ChoiceBox<String> betriebssystem;

    @FXML
    private void initialize() {
        System.out.println("[Controller] newVerbindungPage geladen");
        DefaultGUIController.getModelContainer().loadOrdner();
        fillChoiceBoxOrdner();
        fillChoiceBoxBetriebssystem();
    }

    // TODO "Anlegen von neuen Ordnern"
    private void fillChoiceBoxOrdner() {
        for (Ordner o: DefaultGUIController.getModelContainer().getOrdnerList()) {
            ordner.getItems().add(o.getBezeichnung());
        }
    }

    private void fillChoiceBoxBetriebssystem() {
        betriebssystem.getItems().add("Linux");
        betriebssystem.getItems().add("Windows");
    }

    public void submitButtonClicked(ActionEvent actionEvent) {
        if(((Button) actionEvent.getSource()).getId().equals("submitButton")) {
            newVerbindung_submitButtonClicked();
        } else if (((Button) actionEvent.getSource()).getId().equals("editVerbindungButton")) {
            editVerbindung_submitButtonClicked(UUID.fromString(((Button) actionEvent.getSource()).getParent().getChildrenUnmodifiable().get(0).getId()));
        }
    }

    // TODO "Anlegen von neuen Verbindungen"
    private void newVerbindung_submitButtonClicked() {
        System.out.println("[AKTION] Neue Verbindung - Submit Button Clicked");
        try {
            int index = ordner.getSelectionModel().getSelectedIndex();
            Ordner new_ordner = new Ordner();
            if(index == -1) {
                if(ordner.getValue().equals("")) {
                    Dialogs.confirmDialog("Bitte wählen Sie einen Ordner aus!");
                } else {
                    new_ordner.setBezeichnung(ordner.getValue());
                    new_ordner.setUuid(UUID.randomUUID());
                    DefaultGUIController.getModelContainer().addOrdner(new_ordner);
                }
            } else {
                Ordner ordnerByIndex = DefaultGUIController.getModelContainer().getOrdnerList().get(index);
                new_ordner = DefaultGUIController.getModelContainer().getOrdnerList().get(index);
                if(!ordnerByIndex.getBezeichnung().equals(ordner.getValue())) {
                    new_ordner.setBezeichnung(ordner.getValue());
                    new_ordner.setUuid(UUID.randomUUID());
                    DefaultGUIController.getModelContainer().addOrdner(new_ordner);
                } else {
                    new_ordner = ordnerByIndex;
                }
            }
            Verbindung new_verbindung = getInserts();

            System.out.println(DefaultGUIController.getModelContainer().addVerbindungToOrdner(new_ordner, new_verbindung).getBemerkung());
            DefaultGUIController.getModelContainer().safeOrdner();

            DefaultGUIController.rebuildGUI();

            // TODO
            //List<Node> parentChildren = ((Pane) newVerbindungPage.getParent()).getChildren();
            //parentChildren.set(parentChildren.indexOf(newVerbindungPage), FXMLLoader.load(getClass().getResource(DefaultGUIController.getSessionContainer().getPageURL())));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Verbindung getInserts() {
        Verbindung verbindung = new Verbindung();
        verbindung.setBezeichnung(bezeichnung.getText());
        verbindung.setHost(host.getText());
        verbindung.setPort(Integer.parseInt(port.getText()));
        verbindung.setBenutzername(benutzername.getText());
        verbindung.setPasswort(passwort.getText());
        verbindung.setSafePasswort(safePasswort.isSelected());
        verbindung.setKeyfile(keyfile.getText());
        verbindung.setLogpath(logpath.getText());
        verbindung.setBetriebssystem(betriebssystem.getValue());

        return verbindung;
    }

    // TODO "Editieren von Verbindungen"
    private void editVerbindung_submitButtonClicked(UUID uuid) {
        System.out.println("[AKTION] Edit Verbindung - Submit Button Clicked");
        //Verbindung verbindung = DefaultGUIController.getModelContainer().getVerbindungByUUID(uuid);
        try {
            Verbindung old_verbindung = DefaultGUIController.getModelContainer().getVerbindungByUUID(uuid);
            Verbindung new_verbindung = getInserts();
            new_verbindung.setUuid(uuid);

            DefaultGUIController.getModelContainer().editVerbindung(old_verbindung, new_verbindung);
            DefaultGUIController.getModelContainer().safeOrdner();
            DefaultGUIController.rebuildGUI();

            // TODO
            //List<Node> parentChildren = ((Pane) newVerbindungPage.getParent()).getChildren();
            //parentChildren.set(parentChildren.indexOf(newVerbindungPage), FXMLLoader.load(getClass().getResource(DefaultGUIController.getSessionContainer().getPageURL())));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void cancelButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[AKTION] Cancel Button Clicked");

        // TODO
        DefaultGUIController.getSessionContainer().safeLogs();
        List<Node> parentChildren = ((Pane) newVerbindungPage.getParent()).getChildren();
        parentChildren.set(parentChildren.indexOf(newVerbindungPage), FXMLLoader.load(getClass().getResource(DefaultGUIController.getSessionContainer().getPageURL())));
        DefaultGUIController.getSessionContainer().rebuildLogs();
    }

    public void editVerbindung(Verbindung verbindung) {

    }
}
