package Controller.SecondPaneController;

import Controller.Dialogs;
import Controller.DefaultGUIController;
import Model.Data.Ordner;
import Model.Data.Verbindung;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.UUID;

/**
 * Controller-Klasse für die FXML-Seite "newVerbindungPage.fxml"
 *
 */
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
        System.out.println("[INIT] newVerbindungPage geladen");
        DefaultGUIController.modelContainer.loadOrdner();
        fillComboBoxOrdner();
        fillChoiceBoxBetriebssystem();
    }

    /**
     * füllt die ComboBox "ordner" mit allen vorhandenen Ordnern
     *
     */
    private void fillComboBoxOrdner() {
        for (Ordner o: DefaultGUIController.modelContainer.getOrdnerList()) {
            ordner.getItems().add(o.getBezeichnung());
        }
    }

    /**
     * füllt die ChoiceBox "betriebssystem" mit allen unterstützten Betriebssysteme
     *
     */
    private void fillChoiceBoxBetriebssystem() {
        betriebssystem.getItems().add("Linux");
        betriebssystem.getItems().add("Windows");
    }

    /**
     * wird aufgerufen, wenn der "Bestätigen"-Knopf auf der newVerbindungPage gedrückt wurde
     * prüft die ID der ActionEvent-Source darauf
     *  ob es sich um eine neue Verbindung
     * @see #submitButtonClicked_newVerbindung()
     *  oder eine bereits vorhandene Verbindung handelt
     * @see #submitButtonClicked_editVerbindung(UUID)
     */
    public void submitButtonClicked(ActionEvent actionEvent) {
        if(((Button) actionEvent.getSource()).getId().equals("submitButton")) {
            submitButtonClicked_newVerbindung();
        } else if (((Button) actionEvent.getSource()).getId().equals("editVerbindungButton")) {
            submitButtonClicked_editVerbindung(UUID.fromString(((Button) actionEvent.getSource()).getParent().getChildrenUnmodifiable().get(0).getId()));
        }
    }

    /**
     * Prüft die Input-Felder auf gültige Werte und legt eine
     * neue Verbindung an
     * @return false, wenn keine/ ungültige Werte eingetragen wurde
     * @return true, wenn alle eingetragenen Werte gültig sind
     */
    private boolean submitButtonClicked_newVerbindung() {
        System.out.println("[AKTION] Neue Verbindung - Submit Button Clicked");
        try {
            // Prüfung des Ordners
            int selected_index = ordner.getSelectionModel().getSelectedIndex();
            Ordner new_ordner = new Ordner();
            if(selected_index == -1) {
                if(ordner.getValue() == null) {
                    Dialogs.confirmDialog("Bitte wählen Sie einen Ordner aus oder legen einen neuen an!");
                    return false;
                } else {
                    new_ordner.setBezeichnung(ordner.getValue());
                    new_ordner.setUuid(UUID.randomUUID());
                    DefaultGUIController.modelContainer.addOrdner(new_ordner);
                }
            } else {
                Ordner ordnerByIndex = DefaultGUIController.modelContainer.getOrdnerList().get(selected_index);
                if(!ordnerByIndex.getBezeichnung().equals(ordner.getValue())) {
                    new_ordner.setBezeichnung(ordner.getValue());
                    new_ordner.setUuid(UUID.randomUUID());
                    DefaultGUIController.modelContainer.addOrdner(new_ordner);
                } else {
                    new_ordner = ordnerByIndex;
                }
            }

            // Prüfung der Bezeichnung
            if(bezeichnung.getText().equals("")) {
                Dialogs.confirmDialog("Bitte tragen Sie eine Bezeichnung ein!");
                return false;
            }

            // Prüfung des Ports auf Zahl oder null
            if(!port.getText().equals("")) {
                try {
                    Integer.parseInt(port.getText());
                } catch (NumberFormatException e) {
                    Dialogs.confirmDialog("Ungültige Eingabe im Port-Feld!");
                    return false;
                }
            }
            // Prüfung des Betriebssystem
            if(betriebssystem.getSelectionModel().getSelectedIndex() == -1) {
                Dialogs.confirmDialog("Bitte wählen Sie ein Betriebssystem aus!");
                return false;
            }

            Verbindung new_verbindung = getInserts();

            System.out.println(DefaultGUIController.modelContainer.addVerbindungToOrdner(new_ordner, new_verbindung).getBemerkung());
            DefaultGUIController.modelContainer.safeOrdner();

            DefaultGUIController.rebuildGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Prüft die Input-Felder auf gültige Werte und überschreibt
     * die bereits vorhandene Verbindung
     */
    private void submitButtonClicked_editVerbindung(UUID uuid) {
        System.out.println("[AKTION] Edit Verbindung - Submit Button Clicked");
        try {
            Verbindung old_verbindung = DefaultGUIController.modelContainer.getVerbindungByUUID(uuid);
            Verbindung new_verbindung = getInserts();
            new_verbindung.setUuid(uuid);

            Ordner old_ordner = DefaultGUIController.modelContainer.getOrdnerByVerbindung(old_verbindung);
            int selected_index = ordner.getSelectionModel().getSelectedIndex();
            Ordner new_ordner = new Ordner();
            Ordner selected_ordner = DefaultGUIController.modelContainer.getOrdnerList().get(selected_index);
            if(!selected_ordner.getBezeichnung().equals(ordner.getValue())) {
                new_ordner.setBezeichnung(ordner.getValue());
                new_ordner.setUuid(UUID.randomUUID());
                DefaultGUIController.modelContainer.addOrdner(new_ordner);

                old_ordner.deleteVerbindungByUUID(old_verbindung.getUuid());
                new_ordner.addVerbindung(new_verbindung);
            } else {
                if(selected_ordner.equals(old_ordner)) {
                    DefaultGUIController.modelContainer.editVerbindung(old_verbindung, new_verbindung);
                } else {
                    old_ordner.deleteVerbindungByUUID(old_verbindung.getUuid());
                    selected_ordner.addVerbindung(new_verbindung);
                }
            }
            DefaultGUIController.modelContainer.safeOrdner();
            DefaultGUIController.rebuildGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Holt alle eingetragenen Werte aus den Input-Feldern
     * @return alle eingetragenen Werte
     */
    private Verbindung getInserts() {
        Verbindung verbindung = new Verbindung();
        verbindung.setBezeichnung(bezeichnung.getText());
        verbindung.setHost(host.getText());
        if(port.getText().equals("")) {
            verbindung.setPort(-1);
        } else {
            verbindung.setPort(Integer.parseInt(port.getText()));
        }
        verbindung.setBenutzername(benutzername.getText());
        verbindung.setPasswort(passwort.getText());
        verbindung.setSafePasswort(safePasswort.isSelected());
        verbindung.setKeyfile(keyfile.getText());
        verbindung.setLogpath(logpath.getText());
        verbindung.setBetriebssystem(betriebssystem.getValue());

        return verbindung;
    }

    /**
     * wird aufgerufen, wenn der "Abbrechen"-Knopf auf der newVerbindungPage gedrückt wurde
     * lädt die alte Seite in den rechten Teil der defaultPage
     */
    public void cancelButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[AKTION] Cancel Button Clicked");

        DefaultGUIController.sessionContainer.safeLogs();
        AnchorPane parent = (AnchorPane) newVerbindungPage.getParent();
        int index = parent.getChildren().indexOf(newVerbindungPage);
        parent.getChildren().set(index, FXMLLoader.load(getClass().getResource(DefaultGUIController.sessionContainer.getPageURL())));

        if(parent.getChildren().get(index).getClass().equals(GridPane.class)) {
            ((GridPane) parent.getChildren().get(index)).prefWidthProperty().bind(parent.widthProperty());
            ((GridPane) parent.getChildren().get(index)).prefHeightProperty().bind(parent.heightProperty());
        } else if(parent.getChildren().get(index).getClass().equals(AnchorPane.class)) {
            ((AnchorPane) parent.getChildren().get(index)).prefWidthProperty().bind(parent.widthProperty());
            ((AnchorPane) parent.getChildren().get(index)).prefHeightProperty().bind(parent.heightProperty());
        }
        DefaultGUIController.sessionContainer.rebuildLogs();
    }
}
