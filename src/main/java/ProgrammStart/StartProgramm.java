package ProgrammStart;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StartProgramm extends Application {
    public static ExecutorService executor = Executors.newFixedThreadPool(5);
    private static Stage stage;

    public void ProgrammStart() {
        launch();
    }

    public void start(Stage primaryStage) {
        try {
            restart();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void restart() throws IOException {
        System.out.println("[Controller] Neubau der Stage eingeleitet");


        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(StartProgramm.class.getResource("/defaultPage.fxml"));
        Parent root = loader.load();

        if(stage == null){
            stage = new Stage();
        }

        stage.setMinWidth(400);
        stage.setMinHeight(200);
        /*stage.widthProperty().addListener((obs, oldVal, newVal) -> {

        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {

        });*/
        stage.setTitle("LiveLogger");
        stage.setResizable(true);
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        stage.show();
        System.out.println("[Controller] Neubau der Stage abgeschlossen");
    }
}
