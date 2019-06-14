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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.CheckModel;
import org.controlsfx.control.CheckTreeView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static javafx.geometry.Pos.BOTTOM_LEFT;
import static javafx.geometry.Pos.BOTTOM_RIGHT;

public class DefaultGUIController {
    static ModelContainer modelContainer = new ModelContainer();
    static SessionContainer sessionContainer = new SessionContainer();

    @FXML private SplitPane SplitPane;
    @FXML private VBox vboxTitledPanes;

    private CheckTreeView<Object> tree;

    @FXML
    private void initialize() {
        System.out.println("[Controller] Default Page Initialized");
        buildMenue();
    }

    public static void rebuildGUI() {
        try {
            sessionContainer.safeLogs();
            StartProgramm.restart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode um das Menü mit TitledPanes (als Ordner) und VBoxen mit CheckBoxen als Verbindungen generiert
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
                titledPane.setPrefWidth(200.0);
                addContextMenuToTitledPane(titledPane);
                VBox vBox = new VBox();
                vBox.setPrefWidth(200.0);

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

            if(uuids.size() == 0) {
                SplitPane.getItems().set(1, FXMLLoader.load(getClass().getResource(sessionContainer.getUrl_defaultSecondPage())));
            } else {
                SplitPane.getItems().set(1, FXMLLoader.load(getClass().getResource(sessionContainer.getUrl_logFilePage())));
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
                Dialogs.informationDialog("Diese Funktion ist in der aktuellen Version noch nicht verfügbar.", "Information");
                // TODO: Ordner umbenennen POPUP
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
                                sessionContainer.removeVerbindung(verbindung);
                        }
                    }
                    modelContainer.deleteOrdnerByUUID(UUID.fromString(tP.getId()));
                    sessionContainer.safeLogs();
                    buildMenue();
                }
            }
        });

        // TODO: Kontextmenü erweitern - TitledPanes (Ordner)
        // z.B. Umbenennen, Löschen


        contextMenu.getItems().add(menuItem_open_close);
        contextMenu.getItems().add(menuItem_rename);
        contextMenu.getItems().add(menuItem_delete);
        tP.setContextMenu(contextMenu);
    }

    /**
     * Legt eine neue CheckBox an
     * @param verbindung Die Verbindung, welche verwaltet wird, wenn die CheckBox aufgerufen wird
     */
    private CheckBox createCheckbox(Verbindung verbindung) {
        CheckBox checkBox = new CheckBox();
        checkBox.setAlignment(BOTTOM_LEFT);
        checkBox.setMnemonicParsing(false);
        checkBox.setPrefHeight(18.0);
        checkBox.setPrefWidth(200.0);
        checkBox.setText(verbindung.getBezeichnung());
        checkBox.setId(verbindung.getUuid().toString());
        checkBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                refreshLogs(getCheckedBox(actionEvent));
            }
        });
        addContextMenuToCheckBox(checkBox);
        return checkBox;
    }

    /**
     * Fügt ein Kontext- (Rechtsklick-) Menü zu einer Checkbox hinzu und verwaltet die Menü Items
     * sowie was passiert, wenn diese angeklickt werden
     * @param cB die Checkbox, welcher das Kontextmenü hinzugefügt werden soll
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
                refreshLogs(cB);
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
                    refreshLogs(cB);
                    modelContainer.deleteVerbindungByUUID(UUID.fromString(cB.getId()));
                    sessionContainer.safeLogs();
                    buildMenue();
                }
            }
        });

        // MenuItem - als Vorlage verwenden Knopf - Beim Drücken wird ein Felder zur Erstellung einer neuen Verbindung erstellt mit den Werten der ausgewählten als Vorlage
        // TODO: Menü Item "als Vorlage verwenden"
        MenuItem menuItem_copyAsTemplate = new MenuItem("Als Vorlage verwenden");
        menuItem_copyAsTemplate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Verbindung - Als Vorlage verwenden geklickt");
                Dialogs.informationDialog("Diese Funktion ist in der aktuellen Version noch nicht verfügbar.", "Information");
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

            verbindung.print();
            System.out.println(modelContainer.getOrdnerByVerbindung(verbindung));
            System.out.println(((ComboBox<Ordner>) felder.get(0)).getItems().toString());

            ((ComboBox<Ordner>) felder.get(0)).getSelectionModel().select(modelContainer.getOrdnerList().indexOf(modelContainer.getOrdnerByVerbindung(verbindung)));
            ((TextField) felder.get(1)).setText(verbindung.getBezeichnung());
            ((TextField) felder.get(2)).setText(verbindung.getHost());
            ((TextField) felder.get(3)).setText(Integer.toString(verbindung.getPort()));
            ((TextField) felder.get(4)).setText(verbindung.getBenutzername());
            ((TextField) felder.get(5)).setText(verbindung.getKeyfile());
            ((PasswordField) felder.get(6)).setText(verbindung.getPasswort());
            ((CheckBox) felder.get(7)).setSelected(verbindung.isSafePasswort());
            ((TextField) felder.get(8)).setText(verbindung.getLogpath());
            ((ChoiceBox<String>) felder.get(9)).getSelectionModel().select(verbindung.getBetriebssystem());
            ((Button) felder.get(10)).setText("Bearbeiten");
            ((Button) felder.get(10)).setId("editVerbindungButton");

            newLoadedPane.getChildren().removeAll();
            newLoadedPane.getChildren().addAll(felder);
            SplitPane.getItems().set(1, newLoadedPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void menue_newButtonOrdner_Clicked(ActionEvent actionEvent) {
        System.out.println("[ACTION] NewButtonVerbindung Clicked!");
        while(true) {
            String[] ordnerData = buildMenue_newButtonOrdner_Clicked_Window();
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

    private String[] buildMenue_newButtonOrdner_Clicked_Window() {
        Dialog<String[]> dialog = new Dialog<>();
        ButtonType submitButton = new ButtonType("Hinzufügen" ,ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20,150,10,10));

        TextField bezeichnung = new TextField();
        bezeichnung.setPromptText("Bezeichnung");

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
     * wird aufgerufen, wenn im Menü-Reiter auf "Neu" geklickt wird
     */
    public void menue_newButtonVerbindung_Clicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[ACTION] NewButtonVerbindung Clicked!");
        Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/newVerbindungPage.fxml"));
        SplitPane.getItems().set(1, newLoadedPane);
    }

    /**
     * wird aufgerufen, wenn im Menü-Reiter auf "Neu" geklickt wird
     * TODO: Methode "menue_deleteButtonClicked"
     */
    public void menue_deleteButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[ACTION] Delete Button Clicked!");
        //buildDeleteWindow(((Node) actionEvent.getSource()).getScene().getWindow());
        Dialogs.informationDialog("Diese Funktion ist in der aktuellen Version noch nicht verfügbar.", "Information");
    }

    /**
     * wird aufgerufen, wenn eine CheckBox angeklickt wird
     * @param checked angeklickte CheckBox
     */
    public void refreshLogs(CheckBox checked) {
        System.out.println("[ACTION] CheckBox geklickt - Source = " + checked);
        try {
            int anz = sessionContainer.getCheckedVerbindungenUUIDs().size();

            if(anz == 1 && !checked.isSelected()) {
                SplitPane.getItems().set(1, FXMLLoader.load(getClass().getResource(sessionContainer.getUrl_defaultSecondPage())));
            } else if(anz == 0 && checked.isSelected()) {
                SplitPane.getItems().set(1, FXMLLoader.load(getClass().getResource(sessionContainer.getUrl_logFilePage())));
            }

            if(checked.isSelected()) {
                sessionContainer.addVerbindung(checked);
            } else if(!checked.isSelected()) {
                sessionContainer.removeVerbindung(modelContainer.getVerbindungByUUID(UUID.fromString(checked.getId())));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode um herauszufinden welche CheckBox bei einem ActionEvent angeklickt wird
     * @param actionEvent ActionEvent, welches analysiert werden soll
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

    public static SessionContainer getSessionContainer() {
        return sessionContainer;
    }
    public static ModelContainer getModelContainer() {
        return modelContainer;
    }

    private void buildDeleteWindow(Window window) {
        Stage deleteStage = new Stage();
        ArrayList<Ordner> ordnerArrayList = modelContainer.getOrdnerList();

        CheckBoxTreeItem<Object> rootItem = new CheckBoxTreeItem<Object>(new String("Alles auswählen"));

        for(Ordner ordner: ordnerArrayList) {
            CheckBoxTreeItem<Object> checkBoxTreeItem_Ordner = new CheckBoxTreeItem<Object>(ordner);

            ArrayList<Verbindung> verbindungen = ordner.getList();
            for (Verbindung verbindung : verbindungen) {
                CheckBoxTreeItem<Object> checkBoxTreeItem_Verbindung = new CheckBoxTreeItem<Object>(verbindung);
                checkBoxTreeItem_Ordner.getChildren().add(checkBoxTreeItem_Verbindung);
            }
            rootItem.getChildren().add(checkBoxTreeItem_Ordner);
        }

        rootItem.setExpanded(true);
        this.tree = new CheckTreeView<Object>(rootItem);
        tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        System.out.println(tree.toString());

        Button submit = new Button();
        submit.setMnemonicParsing(false);
        submit.setText("Bestätigen");
        submit.setAlignment(BOTTOM_LEFT);
        submit.setPrefSize(150,20);
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //deleteWindowSubmitButtonClicked();
                System.out.println("[ACTION] Submit Button clicked");

                System.out.println(tree.toString());
                for (Object o: tree.getCheckModel().getCheckedItems()) {
                    System.out.println(o.toString());
                }
            }
        });

        Button cancel = new Button();
        cancel.setMnemonicParsing(false);
        cancel.setText("Abbrechen");
        cancel.setAlignment(BOTTOM_RIGHT);
        cancel.setPrefSize(150,20);
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Cancel Button clicked");
                deleteStage.close();
            }
        });

        GridPane buttons = new GridPane();
        buttons.setPrefWidth(400);
        buttons.setHgap(100);
        buttons.setGridLinesVisible(false);
        buttons.add(submit, 0, 0);
        buttons.add(cancel, 1, 0);

        StackPane root = new StackPane();
        root.getChildren().add(tree);
        root.getChildren().add(submit);
        //root.getChildren().add(buttons);
        //deleteStage.setScene(new Scene(root, 400, 100));
        root.setPrefWidth(400);
        deleteStage.setScene(new Scene(root));
        deleteStage.centerOnScreen();
        deleteStage.initOwner(window);
        deleteStage.setTitle("Welche Daten sollen gelöscht werden?");
        deleteStage.initModality(Modality.APPLICATION_MODAL);
        deleteStage.show();
    }
    private void deleteWindowSubmitButtonClicked() {
        System.out.println("[ACTION] Submit Button clicked");

        CheckModel checkModel = tree.getCheckModel();
        List<Object> checked = checkModel.getCheckedItems();
        for (Object o: checked) {
            o.toString();
        }
    }
}

