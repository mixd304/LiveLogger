package GUI.SecondPaneController;

import Controller.ModelContainer;
import Model.Ordner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.List;

public class NewVerbindungPageController {
    ModelContainer modelContainer = new ModelContainer();

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
        System.out.println("[GUI] newVerbindungPage geladen");
        this.modelContainer.readOrdner();
        fillChoiceBoxOrdner();
        fillChoiceBoxBetriebssystem();
    }

    private void fillChoiceBoxOrdner() {
        for (Ordner o: modelContainer.getOrdnerList()) {
            ordner.getItems().add(o.getBezeichnung());
        }
    }

    private void fillChoiceBoxBetriebssystem() {
        betriebssystem.getItems().add("Linux");
        betriebssystem.getItems().add("Windows");
    }

    public void submitButtonClicked(ActionEvent actionEvent) {

    }

    public void cancelButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("CANCEL BUTTON CLICKED!" + newVerbindungPage + " -- Parent:" + newVerbindungPage.getParent());

        // VERSUCH 1
        //load new Pane
        Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/logFilePage_4.fxml"));

        // get children of parent of secPane (the VBox)
        List<Node> parentChildren = ((Pane) newVerbindungPage.getParent()).getChildren();
        parentChildren.set(parentChildren.indexOf(newVerbindungPage), newLoadedPane);


        System.out.println("CANCEL BUTTON ENDE!" + newVerbindungPage + " -- Parent:" + newVerbindungPage.getParent());
    }
}
