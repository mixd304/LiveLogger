package Controller;

import Model.Container.ModelContainer;
import Model.Container.SessionContainer;
import Model.Data.Ordner;
import Model.Data.Verbindung;
import ProgrammStart.StartProgramm;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.*;
import java.util.*;

import static javafx.geometry.Pos.BOTTOM_LEFT;

/**
 * Controller-Klasse für die FXML-Seite "defaultPage"
 *
 */
public class DefaultGUIController {
    //Dialogs.informationDialog("Diese Funktion ist in der aktuellen Version noch nicht verfügbar.", "Information");
    public static Map<Verbindung, String> passwörter = new HashMap<>();
    public static ModelContainer modelContainer = new ModelContainer();
    public static SessionContainer sessionContainer = new SessionContainer();

    @FXML private AnchorPane defaultPage;
    @FXML private AnchorPane secondPane;
    @FXML private VBox vboxTitledPanes;

    @FXML
    private void initialize() {
        System.out.println("[Controller] Default Page Initialized");
        buildMenue();
    }

    /**
     * Methode um das Menü neu zu aufzubauen
     * Wird aufgerufen, wenn etwas im Menü verändert wurde,
     * z.B. - ein Ordner hinzugefügt/ umbenannt/ gelöscht
     *      - eine Verbindung hinzugefügt/ umbenannt/ gelöscht
     */
    public static void rebuildGUI() {
        try {
            sessionContainer.safeLogs();
            StartProgramm.restart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode, welche das Menü mit TitledPanes (als Ordner) und VBoxen mit CheckBoxen als Verbindungen generiert
     * @see #addContextMenuToTitledPane(TitledPane)
     * @see #createCheckbox(Verbindung)
     */
    @FXML
    private void buildMenue() {
        try {
            modelContainer.loadOrdner();
            vboxTitledPanes.getChildren().clear();
            ArrayList<UUID> uuids = sessionContainer.getCheckedVerbindungenUUIDs();

            ArrayList<Ordner> ordnerArrayList = modelContainer.getOrdnerList();
            for(Ordner ordner: ordnerArrayList) {
                TitledPane titledPane = new TitledPane();
                titledPane.setUserData(ordner);
                titledPane.setId(ordner.getUuid().toString());
                titledPane.setText(ordner.getBezeichnung());
                addContextMenuToTitledPane(titledPane);
                VBox vBox = new VBox();

                ArrayList<Verbindung> verbindungen = ordner.getList();
                for (Verbindung verbindung: verbindungen) {
                    CheckBox checkBox = createCheckbox(verbindung);
                    vBox.getChildren().add(checkBox);
                    if(uuids.contains(UUID.fromString(checkBox.getId()))) {
                        checkBox.setSelected(true);
                    }
                }
                titledPane.setContent(vBox);
                vboxTitledPanes.getChildren().add(titledPane);
            }

            secondPane.getChildren().clear();
            if(uuids.size() == 0) {
                secondPane.getChildren().add(FXMLLoader.load(getClass().getResource(sessionContainer.getUrl_defaultSecondPage())));
                ((AnchorPane) secondPane.getChildren().get(0)).prefWidthProperty().bind(secondPane.widthProperty());
                ((AnchorPane) secondPane.getChildren().get(0)).prefHeightProperty().bind(secondPane.heightProperty());
            } else {
                secondPane.getChildren().add(FXMLLoader.load(getClass().getResource(sessionContainer.getUrl_logFilePage())));
                ((GridPane) secondPane.getChildren().get(0)).prefWidthProperty().bind(secondPane.widthProperty());
                ((GridPane) secondPane.getChildren().get(0)).prefHeightProperty().bind(secondPane.heightProperty());
                sessionContainer.rebuildLogs();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fügt ein Kontext- (Rechtsklick-) Menü zu einem TitledPane (Ordner) hinzu und verwaltet die Menü Items
     * sowie was passiert, wenn diese angeklickt werden
     * @param tP das Pane, zu welchem das Kontextmenü hinzugefügt werden soll
     * @see #buildPopup_newOrdner(String)
     */
    private void addContextMenuToTitledPane(TitledPane tP) {
        ContextMenu contextMenu = new ContextMenu();

        // MenuItem - Öffnen/ Schließen Knopf - Öffnet bzw schließt eine Verbindung und erneuert die Anzeige entsprechend der Anzahl ausgewählter Logs
        MenuItem menuItem_open_close = new MenuItem("Öffnen/ Schließen");
        menuItem_open_close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Ordner - Öffnen/ Schließen geklickt");
                if(tP.isExpanded()) {
                    tP.setExpanded(false);
                } else {
                    tP.setExpanded(true);
                }
            }
        });

        // MenuItem - Umbenennen Knopf - Bietet die Möglichkeit einen Ordner umzubenennen
        MenuItem menuItem_rename = new MenuItem("Umbenennen");
        menuItem_rename.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Ordner - Umbenennen geklickt");
                while(true) {
                    String[] ordnerData = buildPopup_newOrdner(tP.getText());
                    if(ordnerData == null) {
                        break;
                    } else if(ordnerData[0].equals("")) {
                        Dialogs.confirmDialog("Bitte geben Sie eine Bezeichnung für den Ordner ein!");
                    } else {
                        Ordner old_ordner = modelContainer.getOrdnerByUUID(UUID.fromString(tP.getId()));

                        Ordner new_ordner = new Ordner();
                        new_ordner.setBezeichnung(ordnerData[0]);
                        new_ordner.setUuid(old_ordner.getUuid());
                        new_ordner.setList(old_ordner.getList());
                        modelContainer.editOrdner(old_ordner, new_ordner);
                        modelContainer.safeOrdner();
                        rebuildGUI();
                        break;
                    }

                }
            }
        });

        // MenuItem - Öffnen/ Schließen Knopf - Öffnet bzw schließt eine Verbindung und erneuert die Anzeige entsprechend der Anzahl ausgewählter Logs
        MenuItem menuItem_delete = new MenuItem("Löschen");
        menuItem_delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Ordner - Umbenennen geklickt");
                if(Dialogs.confirmDialog("Soll der Ordner '" + tP.getText() + "' wirklich gelöscht werden? \n \n" +
                        "WARNUNG: Hierbei werden alle zugehörigen Verbindungen ebenfalls gelöscht!")) {
                    UUID uuid = UUID.fromString(tP.getId());

                    for (Verbindung verbindung: modelContainer.getOrdnerByUUID(uuid).getList()) {
                        if(sessionContainer.getCheckedVerbindungenUUIDs().contains(verbindung.getUuid())) {
                                sessionContainer.closeVerbindung(verbindung);
                        }
                    }
                    modelContainer.deleteOrdnerByUUID(UUID.fromString(tP.getId()));
                    rebuildGUI();
                }
            }
        });

        // TODO: Kontextmenü erweitern - TitledPanes (Ordner)

        // Alle MenuItems zum ContextMenu hinzufügen
        contextMenu.getItems().add(menuItem_open_close);
        contextMenu.getItems().add(menuItem_rename);
        contextMenu.getItems().add(menuItem_delete);
        tP.setContextMenu(contextMenu);
    }

    /**
     * Legt eine neue CheckBox an
     * @param verbindung Die Verbindung, welche verwaltet wird, wenn die CheckBox aufgerufen wird
     * @see #addContextMenuToCheckBox(CheckBox)
     * @see #getCheckedBox(ActionEvent)
     * @return liefert die erstelle CheckBox zurück
     */
    private CheckBox createCheckbox(Verbindung verbindung) {
        CheckBox checkBox = new CheckBox();
        checkBox.setAlignment(BOTTOM_LEFT);
        checkBox.setMnemonicParsing(false);
        checkBox.setPrefHeight(18.0);
        checkBox.setText(verbindung.getBezeichnung());
        checkBox.setId(verbindung.getUuid().toString());
        checkBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                checkBoxClicked(getCheckedBox(actionEvent));
            }
        });
        addContextMenuToCheckBox(checkBox);
        return checkBox;
    }

    /**
     * Fügt ein Kontext- (Rechtsklick-) Menü zu einer Checkbox hinzu und verwaltet die Menü Items
     * sowie was passiert, wenn diese angeklickt werden
     * @param cB die Checkbox, welcher das Kontextmenü hinzugefügt werden soll
     * @see #checkBoxClicked(CheckBox)
     * @see #editVerbindung(UUID)
     * @see #copyVerbindung(UUID)
     */
    private void addContextMenuToCheckBox(CheckBox cB) {
        ContextMenu contextMenu = new ContextMenu();

        // MenuItem - Öffnen/ Schließen Knopf - Öffnet bzw schließt eine Verbindung und erneuert die Anzeige entsprechend der Anzahl ausgewählter Logs
        MenuItem menuItem_open_close = new MenuItem("Öffnen/ Schließen");
        menuItem_open_close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Verbindung - Öffnen/ Schließen geklickt");
                if(cB.isSelected()) {
                    cB.setSelected(false);
                } else {
                    cB.setSelected(true);
                }
                checkBoxClicked(cB);
            }
        });

        // MenuItem - Bearbeiten Knopf - Knopf zum Bearbeiten einer Verbindung
        MenuItem menuItem_edit = new MenuItem("Bearbeiten");
        menuItem_edit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Verbindung - Bearbeiten geklickt");
                editVerbindung(UUID.fromString(cB.getId()));
            }
        });

        // MenuItem - Löschen Knopf - Knopf zum Löschen einer Verbindung
        MenuItem menuItem_delete = new MenuItem("Löschen");
        menuItem_delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Verbindung - Löschen geklickt");
                if(Dialogs.confirmDialog("Soll die Verbindung zu '" + cB.getText() + "' wirklich gelöscht werden?")) {
                    if(cB.isSelected()) {
                        cB.setSelected(false);
                    }
                    checkBoxClicked(cB);
                    modelContainer.deleteVerbindungByUUID(UUID.fromString(cB.getId()));
                    sessionContainer.safeLogs();
                    buildMenue();
                }
            }
        });

        // MenuItem - als Vorlage verwenden Knopf - Beim Drücken wird ein Felder zur Erstellung einer neuen Verbindung erstellt mit den Werten der ausgewählten als Vorlage
        MenuItem menuItem_copyAsTemplate = new MenuItem("Als Vorlage verwenden");
        menuItem_copyAsTemplate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Verbindung - Als Vorlage verwenden geklickt");
                copyVerbindung(UUID.fromString(cB.getId()));
            }
        });

        // TODO: Kontextmenü erweitern - CheckBox (Verbindungen)

        // Alle MenuItems zum ContextMenu hinzufügen
        contextMenu.getItems().add(menuItem_open_close);
        contextMenu.getItems().add(menuItem_edit);
        contextMenu.getItems().add(menuItem_delete);
        contextMenu.getItems().add(menuItem_copyAsTemplate);
        cB.setContextMenu(contextMenu);
    }

    /**
     * zeigt die Daten einer Verbindung und ermöglicht es diese zu verändern
     * @param uuid UUID der Verbindung, welche editiert werden soll
     */
    private void editVerbindung(UUID uuid) {
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/newVerbindungPage.fxml"));
            Verbindung verbindung = modelContainer.getVerbindungByUUID(uuid);
            Pane pane = (Pane) newLoadedPane.getChildren().get(0);
            pane.setId(uuid.toString());
            ObservableList<Node> felder = pane.getChildren();

            ((ComboBox<Ordner>) felder.get(0)).getSelectionModel().select(modelContainer.getOrdnerList().indexOf(modelContainer.getOrdnerByVerbindung(verbindung)));
            ((TextField) felder.get(1)).setText(verbindung.getBezeichnung());
            ((TextField) felder.get(2)).setText(verbindung.getHost());
            ((TextField) felder.get(3)).setText(Integer.toString(verbindung.getPort()));
            ((TextField) felder.get(4)).setText(verbindung.getBenutzername());
            ((TextField) felder.get(5)).setText(verbindung.getKeyfile());
            ((PasswordField) felder.get(6)).setText(verbindung.getPasswort());
            ((CheckBox) felder.get(7)).setSelected(verbindung.safePasswort());
            ((TextField) felder.get(8)).setText(verbindung.getLogpath());
            ((ChoiceBox<String>) felder.get(9)).getSelectionModel().select(verbindung.getBetriebssystem());
            ((Button) felder.get(10)).setText("Bearbeiten");
            ((Button) felder.get(10)).setId("editVerbindungButton");

            newLoadedPane.getChildren().removeAll();
            newLoadedPane.getChildren().addAll(felder);
            secondPane.getChildren().set(0, newLoadedPane);
            ((AnchorPane) secondPane.getChildren().get(0)).prefWidthProperty().bind(secondPane.widthProperty());
            ((AnchorPane) secondPane.getChildren().get(0)).prefHeightProperty().bind(secondPane.heightProperty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * benutzt die Daten einer Verbindung als Vorlage zur Erstellung einer neuen
     * @param uuid UUID der Verbindung, welche als Vorlage verwendet werden soll
     */
    private void copyVerbindung(UUID uuid) {
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/newVerbindungPage.fxml"));
            Verbindung verbindung = modelContainer.getVerbindungByUUID(uuid);
            Pane pane = (Pane) newLoadedPane.getChildren().get(0);
            ObservableList<Node> felder = pane.getChildren();

            ((ComboBox<Ordner>) felder.get(0)).getSelectionModel().select(modelContainer.getOrdnerList().indexOf(modelContainer.getOrdnerByVerbindung(verbindung)));
            ((TextField) felder.get(1)).setText(verbindung.getBezeichnung());
            ((TextField) felder.get(2)).setText(verbindung.getHost());
            ((TextField) felder.get(3)).setText(Integer.toString(verbindung.getPort()));
            ((TextField) felder.get(4)).setText(verbindung.getBenutzername());
            ((TextField) felder.get(5)).setText(verbindung.getKeyfile());
            ((PasswordField) felder.get(6)).setText(verbindung.getPasswort());
            ((CheckBox) felder.get(7)).setSelected(verbindung.safePasswort());
            ((TextField) felder.get(8)).setText(verbindung.getLogpath());
            ((ChoiceBox<String>) felder.get(9)).getSelectionModel().select(verbindung.getBetriebssystem());

            newLoadedPane.getChildren().removeAll();
            newLoadedPane.getChildren().addAll(felder);
            secondPane.getChildren().set(0, newLoadedPane);
            ((AnchorPane) secondPane.getChildren().get(0)).prefWidthProperty().bind(secondPane.widthProperty());
            ((AnchorPane) secondPane.getChildren().get(0)).prefHeightProperty().bind(secondPane.heightProperty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * wird aufgerufen, wenn im Menü-Reiter auf "Neu -> Ordner" geklickt wird
     * zeigt die Daten einer Verbindung und ermöglicht es diese zu verändern
     * @see #buildPopup_newOrdner(String)
     */
    public void menue_newButtonClicked_Ordner(ActionEvent actionEvent) {
        System.out.println("[ACTION] NewButtonVerbindung Clicked!");
        while(true) {
            String[] ordnerData = buildPopup_newOrdner("");
            if(ordnerData == null) {
                break;
            } else if(ordnerData[0].equals("")) {
                    Dialogs.confirmDialog("Bitte geben Sie eine Bezeichnung für den Ordner ein!");
            } else {
                Ordner new_ordner = new Ordner();
                new_ordner.setBezeichnung(ordnerData[0]);
                new_ordner.setUuid(UUID.randomUUID());
                modelContainer.addOrdner(new_ordner);

                rebuildGUI();
                break;
            }

        }
    }

    /**
     * baut ein Popup zur Erstellung/ Umbenennung eines Ordners
     * @param old_name Der standardmäßig eingetragene Name im Feld "Bezeichnung"
     * @return die eingetragenen Daten
     * @see #buildPopup_newOrdner(String)
     * @return liefert einen String-Array mit den Eingabedaten zurück
     */
    private String[] buildPopup_newOrdner(String old_name) {
        Dialog<String[]> dialog = new Dialog<>();
        ButtonType submitButton = new ButtonType("Hinzufügen" ,ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,10,10));

        TextField bezeichnung = new TextField();
        bezeichnung.setPromptText("Bezeichnung");
        bezeichnung.setText(old_name);

        grid.add(new Label("Bezeichnung: "), 0, 0);
        grid.add(bezeichnung,1,0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == submitButton){
                String[] array = new String[1];
                array[0] = bezeichnung.getText();
                return array;
            } else if(dialogButton == ButtonType.CANCEL){
                return null;
            }
            return null;
        });

        Optional<String[]> result = dialog.showAndWait();
        if(result.isPresent()) {
            return result.get();
        } else {
            return null;
        }
    }

    /**
     * wird aufgerufen, wenn im Menü-Reiter auf "Neu -> Verbindung" geklickt wird
     * ruft ein neues FXML Fenster auf
     * @see Controller.SecondPaneController.NewVerbindungPage_Controller
     */
    public void menue_newButtonClicked_Verbindung(ActionEvent actionEvent) throws IOException {
        System.out.println("[ACTION] NewButtonVerbindung Clicked!");
        Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/newVerbindungPage.fxml"));
        secondPane.getChildren().set(0, newLoadedPane);
    }

    /**
     * wird aufgerufen, wenn im Menü-Reiter auf "Löschen" geklickt wird
     * TODO: Methode "menue_deleteButtonClicked"
     */
    public void menue_deleteButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[ACTION] Delete Button Clicked!");
        Dialogs.informationDialog("Diese Funktion ist in der aktuellen Version noch nicht verfügbar.", "Information");
    }

    /**
     * wird aufgerufen, wenn eine CheckBox angeklickt wird
     * Überprüft, ob es bereits 4 aktive Verbindungen gibt
     * Wenn nein, dann wird die Verbindung mithilfe der UUID der CheckBox geholt und weitergegeben
     * @param checked angeklickte CheckBox
     * @see Dialogs#getPasswortDialog(Verbindung)
     * @see #checkVerbindung(Verbindung)
     * @see SessionContainer#createNewLogSession(Verbindung) (CheckBox)
     * @see SessionContainer#closeVerbindung(Verbindung)
     */
    public void checkBoxClicked(CheckBox checked) {
        System.out.println("[ACTION] CheckBox geklickt - Source = " + checked);
        try {
            int anz = sessionContainer.countOpen();
            Verbindung verbindung = modelContainer.getVerbindungByUUID(UUID.fromString(checked.getId()));

            if(!checked.isSelected()) {
                sessionContainer.closeVerbindung(modelContainer.getVerbindungByUUID(UUID.fromString(checked.getId())));
                if(anz == 1) {
                    secondPane.getChildren().set(0, FXMLLoader.load(getClass().getResource(sessionContainer.getUrl_defaultSecondPage())));
                    ((AnchorPane) secondPane.getChildren().get(0)).prefWidthProperty().bind(secondPane.widthProperty());
                    ((AnchorPane) secondPane.getChildren().get(0)).prefHeightProperty().bind(secondPane.heightProperty());
                }
                // Wenn die Verbindung + Passwort in der Map stand --> entfernen
                if(passwörter.containsKey(verbindung)) {
                    passwörter.remove(verbindung);
                }
            } else {
                // Wenn bereits 4 Sessions geöffnet sind, kann keine weitere geöffnet werden
                if(anz == 4) {
                    checked.setSelected(false);
                    Dialogs.warnDialog("Sie dürfen nur maximal 4 Datein gleichzeitig auswählen!", "Warnung");
                    return;
                // Wenn checkVerbindung() false zurückliefert, ist die Verbindung nicht vollständig ausgefüllt und kann nicht geöffnet werden
                } else if(!checkVerbindung(verbindung)) {
                    Dialogs.informationDialog("Bitte vervollständigen Sie die Eingaben der Verbindung!", "Information");
                    checked.setSelected(false);
                    return;
                // Wenn das Passwort nicht gespeichert wurde, muss das Passwort vor dem Öffnen erfragt werden
                } else if(!verbindung.safePasswort()){
                    String passwort = Dialogs.getPasswortDialog(verbindung);
                    if(passwort != null) {
                        passwörter.put(verbindung, passwort);
                    } else {
                        checked.setSelected(false);
                        return;
                    }
                }
                // Wenn die Anzahl 0 ist und eine Verbindung geöffnet werden soll, muss die "LogFilePage" gebaut werden
                if(anz == 0) {
                    secondPane.getChildren().set(0, FXMLLoader.load(getClass().getResource(sessionContainer.getUrl_logFilePage())));
                    ((GridPane) secondPane.getChildren().get(0)).prefWidthProperty().bind(secondPane.widthProperty());
                    ((GridPane) secondPane.getChildren().get(0)).prefHeightProperty().bind(secondPane.heightProperty());
                }
                sessionContainer.createNewLogSession(verbindung);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * überprüft die Inhalte einer Verbindung auf Vollständigkeit
     * @param verbindung geprüfte Verbindung
     * @return false, wenn nicht vollständig
     * @return true, wenn vollständig
     */
    private boolean checkVerbindung(Verbindung verbindung) {
        if(verbindung.getLogpath().equals("")) {
            return false;
        } else if(verbindung.getHost().equals("")) {
            return false;
        } else if(verbindung.getBetriebssystem().equals("")) {
            return false;
        } else if(verbindung.getBenutzername().equals("")) {
            return false;
        }
        return true;
    }

    /**
     * Methode um herauszufinden welche CheckBox bei einem ActionEvent angeklickt wird
     * @param actionEvent ActionEvent, welches analysiert werden soll
     * @return CheckBox, welche angeklickt wurde
     */
    private CheckBox getCheckedBox(ActionEvent actionEvent) {
        List<Node> titledPanes = vboxTitledPanes.getChildren();
        for (Node node_titledpane: titledPanes) {
            TitledPane titledPane = (TitledPane) node_titledpane;
            VBox vBox = (VBox) titledPane.getContent();
            List<Node> checkBoxes = vBox.getChildren();
            for (Node node_checkbox: checkBoxes) {
                CheckBox checkBox = (CheckBox) node_checkbox;
                if(actionEvent.getSource() == checkBox)
                    return checkBox;
            }
        }
        return null;
    }
}

