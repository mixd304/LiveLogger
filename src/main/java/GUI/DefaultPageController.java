package GUI;

import Controller.ModelContainer;
import Model.Ordner;
import Model.Verbindung;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.controlsfx.control.CheckModel;
import org.controlsfx.control.CheckTreeView;

import javax.swing.event.MenuDragMouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javafx.geometry.Pos.BOTTOM_LEFT;
import static javafx.geometry.Pos.BOTTOM_RIGHT;

public class DefaultPageController {
    ModelContainer modelContainer = new ModelContainer();

    @FXML SplitPane SplitPane;
    @FXML Pane SecondPane;
    @FXML VBox vboxTitledPanes;

    private CheckTreeView<Object> tree;

    @FXML
    private void initialize() {
        System.out.println("[GUI] Default Page Initialized");
        this.modelContainer.readOrdner();
        fillMenue();
    }

    @FXML
    private void fillMenue() {
        ArrayList<Ordner> ordnerArrayList = modelContainer.getOrdnerList();
        for(Ordner ordner: ordnerArrayList) {
            TitledPane titledPane = new TitledPane();
            titledPane.setText(ordner.getBezeichnung());
            titledPane.setPrefWidth(200.0);
            titledPane.setContextMenu(generateContextMenu());
            VBox vBox = new VBox();
            vBox.setPrefWidth(200.0);

            ArrayList<Verbindung> verbindungen = ordner.getList();
            for (Verbindung verbindung: verbindungen) {
                CheckBox checkBox = createCheckbox(verbindung);
                vBox.getChildren().add(checkBox);
            }
            titledPane.setContent(vBox);
            vboxTitledPanes.getChildren().add(titledPane);
        }
    }

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
                refreshLogs(actionEvent);
            }
        });
        checkBox.setContextMenu(generateContextMenu());

        return checkBox;
    }

    private ContextMenu generateContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem_open = new MenuItem("Öffnen");
        menuItem_open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Öffnen geklickt");
            }
        });
        MenuItem menuItem_edit = new MenuItem("Bearbeiten");
        menuItem_edit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Bearbeiten geklickt");
            }
        });
        MenuItem menuItem_delete = new MenuItem("Löschen");
        menuItem_delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Löschen geklickt");
            }
        });
        MenuItem menuItem_copyAsTemplate = new MenuItem("Als Vorlage verwenden");
        menuItem_copyAsTemplate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("[ACTION] Als Vorlage verwenden geklickt");
            }
        });

        contextMenu.getItems().add(menuItem_open);
        contextMenu.getItems().add(menuItem_edit);
        contextMenu.getItems().add(menuItem_delete);
        contextMenu.getItems().add(menuItem_copyAsTemplate);

        return contextMenu;
    }

    public void menue_newButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[ACTION] New Button Clicked!");
        Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/newVerbindungPage.fxml"));
        SplitPane.getItems().set(1, newLoadedPane);
    }

    public void menue_deleteButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[ACTION] Delete Button Clicked!");
        //buildDeleteWindow(((Node) actionEvent.getSource()).getScene().getWindow());
    }

    public void refreshLogs(ActionEvent actionEvent) {
        System.out.println("[ACTION] CheckBox CLICKED! - Source = " + actionEvent.getSource());
        int counter = countSelected();
        CheckBox checked = getCheckedBox(actionEvent);
        try {
            if(counter == 0) {
                Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/defaultSecondPage.fxml"));
                SplitPane.getItems().set(1, newLoadedPane);
            } else if (counter == 1) {
                Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/logFilePage_1.fxml"));
                SplitPane.getItems().set(1, newLoadedPane);
            } else if (counter == 2) {
                Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/logFilePage_2.fxml"));
                SplitPane.getItems().set(1, newLoadedPane);
            } else if (counter == 3 || counter == 4) {
                Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/logFilePage_4.fxml"));
                SplitPane.getItems().set(1, newLoadedPane);
            } else if (counter > 4) {
                checked.setSelected(false);
                Dialogs.warnDialog("Sie dürfen nur maximal 4 Datein gleichzeitig auswählen!", "Warnung");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private int countSelected() {
        int counter = 0;
        List<Node> titledPanes = vboxTitledPanes.getChildren();
        for (Node node_titledpane: titledPanes) {
            TitledPane titledPane = (TitledPane) node_titledpane;
            VBox vBox = (VBox) titledPane.getContent();
            List<Node> checkBoxes = vBox.getChildren();
            for (Node node_checkbox: checkBoxes) {
                CheckBox checkBox = (CheckBox) node_checkbox;
                if(checkBox.isSelected())
                    counter++;
            }
        }
        return counter;
    }

    public javafx.scene.control.SplitPane getSplitPane() {
        return SplitPane;
    }

    public void setSplitPane(javafx.scene.control.SplitPane splitPane) {
        SplitPane = splitPane;
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

