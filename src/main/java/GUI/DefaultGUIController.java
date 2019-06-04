package GUI;

import GUI.SecondPaneController.NewVerbindungPage_Controller;
import Model.Container.ModelContainer;
import Model.Container.SessionContainer;
import Model.Ordner;
import Model.Verbindung;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import javax.xml.soap.Text;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static javafx.geometry.Pos.BOTTOM_LEFT;
import static javafx.geometry.Pos.BOTTOM_RIGHT;

public class DefaultGUIController {
    private static ModelContainer modelContainer = new ModelContainer();
    private static SessionContainer sessionContainer = new SessionContainer();


    @FXML private SplitPane SplitPane;
    @FXML private VBox vboxTitledPanes;

    private CheckTreeView<Object> tree;

    @FXML
    private void initialize() {
        System.out.println("[GUI] Default Page Initialized");
        buildMenue();
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

            SplitPane.getItems().set(1, FXMLLoader.load(getClass().getResource(sessionContainer.getPageURL())));
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

        // TODO: weitere Menü Items hinzufügen - TitledPanes (Ordner)
        // z.B. Umbenennen


        contextMenu.getItems().add(menuItem_open_close);
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
                if(cB.isSelected()) {
                    cB.setSelected(false);
                }
                refreshLogs(cB);
                modelContainer.deleteVerbindungByUUID(UUID.fromString(cB.getId()));
                buildMenue();
            }
        });

        // MenuItem - als Vorlage verwenden Knopf - Beim Drücken wird ein Felder zur Erstellung einer neuen Verbindung erstellt mit den Werten der ausgewählten als Vorlage
        // TODO: Menü Item "als Vorlage verwenden"
        MenuItem menuItem_copyAsTemplate = new MenuItem("Als Vorlage verwenden");
        menuItem_copyAsTemplate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Verbindung - Als Vorlage verwenden geklickt");
            }
        });

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
        Verbindung verbindung = modelContainer.getVerbindungByUUID(uuid);
        try {
            Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/newVerbindungPage.fxml"));
            Pane pane = (Pane) newLoadedPane.getChildren().get(0);
            pane.setId(uuid.toString());
            ObservableList<Node> felder = pane.getChildren();

            ((ComboBox<Ordner>) felder.get(0)).getSelectionModel().select(modelContainer.getOrdnerByVerbindung(verbindung));
            ((TextField) felder.get(1)).setText(verbindung.getBezeichnung());
            ((TextField) felder.get(2)).setText(verbindung.getHost());
            ((TextField) felder.get(3)).setText(Integer.toString(verbindung.getPort()));
            ((TextField) felder.get(4)).setText(verbindung.getBenutzername());
            ((TextField) felder.get(5)).setText(verbindung.getKeyfile());
            ((PasswordField) felder.get(6)).setText(verbindung.getPasswort());
            ((CheckBox) felder.get(7)).setSelected(verbindung.isSafePasswort());
            ((TextField) felder.get(8)).setText(verbindung.getLogpath());
            ((ChoiceBox<String>) felder.get(9)).getSelectionModel().select(verbindung.getBetriebssystem());
            ((Button) felder.get(10)).setId("editVerbindungButton");

            newLoadedPane.getChildren().removeAll();
            newLoadedPane.getChildren().addAll(felder);
            SplitPane.getItems().set(1, newLoadedPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * wird aufgerufen, wenn im Menü-Reiter auf "Neu" geklickt wird
     */
    public void menue_newButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[ACTION] New Button Clicked!");
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
    }

    /**
     * wird aufgerufen, wenn eine CheckBox angeklickt wird
     * @param checked angeklickte CheckBox
     */
    public void refreshLogs(CheckBox checked) {
        System.out.println("[ACTION] CheckBox geklickt - Source = " + checked);
        try {
            if(checked.isSelected()) {
                sessionContainer.addVerbindung(checked);
            } else if(!checked.isSelected()) {
                sessionContainer.removeVerbindung(checked);
            }
            SplitPane.getItems().set(1, FXMLLoader.load(getClass().getResource(sessionContainer.getPageURL())));
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
    public static void setModelContainer(ModelContainer modelContainer) {
            DefaultGUIController.modelContainer = modelContainer;
        }
    public static void setSessionContainer(SessionContainer sessionContainer) {
            DefaultGUIController.sessionContainer = sessionContainer;
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

