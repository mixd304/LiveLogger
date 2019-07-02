package ViewController;

import Default.StartProgramm;
import View.Dialogs;
import ViewController.SecondPaneController.NewVerbindungPage_Controller;
import ModelController.ModelContainer;
import ModelController.SessionContainer;
import Model.Data.Ordner;
import Model.Data.Verbindung;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javax.swing.*;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static javafx.geometry.Pos.BOTTOM_LEFT;

/**
 * Controller-Klasse für die FXML-Seite "defaultPage"
 *
 */
public class DefaultPage_Controller {
    public static String speicherort = null;
    public static boolean recording;
    public static PrintWriter printWriter;

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
        loadSettings();
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
        System.out.println("[INFO] - {DefaultPage_Controller} Menü wird gebaut!");
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
            System.out.println("[INFO] - {DefaultPage_Controller} Menü erfolgreich gebaut!");
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
                System.out.println("[ACTION] {ContextMenu - Ordner} Öffnen/ Schließen geklickt");
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
                System.out.println("[ACTION] {ContextMenu - Ordner} Umbenennen geklickt");
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
                System.out.println("[ACTION] {ContextMenu - Ordner} Umbenennen geklickt");
                if(Dialogs.confirmDialog("Soll der Ordner '" + tP.getText() + "' wirklich gelöscht werden? \n \n" +
                        "WARNUNG: Hierbei werden alle zugehörigen Verbindungen ebenfalls gelöscht!")) {
                    UUID uuid = UUID.fromString(tP.getId());

                    for (Verbindung verbindung: modelContainer.getOrdnerByUUID(uuid).getList()) {
                        if(sessionContainer.getCheckedVerbindungenUUIDs().contains(verbindung.getUuid())) {
                                sessionContainer.closeVerbindung(verbindung);
                        }
                    }
                    modelContainer.deleteOrdnerByUUID(UUID.fromString(tP.getId()));
                    modelContainer.safeOrdner();
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
        System.out.println("[INFO] - {DefaultPage_Controller} - CheckBox erfolgreich erstellt!");
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
                System.out.println("[ACTION] {ContextMenu - Verbindung} Öffnen/ Schließen geklickt");
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
                System.out.println("[ACTION] {ContextMenu - Verbindung} Bearbeiten geklickt");
                editVerbindung(UUID.fromString(cB.getId()));
            }
        });

        // MenuItem - Löschen Knopf - Knopf zum Löschen einer Verbindung
        MenuItem menuItem_delete = new MenuItem("Löschen");
        menuItem_delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] {ContextMenu - Verbindung} Löschen geklickt");
                if(Dialogs.confirmDialog("Soll die Verbindung zu '" + cB.getText() + "' wirklich gelöscht werden?")) {
                    if(cB.isSelected()) {
                        cB.setSelected(false);
                    }
                    checkBoxClicked(cB);
                    modelContainer.deleteVerbindungByUUID(UUID.fromString(cB.getId()));
                    modelContainer.safeOrdner();
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
                System.out.println("[ACTION] {ContextMenu - Verbindung} Als Vorlage verwenden geklickt");
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
     * @see #fillNewVerbindungPage(ObservableList, Verbindung)
     */
    private void editVerbindung(UUID uuid) {
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/View/SecondPane/newVerbindungPage.fxml"));
            Verbindung verbindung = modelContainer.getVerbindungByUUID(uuid);
            ObservableList<Node> felder = newLoadedPane.getChildren();

            newLoadedPane.setId(uuid.toString());

            fillNewVerbindungPage(felder, verbindung);

            // Ändern der ID des Submit Buttons, damit erkannt werden kann, ob Update oder Neu
            ((Button) felder.get(7)).setText("Übernehmen");
            ((Button) felder.get(7)).setId("editVerbindungButton");

            secondPane.getChildren().set(0, newLoadedPane);
            ((GridPane) secondPane.getChildren().get(0)).prefWidthProperty().bind(secondPane.widthProperty());
            ((GridPane) secondPane.getChildren().get(0)).prefHeightProperty().bind(secondPane.heightProperty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * benutzt die Daten einer Verbindung als Vorlage zur Erstellung einer neuen
     * @param uuid UUID der Verbindung, welche als Vorlage verwendet werden soll
     * @see #fillNewVerbindungPage(ObservableList, Verbindung)
     */
    private void copyVerbindung(UUID uuid) {
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/View/SecondPane/newVerbindungPage.fxml"));
            Verbindung verbindung = modelContainer.getVerbindungByUUID(uuid);
            ObservableList<Node> felder = newLoadedPane.getChildren();

            newLoadedPane.setId(uuid.toString());
            fillNewVerbindungPage(felder, verbindung);

            secondPane.getChildren().set(0, newLoadedPane);
            ((GridPane) secondPane.getChildren().get(0)).prefWidthProperty().bind(secondPane.widthProperty());
            ((GridPane) secondPane.getChildren().get(0)).prefHeightProperty().bind(secondPane.heightProperty());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Füllt alle Input Felder der Seite mit den übergebenen Daten einer Verbindung
     * @param felder zu befüllende Felder
     * @param verbindung Daten mit welchen die Felder gefüllt werden sollen
     */
    private void fillNewVerbindungPage(ObservableList<Node> felder, Verbindung verbindung) {
        for(int i = 0; i < felder.size(); i++) {
            System.out.println("Feld " + i + " = " + felder.get(i).toString());
        };
        ((ComboBox<Ordner>) felder.get(0)).getSelectionModel().select(modelContainer.getOrdnerList().indexOf(modelContainer.getOrdnerByVerbindung(verbindung)));
        ((TextField) felder.get(1)).setText(verbindung.getBezeichnung());
        ((TextField) felder.get(2)).setText(verbindung.getHost());
        ((TextField) felder.get(3)).setText(Integer.toString(verbindung.getPort()));
        ((TextField) felder.get(4)).setText(verbindung.getBenutzername());
        ((PasswordField) felder.get(5)).setText(verbindung.getPasswort());
        ((ChoiceBox<String>) felder.get(6)).getSelectionModel().select(verbindung.getBetriebssystem());
        ((CheckBox) felder.get(9)).setSelected(verbindung.isSafePasswort());
        ((TextField) ((GridPane)((TitledPane) felder.get(10)).getContent()).getChildren().get(0)).setText(verbindung.getKeyfile());
        ((TextField) ((GridPane)((TitledPane) felder.get(10)).getContent()).getChildren().get(1)).setText(verbindung.getLogpath());
        ((TextField) ((GridPane)((TitledPane) felder.get(10)).getContent()).getChildren().get(2)).setText(verbindung.getPrecommand());
        System.out.println("[INFO] - {DefaultPage_Controller} - Felder erfolgreich befüllt!");
    }

    /**
     * wird aufgerufen, wenn im Menü-Reiter auf "Neu -> Ordner" geklickt wird
     * zeigt die Daten einer Verbindung und ermöglicht es diese zu verändern
     * @see #buildPopup_newOrdner(String)
     */
    public void menue_newButtonClicked_Ordner(ActionEvent actionEvent) {
        System.out.println("[ACTION] {Menu - NewButtonOrdner} Clicked!");
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
                modelContainer.safeOrdner();
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
     * @see NewVerbindungPage_Controller
     */
    public void menue_newButtonClicked_Verbindung(ActionEvent actionEvent) throws IOException {
        System.out.println("[ACTION] {Menu - NewButtonVerbindung} Clicked!");
        Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/View/SecondPane/newVerbindungPage.fxml"));
        ((ChoiceBox<String>) newLoadedPane.getChildren().get(6)).getSelectionModel().select("Linux");
        secondPane.getChildren().set(0, newLoadedPane);
        ((GridPane) secondPane.getChildren().get(0)).prefWidthProperty().bind(secondPane.widthProperty());
        ((GridPane) secondPane.getChildren().get(0)).prefHeightProperty().bind(secondPane.heightProperty());
    }

    /**
     * wird aufgerufen, wenn im Menü-Reiter auf "Löschen" geklickt wird
     * TODO: Methode "menue_deleteButtonClicked"
     */
    public void menue_deleteButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[ACTION] {Menu - DeleteButton} Clicked!");
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
        System.out.println("[ACTION] CheckBox geklickt - Source = [ " + checked.getId() + ", " + checked.getText() + " ]");
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
                } else if(!verbindung.isSafePasswort()){
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

    /**
     *
     */
    public void menue_changeSafeLocation(ActionEvent actionEvent) {
        changeSafeLocation();
    }

    /**
     *
     */
    public static boolean changeSafeLocation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Neuer Speicherort");
        fileChooser.setFileHidingEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        System.out.println(speicherort);
        if(speicherort != null) {
            System.out.println(speicherort);
            fileChooser.setCurrentDirectory(new File(speicherort));
        }

        int returnVal = fileChooser.showOpenDialog(null);
        String path = null;
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            path = fileChooser.getSelectedFile().getAbsolutePath();
        }

        if(path == null) {
            return false;
        }

        speicherort = path;
        safeSettings();
        return true;
    }

    /**
     *
     */
    public static boolean checkSafeLocation() {
        if(!speicherort.equals(null)) {
            File location = new File(speicherort);
            if(location.exists()) {
                return true;
            } else {
                Dialogs.confirmDialog("Der Speicherort konnte nicht gefunden werden!\n" +
                        "Bitte wählen Sie einen neuen Speicherort aus!");
                if(changeSafeLocation()) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            Dialogs.confirmDialog("Sie haben noch keinen Speicherort ausgewählt!\n" +
                    "Bitte wählen Sie einen Speicherort aus!");
            if(changeSafeLocation()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     *
     */
    public void menue_toggleRecord(ActionEvent actionEvent) {
        RadioMenuItem source = (RadioMenuItem) actionEvent.getSource();
        if(!recording) {
            try {
                checkSafeLocation();
                DateFormat dateFormat = new SimpleDateFormat("yyyy MM dd HH.mm-ss");
                Date date = new Date();
                File file = new File(speicherort + "/[" + dateFormat.format(date) + "] Recording.txt");
                printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, true)), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            recording = true;
            source.setText("Aufnahme stoppen");
        } else {
            printWriter.close();
            recording = false;
            source.setText("Aufnahme starten");
        }
    }

    /**
     *
     */
    private static boolean safeSettings(){
        System.out.println("[INFO] Speichere Settings");
        try {
            Properties saveProps = new Properties();
            saveProps.setProperty("Speicherort", speicherort);
            saveProps.storeToXML(new FileOutputStream("settings.xml"), "");
            System.out.println("[INFO] Settings erfolgreich gespeichert!");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("[ERROR] Fehler beim speichern der Settings!");
        return false;
    }

    /**
     *
     */
    private static boolean loadSettings(){
        System.out.println("[INFO] Lade Settings...");
        try {
            File file = new File("settings.xml");
            if(file.exists()) {
                Properties loadProps = new Properties();
                loadProps.loadFromXML(new FileInputStream("settings.xml"));
                speicherort = loadProps.getProperty("Speicherort");
                System.out.println("[INFO] Settings erfolgreich geladen");
                return true;
            } else {
                System.out.println("[WARNING] Keine Settings gefunden");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("[ERROR] Fehler beim Laden der Settings!");
        return false;
    }
}

