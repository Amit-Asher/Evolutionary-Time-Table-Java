package MainForm;

import MainForm.Pages.Blank.BlankController;
import MainForm.Pages.Home.HomeController;
import MainForm.Pages.Problem.ProblemController;
import MainForm.Pages.Solution.SolutionController;
import MainForm.Pages.Statistics.StatisticsController;
import Resources.ResourcesConstants;
import TimeTableModel.EvolutionaryTimeTableModel;
import TimeTableModel.Exceptions.*;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainFormController implements Initializable {
    private EvolutionaryTimeTableModel model = new EvolutionaryTimeTableModel();
    private HomeController homePageController;
    private ProblemController problemPageController;
    private SolutionController solutionPageController;
    private StatisticsController statisticsController;
    private BlankController blankController;
    private List<Rectangle> rectangles = new ArrayList<>();

    @FXML
    private BorderPane BorderPaneMain;

    @FXML
    private TextField textFieldFileLocation;

    @FXML
    private Button ButtonSolution;

    @FXML
    private Button buttonBrowse;

    @FXML
    private Label labelLoadFailed;

    @FXML
    private Button ButtonHome;

    @FXML
    private Button ButtonStatistics;

    @FXML
    private ScrollPane ScrollPanePage;

    @FXML
    private Button ButtonProblem;

    @FXML
    private ComboBox<String> ComboBoxStyle;

    @FXML
    private HBox HBoxPostHeader;

    @FXML
    private VBox VBoxButtons;

    @FXML
    private Button ButtonAbout;

    @FXML
    private Button ButtonExit;

    private Stage primaryStage;

    private VBox currentVbox;

    public MainFormController() {

        /* LOAD HOME CONTROLLER */
        FXMLLoader loader = new FXMLLoader();
        // load main fxml
        URL urlFXML = getClass().getResource(ResourcesConstants.HOME_FXML_RESOURCE_IDENTIFIER);
        loader.setLocation(urlFXML);
        try {
            VBox root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        homePageController = loader.getController();
        homePageController.setDescriptorRef(model);
        homePageController.setMainFormController(this);

        /* LOAD PROBLEM CONTROLLER */
        loader = new FXMLLoader();
        urlFXML = getClass().getResource(ResourcesConstants.PROBLEM_FXML_RESOURCE_IDENTIFIER);
        loader.setLocation(urlFXML);

        try {
            VBox root2 = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        problemPageController = loader.getController();
        problemPageController.setDescriptorRef(model);

        /* LOAD SOLUTION CONTROLLER */
        loader = new FXMLLoader();
        urlFXML = getClass().getResource(ResourcesConstants.SOLUTION_FXML_RESOURCE_IDENTIFIER);
        loader.setLocation(urlFXML);
        try {
            VBox root3 = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        solutionPageController = loader.getController();
        solutionPageController.setDescriptorRef(model);

        /* LOAD STATISTICS CONTROLLER */
        loader = new FXMLLoader();
        urlFXML = getClass().getResource(ResourcesConstants.STATISTICS_FXML_RESOURCE_IDENTIFIER);
        loader.setLocation(urlFXML);
        try {
            VBox root4 = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        statisticsController = loader.getController();
        statisticsController.setDescriptorRef(model);

        /* LOAD BLANK CONTROLLER */
        loader = new FXMLLoader();
        urlFXML = getClass().getResource(ResourcesConstants.BLANK_FXML_RESOURCE_IDENTIFIER);
        loader.setLocation(urlFXML);
        try {
            VBox root5 = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


        blankController = loader.getController();
    }

    @FXML
    void OpenSplashScreen(ActionEvent event) {
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("About");
        stage.setResizable(false);

        Group root = new Group();
        Image image = new Image("MainForm/Images/SplashScreen.jpg");
        ImageView iv1 = new ImageView();
        iv1.setImage(image);
        iv1.setFitWidth(640);
        iv1.setFitHeight(360);
        root.getChildren().add(iv1);

        FadeTransition fadeTransition= new FadeTransition(Duration.millis(5000), iv1);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();

        Scene scene = new Scene(root, 640, 360);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    public void initialize() {
        ButtonHome.setDisable(true);
        ButtonProblem.setDisable(true);
        ButtonSolution.setDisable(true);
        ButtonStatistics.setDisable(true);
    }

    private void changeStyle(int index){
        switch (index){
            case 0:
                BorderPaneMain.getStylesheets().clear();
                BorderPaneMain.getStylesheets().add(getClass().getResource("CSS/Neon.css").toExternalForm());
                break;
            case 1:
                BorderPaneMain.getStylesheets().clear();
                BorderPaneMain.getStylesheets().add(getClass().getResource("CSS/Ocean.css").toExternalForm());
                break;
            case 2:
                BorderPaneMain.getStylesheets().clear();
                BorderPaneMain.getStylesheets().add(getClass().getResource("CSS/Coffee.css").toExternalForm());
                break;
            case 3:
                BorderPaneMain.getStylesheets().clear();
                BorderPaneMain.getStylesheets().add(getClass().getResource("CSS/Night.css").toExternalForm());
                break;
            case 4:
                BorderPaneMain.getStylesheets().clear();
                BorderPaneMain.getStylesheets().add(getClass().getResource("CSS/Empty.css").toExternalForm());
                break;
        }
    }

    public void startLoadingAnimation() {
        HBoxPostHeader.getChildren().clear();
        for(int i=0; i < 3; i++) {
            Rectangle rect = new Rectangle(0, 20, 15, 15);
            rect.setFill(Color.GREEN);
            HBoxPostHeader.getChildren().add(rect);

            FadeTransition ft = new FadeTransition(Duration.millis(3000));
            ft.setFromValue(1);
            ft.setToValue(0.1);
            ft.setCycleCount(Animation.INDEFINITE);

            RotateTransition rt = new RotateTransition(Duration.seconds(3));
            rt.setByAngle(180);
            rt.setCycleCount(Animation.INDEFINITE);

            ParallelTransition pt = new ParallelTransition(rect, ft, rt);
            pt.play();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            rectangles.add(rect);
        }
    }

    public void endLoadingAnimation() {
        HBoxPostHeader.getChildren().clear();
        Label finished = new Label("Finished!");
        finished.setTextFill(Color.GREEN);
        HBoxPostHeader.getChildren().add(finished);
    }

    public void turnOnLoadingAnimation() {
        rectangles.forEach(r -> r.setVisible(true));
    }

    public void turnOffLoadingAnimation() {
        rectangles.forEach(r -> r.setVisible(false));
    }

    @FXML
    public void LoadSolutionPage(ActionEvent event) {
        solutionPageController.LoadBestSolution();
        VBox newPage = solutionPageController.getVboxMain();
        currentVbox = newPage;
        ScrollPanePage.setContent(newPage);
    }

    @FXML
    void LoadHomePage(ActionEvent event) {
        VBox newPage = homePageController.getVboxMain();

        ScrollPanePage.setContent(newPage);
    }

    @FXML
    void LoadStatisticsPage(ActionEvent event) {
        VBox newPage = statisticsController.getVboxMain();
        ScrollPanePage.setContent(newPage);
    }

    @FXML
    void LoadProblemPage(ActionEvent event) {
        problemPageController.loadData();
        VBox newPage = problemPageController.getVboxMain();
        ScrollPanePage.setContent(newPage);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void EnableSolutionButton()
    {
        ButtonSolution.setDisable(false);
    }

    public boolean loadFile(String fileName) {
        boolean loadedSuccessfully = false;
        //labelLoadFailed.setTextFill(Color.web("#FF0000"));
        try {
            model.LoadFile(fileName);
            loadedSuccessfully = true;
        }
        catch (FileNotFoundException e) {
            updateLabelFailure(labelLoadFailed, "File not found. Please try again with correct file path.");
        }
        catch (ExceptionDuplicateRule e){
            updateLabelFailure(labelLoadFailed, "File contains Duplicate Rule. Please try again with a valid file.");
        }
        catch (ExceptionClassIdNotSequential e){
            updateLabelFailure(labelLoadFailed,"File contains non-sequential order of classes IDs. Please try again with a valid file.");
        }
        catch(ExceptionTeacherIdNotSequential e){
            updateLabelFailure(labelLoadFailed,"File contains sequential order of teachers IDs which not starting from one. Please try again with a valid file.");
        }
        catch (ExceptionSubjectIdNotSequential e){
            updateLabelFailure(labelLoadFailed,"File contains sequential order of subjects IDs which not starting from one. Please try again with a valid file.");
        }
        catch (ExceptionWrongFileType e){
            updateLabelFailure(labelLoadFailed,"File type is not .xml; Please try again with a valid file.");
        }
        catch (ExceptionInvalidSubjectForTeacher e){
            String msg = "File contains teacher (Teacher ID=" + e.getTeacherId() + ")" +
                    " that teaches non-existing subject (Subject ID=" + e.getSubjectId() + "). " +
                    "Please try again with a valid file.";

            updateLabelFailure(labelLoadFailed,msg);
        }
        catch (ExceptionInvalidSubjectForClass e){
            String msg = "File contains class (Class ID=" + e.getClasId() + ")" +
                    " that learns non-existing subject (Subject ID=" + e.getSubjectId() + "). " +
                    "Please try again with a valid file.";
            updateLabelFailure(labelLoadFailed,msg);
        }
        catch (ExceptionTooManyHoursForClass e){
            String msg = "File contains class (Class ID=" + e.getClasId() + ")" +
                    " that requires " + e.getActualRequiredHours() + " hours to study" +
                    " while max hours in the Time Table are " + e.getMaxHours() + ". " +
                    "Please try again with a valid file.";

            updateLabelFailure(labelLoadFailed,msg);
        }
        catch (ExceptionElitismGreaterThanPopulation e) {
            String msg = "File contains elitism size of " + e.getElitismCount() +
                    " which is greater than population size which is " + e.getPopulationSize() + ". " +
                    "Please try again with a valid file.";
            updateLabelFailure(labelLoadFailed,msg);
        }
        return loadedSuccessfully;
    }

    @FXML
    void buttonBrowseAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file");

        File selectedFile = fileChooser.showOpenDialog(primaryStage);
        if (selectedFile == null) {
            return;
        }

        String absolutePath = selectedFile.getAbsolutePath();
        if(loadFile(absolutePath)) {
            textFieldFileLocation.setText(absolutePath);
            ButtonHome.setDisable(false);
            ButtonProblem.setDisable(false);
            ButtonSolution.setDisable(true);
            problemPageController.setLoaded(false);

            updateControllers();

            VBox newPage = homePageController.getVboxMain();
            ScrollPanePage.setContent(newPage);

            updateLabelSuccess(labelLoadFailed, "File successfully loaded.");
        }
        else {
            ScrollPanePage.setContent(blankController.getVBoxBlank());
            initialize();
        }

        labelLoadFailed.setVisible(true);
        DisableStatistics();
        homePageController.DisableSeeSolution();
    }

    private void updateControllers(){
        problemPageController.loadData();
        homePageController.ResetPage();
    }

    public void EnableStatistics() {
        ButtonStatistics.setDisable(false);
    }

    public void DisableStatistics() {
        ButtonStatistics.setDisable(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        BorderPaneMain.getStylesheets().add(getClass().getResource("CSS/Neon.css").toExternalForm());
        ComboBoxStyle.getItems().add("Default");
        ComboBoxStyle.getItems().add("Ocean");
        ComboBoxStyle.getItems().add("Coffee");
        ComboBoxStyle.getItems().add("Night");
        ComboBoxStyle.getItems().add("Empty");
        ComboBoxStyle.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            changeStyle((int)newValue);
        });
        ComboBoxStyle.getSelectionModel().select(0);
        homePageController.setSolutionController(solutionPageController);

        ButtonHome.setDisable(true);
        ButtonProblem.setDisable(true);
        ButtonSolution.setDisable(true);
        ButtonStatistics.setDisable(true);

        ScrollPanePage.setContent(blankController.getVBoxBlank());
    }

    private void updateLabelSuccess(Label label, String msg) {

        label.setText(msg);

        if(label.getStyleClass().contains("failure-label")) {
            label.getStyleClass().remove("failure-label");
        }

        label.getStyleClass().add("success-label");
        label.setVisible(true);
    }

    private void updateLabelFailure(Label label, String msg) {
        label.setText(msg);

        if(label.getStyleClass().contains("success-label")) {
            label.getStyleClass().remove("success-label");
        }
        label.getStyleClass().add("failure-label");
        label.setVisible(true);
    }


    @FXML
    void onClickExit(ActionEvent event) {
        Platform.exit();
    }
}