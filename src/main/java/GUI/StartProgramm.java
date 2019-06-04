package GUI;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class StartProgramm extends Application {
    private static Stage stage;

    public void ProgrammStart() {
        launch();
    }

    public void start(Stage primaryStage) throws Exception {
        restart();
    }


    public static void restart() throws IOException {
        System.out.println("[GUI] Neubau der Stage eingeleitet");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(StartProgramm.class.getResource("/defaultPage.fxml"));
        Parent root = loader.load();

        if(stage == null){
            stage = new Stage();
        }

        stage.setTitle("LiveLogger");
        stage.setResizable(false);
        stage.setScene(new Scene(root));
        stage.show();
        System.out.println("[GUI] Neubau der Stage abgeschlossen");
    }
}
