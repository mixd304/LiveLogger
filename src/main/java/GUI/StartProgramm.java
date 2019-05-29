package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class StartProgramm extends Application {
    public void ProgrammStart() {
        launch();
    }

    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/defaultPage.fxml"));
        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("LiveLogger");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
    }
}
