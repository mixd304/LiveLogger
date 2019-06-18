package Controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Dialogs {
    public static void warnDialog(String warning, String header){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(header);
        alert.setHeaderText(header);
        alert.setContentText(warning);
        alert.showAndWait();
    }

    public static void informationDialog(String information, String header){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(header);
        alert.setHeaderText(header);
        alert.setContentText(information);
        alert.showAndWait();
    }

    /**
     * Ruft ein bestätigungsdialog auf
     * @param confirmation Ist die Nachricht an den Nutzer die er bestätigen soll
     * @return boolean ob er bestätigt hat oder nicht
     */
    public static boolean confirmDialog(String confirmation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bestätigung");
        alert.setHeaderText(confirmation);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }
}
