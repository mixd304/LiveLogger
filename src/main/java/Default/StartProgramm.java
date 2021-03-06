package Default;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.FileReader;
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
        loader.setLocation(StartProgramm.class.getResource("/View/defaultPage.fxml"));
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
        String title = "LiveLogger";
        /*try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader("pom.xml"));
            title = model.getArtifactId() + " - v." + model.getVersion();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        stage.setTitle(title);
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
