import MainForm.MainFormController;
import Resources.ResourcesConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        // LOAD MAIN FORM FXML
        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = getClass().getResource(ResourcesConstants.MAIN_FXML_RESOURCE_IDENTIFIER);
        loader.setLocation(mainFXML);
        BorderPane root = loader.load();
        // WIRE UP CONTROLLER
        MainFormController mainFormController = loader.getController();
        mainFormController.setPrimaryStage(primaryStage);

        // SET STAGE
        primaryStage.setTitle("Evolutionary Time Table Desktop Application");
        Scene scene = new Scene(root, 1100, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
