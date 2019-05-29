package GUI;

import Controller.ModelController;
import Model.Ordner;
import Model.Verbindung;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javafx.geometry.Pos.BOTTOM_LEFT;

public class DefaultPageController {
    ModelController modelController = new ModelController();

    @FXML SplitPane SplitPane;
    @FXML Pane SecondPane;
    @FXML VBox vboxTitledPanes;

    @FXML
    private void initialize() {
        System.out.println("[GUI] Default Page Initialized");
        this.modelController.readOrdner();
        fillMenue();
    }

    @FXML
    private void fillMenue() {
        ArrayList<Ordner> ordnerArrayList = modelController.getOrdnerList();
        for(Ordner ordner: ordnerArrayList) {
            TitledPane titledPane = new TitledPane();
            titledPane.setText(ordner.getBezeichnung());
            titledPane.setPrefWidth(200.0);
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
        return checkBox;
    }

    public void newButtonClicked(ActionEvent actionEvent) throws IOException {
        System.out.println("[ACTION] New Button Clicked!");
        Pane newLoadedPane = FXMLLoader.load(getClass().getResource("/SecondPane/newVerbindungPage.fxml"));
        SplitPane.getItems().set(1, newLoadedPane);
    }

    public void deleteButtonClicked(ActionEvent actionEvent) {
        System.out.println("[ACTION] Delete Button Clicked!");

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
}
