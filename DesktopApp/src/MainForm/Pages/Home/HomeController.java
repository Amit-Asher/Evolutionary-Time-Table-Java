package MainForm.Pages.Home;

import EvolutionModel.Components.Info.FieldInfo;
import MainForm.MainFormController;
import MainForm.Pages.Solution.SolutionController;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.CrossoverTypes.AspectOriented;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.CrossoverTypes.DayTimeOriented;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.SelectionTypes.RouletteWheel;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.SelectionTypes.Tournament;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.SelectionTypes.Truncation;
import TimeTableModel.EvolutionaryTimeTableModel;
import TimeTableModel.Exceptions.ExceptionNotInRange;
import TimeTableModel.Exceptions.ExceptionWrongType;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public class HomeController implements Initializable {

    private EvolutionaryTimeTableModel modelRef;

    private SolutionController solutionController;

    @FXML
    private VBox VboxMain;

    @FXML
    private CheckBox CheckBoxGenreation;

    @FXML
    private TextField TextFieldGenerationInput;

    @FXML
    private ProgressBar ProgressBarGeneration;

    @FXML
    private CheckBox CheckBoxFitness;

    @FXML
    private TextField TextFieldFitnessInput;

    @FXML
    private ProgressBar ProgressBarFitness;

    @FXML
    private CheckBox CheckBoxTime;

    @FXML
    private TextField TextFieldTimeInput;

    @FXML
    private ProgressBar ProgressBarTime;

    @FXML
    private TextField TextFieldUpdateFreqInput;

    @FXML
    private Button ButtonRun;

    @FXML
    private Button ButtonPause;

    @FXML
    private Button ButtonResume;

    @FXML
    private Button ButtonStop;


    @FXML
    private Button ButtonShowSolution;

    @FXML
    private Label LabelGenerationCount;

    @FXML
    private Label LabelBestFitness;


    @FXML
    private ComboBox ComboBoxGeneralSettings;

    @FXML
    private HBox HBoxGeneralSettings;

    @FXML
    private Button ButtonUpdate;

    @FXML
    private Label LabelErrorMessage;

    @FXML
    private Label LabelSettingsError;

    private String[] generalSettingsOptions = {"Mutations", "Selection","Elitism","Crossover"};

    private SimpleStringProperty UICurrentGenerationProp;
    private SimpleStringProperty UICurrentBestFitnessProp;

    private MainFormController mainFormController;

    private eSettingsType currentSettingsState;

    private enum eSettingsType{
        Mutation,
        Selection,
        Elitism,
        Crossover
    }

    public void setSolutionController(SolutionController solutionController) {
        this.solutionController = solutionController;
    }

    public HomeController() {
    }


    public void setMainFormController(MainFormController mainFormController) {
        this.mainFormController = mainFormController;
    }

    public VBox getVboxMain() {
        return VboxMain;
    }

    public void setDescriptorRef(EvolutionaryTimeTableModel modelRef) {
        this.modelRef = modelRef;
    }


    private boolean limitDefined() {
        boolean limitDefined = true;
        if(CheckBoxGenreation.isSelected() || CheckBoxFitness.isSelected() || CheckBoxTime.isSelected()) {
            if (CheckBoxGenreation.isSelected() && TextFieldGenerationInput.getText().isEmpty()) limitDefined = false;
            if (CheckBoxFitness.isSelected() && TextFieldFitnessInput.getText().isEmpty()) limitDefined = false;
            if (CheckBoxTime.isSelected() && TextFieldTimeInput.getText().isEmpty()) limitDefined = false;
        }
        else {
            limitDefined = false;
        }

        return limitDefined;
    }


    private Integer getLimitInteger(CheckBox cb, TextField tf) {
        Integer res = null;
        if(cb.isSelected() && !tf.getText().isEmpty()) {
            res = Integer.parseInt(tf.getText());

        }
        return res;
    }

    private Double getLimitDouble(CheckBox cb, TextField tf) {
        Double res = null;
        if(cb.isSelected() && !tf.getText().isEmpty()) {
            res = Double.parseDouble(tf.getText());
        }
        return res;
    }


    boolean searchErrorInInitialSettings(Integer generationLimit, Double fitnessLimit,
                                         Double timeLimitInMinutes, Integer updateFreq) {
        boolean errorFound = false;

        if(generationLimit != null && updateFreq > generationLimit) {

            updateLabelFailure(LabelErrorMessage,
                    "Unable to start algorithm. Please ensure that update frequency is less than generation limit.");
            errorFound = true;
        }

        if(fitnessLimit != null && fitnessLimit > 100) {
            updateLabelFailure(LabelErrorMessage,
                    "Unable to start algorithm. Please ensure that fitness limit is no greater than 100.");
            errorFound = true;
        }

        return errorFound;
    }

    private Consumer<Integer> getGenerationConsumer(Integer generationLimit) {
        return cg -> {
            LabelGenerationCount.setText(cg.toString());
            if(generationLimit != null) {
                ProgressBarGeneration.setProgress((double)cg / generationLimit);
            }
        };
    }

    private Consumer<Double> getFitnessConsumer(Double fitnessLimit) {
        return cf -> {
            LabelBestFitness.setText(cf.toString());
            if(fitnessLimit != null) {
                ProgressBarFitness.setProgress(cf / fitnessLimit);
            }
        };
    }

    private Consumer<Double> getTimeConsumer(Double timeLimitInSeconds) {
        return ct -> {
            if(timeLimitInSeconds != null) {
                ProgressBarTime.setProgress((ct / timeLimitInSeconds));
            }
        };
    }

    private Runnable getOnFinish() {
        return () -> {
            ButtonPause.setDisable(true);
            ButtonResume.setDisable(true);
            ButtonRun.setDisable(false);
            ButtonStop.setDisable(true);

            CheckBoxTime.setDisable(false);
            CheckBoxGenreation.setDisable(false);
            CheckBoxFitness.setDisable(false);

            TextFieldUpdateFreqInput.setDisable(false);
            HBoxGeneralSettings.setDisable(true);
            resetSettingsSelection();
            ClearSettingsSelection();
            mainFormController.endLoadingAnimation();
            mainFormController.EnableStatistics();
            solutionController.EnableTimeTraverse();
        };
    }

    private void updateControlsAfterRun() {
        updateLabelSuccess(LabelErrorMessage, "Algorithm successfully started");
        ButtonPause.setDisable(false);
        ButtonResume.setDisable(true);
        ButtonRun.setDisable(true);
        ButtonStop.setDisable(false);

        CheckBoxTime.setDisable(true);
        CheckBoxGenreation.setDisable(true);
        CheckBoxFitness.setDisable(true);

        TextFieldUpdateFreqInput.setDisable(true);
        HBoxGeneralSettings.setDisable(true);
        resetSettingsSelection();
        ClearSettingsSelection();
        mainFormController.EnableSolutionButton();
        mainFormController.startLoadingAnimation();
        mainFormController.DisableStatistics();
        solutionController.DisableTimeTraverse();
        ButtonShowSolution.setDisable(false);
    }

    private Double getTimeInSeconds(Double timeLimitInMinutes) {
        Double timeLimitInSeconds = null;
        if(timeLimitInMinutes != null){
            timeLimitInSeconds = Math.floor(timeLimitInMinutes * 60);
        }
        return timeLimitInSeconds;
    }

    @FXML
    void OnClickRun(ActionEvent event) {

        Integer generationLimit = getLimitInteger(CheckBoxGenreation, TextFieldGenerationInput);
        Double fitnessLimit = getLimitDouble(CheckBoxFitness, TextFieldFitnessInput);
        Double timeLimitInMinutes = getLimitDouble(CheckBoxTime, TextFieldTimeInput);
        Integer updateFreq = Integer.parseInt(TextFieldUpdateFreqInput.getText());

        if(searchErrorInInitialSettings(generationLimit, fitnessLimit, timeLimitInMinutes, updateFreq)) {
            return;
        }

        Double timeLimitInSeconds = getTimeInSeconds(timeLimitInMinutes);
        Consumer<Integer> generationConsumer = getGenerationConsumer(generationLimit);
        Consumer<Double> FitnessConsumer = getFitnessConsumer(fitnessLimit);
        Consumer<Double> timeConsumer = getTimeConsumer(timeLimitInSeconds);
        Runnable onFinish = getOnFinish();

        modelRef.RunEvolution(generationConsumer, FitnessConsumer, timeConsumer, onFinish,
                generationLimit, fitnessLimit, timeLimitInSeconds, updateFreq);

        updateControlsAfterRun();
    }


    private void ClearSettingsSelection(){
        ((ComboBox)HBoxGeneralSettings.getChildren().get(1)).getSelectionModel().select(-1);
    }

    @FXML
    void OnClickStop(ActionEvent event) {
        modelRef.StopEvolution();
        ButtonPause.setDisable(true);
        ButtonResume.setDisable(true);
        ButtonRun.setDisable(false);
        ButtonStop.setDisable(true);
        HBoxGeneralSettings.setDisable(true);
        resetSettingsSelection();
        ClearSettingsSelection();
        mainFormController.endLoadingAnimation();
        solutionController.EnableTimeTraverse();
    }

    @FXML
    void OnClickPause(ActionEvent event) {
        modelRef.PauseEvolution();
        ButtonPause.setDisable(true);
        ButtonResume.setDisable(false);
        ButtonRun.setDisable(false);
        ButtonStop.setDisable(false);
        HBoxGeneralSettings.setDisable(false);
        mainFormController.turnOffLoadingAnimation();
    }

    @FXML
    void OnClickResume(ActionEvent event) {
        modelRef.ResumeEvolution();
        ButtonPause.setDisable(false);
        ButtonResume.setDisable(true);
        ButtonRun.setDisable(true);
        ButtonStop.setDisable(false);
        HBoxGeneralSettings.setDisable(true);
        resetSettingsSelection();
        ClearSettingsSelection();
        mainFormController.turnOnLoadingAnimation();
    }

    void OnInitialSettingChanged() {
        ButtonRun.setDisable(!limitDefined() || TextFieldUpdateFreqInput.getText().isEmpty());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        LabelErrorMessage.setVisible(false);

        TextFieldGenerationInput.disableProperty().bind(Bindings.or(CheckBoxGenreation.selectedProperty().not(),CheckBoxGenreation.disableProperty()));
        TextFieldFitnessInput.disableProperty().bind(Bindings.or(CheckBoxFitness.selectedProperty().not(),CheckBoxFitness.disableProperty()));
        TextFieldTimeInput.disableProperty().bind(Bindings.or(CheckBoxTime.selectedProperty().not(),CheckBoxTime.disableProperty()));

        ProgressBarGeneration.disableProperty().bind(CheckBoxGenreation.selectedProperty().not());
        ProgressBarFitness.disableProperty().bind(CheckBoxFitness.selectedProperty().not());
        ProgressBarTime.disableProperty().bind(CheckBoxTime.selectedProperty().not());

        TextFieldGenerationInput.textProperty().addListener((obs, oldText, newText) -> OnInitialSettingChanged());
        TextFieldFitnessInput.textProperty().addListener((obs, oldText, newText) -> OnInitialSettingChanged());
        TextFieldTimeInput.textProperty().addListener((obs, oldText, newText) -> OnInitialSettingChanged());
        TextFieldUpdateFreqInput.textProperty().addListener((obs, oldText, newText) -> OnInitialSettingChanged());

        CheckBoxGenreation.selectedProperty().addListener((obs, oldText, newText) -> OnInitialSettingChanged());
        CheckBoxFitness.selectedProperty().addListener((obs, oldText, newText) -> OnInitialSettingChanged());
        CheckBoxTime.selectedProperty().addListener((obs, oldText, newText) -> OnInitialSettingChanged());

        UnaryOperator<TextFormatter.Change> doubleFilter = change -> {
            String text = change.getControlNewText();
            if (text.matches("^[0-9]*(\\.[0-9]*)?")) { //dont touch this ever
                return change;
            }
            return null;
        };

        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String text = change.getControlNewText();
            if (text.matches("[0-9]*")) { //dont touch this ever
                return change;
            }
            return null;
        };

        TextFieldGenerationInput.setTextFormatter(new TextFormatter<String>(integerFilter));
        TextFieldFitnessInput.setTextFormatter(new TextFormatter<String>(doubleFilter));
        TextFieldTimeInput.setTextFormatter(new TextFormatter<String>(doubleFilter));
        TextFieldUpdateFreqInput.setTextFormatter(new TextFormatter<String>(integerFilter));

        ComboBoxGeneralSettings.getItems().addAll(generalSettingsOptions);

        ComboBoxGeneralSettings.getSelectionModel().selectedIndexProperty().
                addListener((options, oldValue, newValue) ->{
                    onGeneralSettingsChange((Integer) oldValue,(Integer) newValue);
        });

        HBoxGeneralSettings.setDisable(true);
        ButtonShowSolution.setDisable(true);
    }

    private Collection<Node> createMutationNodes(){
        Collection<Node> nodeForMutation = new ArrayList<>();
        nodeForMutation.add(new Label("type"));
        ComboBox<String> mutationType = new ComboBox<String>();
        nodeForMutation.add(mutationType);
        for(EvolutionaryTimeTableModel.MutationDetails mutation : modelRef.GetMutationsDetails()) {
            mutationType.getItems().add(mutation.name);
        }

        mutationType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            List temp = modelRef.GetMutationFieldInfo((int) newValue);
            addMutationConfig(temp);
            LabelSettingsError.setVisible(false);
        });

        return nodeForMutation;
    }

    public void DisableSeeSolution() {
        ButtonShowSolution.setDisable(true);
    }

    private void addMutationConfig(List<FieldInfo> toAdd){

        int start =  4;
        List nodes = new ArrayList();
        Node last = HBoxGeneralSettings.getChildren().get(HBoxGeneralSettings.getChildren().size() - 1);
        HBoxGeneralSettings.getChildren().remove(start,HBoxGeneralSettings.getChildren().size() - 1);
        for(FieldInfo info : toAdd){
            Label name = new Label(info.getFieldName());
            nodes.add(name);
            Node data;
            if(info.getFieldClass().isEnum()){
                ComboBox cb = new ComboBox();
                Object[] enumsNames = info.getFieldClass().getEnumConstants();
                for(Object type : enumsNames){
                    cb.getItems().add(type);
                }
                cb.getSelectionModel().select(info.getFieldValue());
                nodes.add(cb);
            }
            else {
                TextField tf = new TextField();
                tf.setText(info.getFieldValue());
                tf.setMinSize(TextField.USE_COMPUTED_SIZE,TextField.USE_COMPUTED_SIZE);
                tf.setPrefSize(68,25);
                nodes.add(tf);
            }
        }
        HBoxGeneralSettings.getChildren().addAll(start,nodes);
        last.toFront();
    }

    private Collection<Node> createSelectionNodes(){
        Collection<Node> nodeForMutation = new ArrayList<>();
        nodeForMutation.add(new Label("changeTo"));
        ComboBox<String> selectionType = new ComboBox<String>();
        selectionType.getItems().addAll(modelRef.GetSelectionOptions());
        nodeForMutation.add(selectionType);
        //selectionType.getSelectionModel().select(descriptorRef.getEvolutionEngine().getSelection().GetName());
        //addSelectionConfig2(selectionType.getValue());
        selectionType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
//            addSelectionConfig(descriptorRef.getEvolutionEngine().getSelection().getFieldInfo());
            addSelectionConfig2(selectionType.getValue());
            LabelSettingsError.setVisible(false);

        });
        return nodeForMutation;
    }


    private void addSelectionConfig2(String name){
        List<FieldInfo> info;
        switch (name){
            case "Truncation":
                info = Truncation.getStaticFields();
                addSelectionConfig(info);
                break;
            case "Roulette Wheel":
                info = RouletteWheel.getStaticFields();
                addSelectionConfig(info);
                break;
            case "Tournament":
                info = Tournament.getStaticFields();
                addSelectionConfig(info);

        }
    }

    private void addSelectionConfig(List<FieldInfo> toAdd){
        int start =  4;
        List nodes = new ArrayList();
        Node last = HBoxGeneralSettings.getChildren().get(HBoxGeneralSettings.getChildren().size() - 1);
        HBoxGeneralSettings.getChildren().remove(start,HBoxGeneralSettings.getChildren().size() - 1);
        for(FieldInfo info : toAdd){
            Label name = new Label(info.getFieldName());
            nodes.add(name);
            Node data;
            if(info.getFieldClass().isEnum()){
                ComboBox cb = new ComboBox();
                Object[] enumsNames = info.getFieldClass().getEnumConstants();
                for(Object type : enumsNames){
                    cb.getItems().add(type);
                }
                cb.getSelectionModel().select(info.getFieldValue());
                nodes.add(cb);
            }
            else {
                TextField tf = new TextField();
                tf.setText(info.getFieldValue());
                tf.setMinSize(TextField.USE_COMPUTED_SIZE,TextField.USE_COMPUTED_SIZE);
                tf.setPrefSize(68,25);
                nodes.add(tf);
            }
        }
        HBoxGeneralSettings.getChildren().addAll(start,nodes);
        last.toFront();
    }

    private Collection<Node> createElitismNodes(){
        Collection<Node> nodeForElitism = new ArrayList<>();
        nodeForElitism.add(new Label("Elitism"));
        TextField tf = new TextField();
        tf.setText(Integer.toString(modelRef.getElitism()));
        tf.setMinSize(TextField.USE_COMPUTED_SIZE,TextField.USE_COMPUTED_SIZE);
        tf.setPrefSize(68,25);
        nodeForElitism.add(tf);
        return nodeForElitism;

    }

    private Collection<Node> createCrossOverNodes(){
        Collection<Node> nodeForMutation = new ArrayList<>();
        nodeForMutation.add(new Label("changeTo"));
        ComboBox<String> selectionType = new ComboBox<String>();
        selectionType.getItems().addAll(modelRef.GetCrossoverOptions());
        nodeForMutation.add(selectionType);
        //selectionType.getSelectionModel().select(descriptorRef.getEvolutionEngine().getCrossover().GetName());
        selectionType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            addCrossOverConfig2(selectionType.getValue());
            LabelSettingsError.setVisible(false);
        });
        return nodeForMutation;
    }

    private void addCrossOverConfig2(String name){
        List<FieldInfo> info;
        switch (name){
            case "AspectOriented":
                info = AspectOriented.getStaticFields();
                addSelectionConfig(info);
                break;
            case "DayTimeOriented":
                info = DayTimeOriented.getStaticFields();
                addSelectionConfig(info);
                break;
        }
    }

    private void resetSettingsSelection(){
        int startIndexToChange = 2;//this is the index of the combox
        int last = HBoxGeneralSettings.getChildren().size() - 1;
        HBoxGeneralSettings.getChildren().remove(startIndexToChange,last);
    }

    private void onGeneralSettingsChange(Integer oldValue, Integer newValue) {
        LabelSettingsError.setVisible(false);
        int startIndexToChange = 2;//this is the index of the combox
        resetSettingsSelection();
        switch (newValue){
            case 0:
                HBoxGeneralSettings.getChildren().addAll(startIndexToChange,createMutationNodes());
                currentSettingsState = eSettingsType.Mutation;
                break;
            case 1:
                HBoxGeneralSettings.getChildren().addAll(startIndexToChange,createSelectionNodes());
                currentSettingsState = eSettingsType.Selection;
                break;
            case 2:
                HBoxGeneralSettings.getChildren().addAll(startIndexToChange,createElitismNodes());
                currentSettingsState = eSettingsType.Elitism;
                break;
            case 3:
                HBoxGeneralSettings.getChildren().addAll(startIndexToChange,createCrossOverNodes());
                currentSettingsState = eSettingsType.Crossover;
                break;

        }
    }


    public void ResetPage(){
        Platform.runLater(()->{
            ProgressBarFitness.setProgress(0);
            ProgressBarGeneration.setProgress(0);
            ProgressBarTime.setProgress(0);

            LabelBestFitness.setText("");
            LabelGenerationCount.setText("");

            CheckBoxFitness.setSelected(false);
            CheckBoxGenreation.setSelected(false);
            CheckBoxTime.setSelected(false);

            TextFieldFitnessInput.setText("");
            TextFieldGenerationInput.setText("");
            TextFieldTimeInput.setText("");
            TextFieldUpdateFreqInput.setText("");

            ButtonRun.setDisable(true);
            ButtonStop.setDisable(true);
            ButtonResume.setDisable(true);
            ButtonPause.setDisable(true);
        });

    }

    @FXML
    void OnClickShowSolution(ActionEvent event) {
        mainFormController.LoadSolutionPage(event);
    }

    @FXML
    void OnClickUpdate(ActionEvent event) {
        LabelSettingsError.setVisible(false);
        if(((ComboBox)HBoxGeneralSettings.getChildren().get(1)).getSelectionModel().getSelectedIndex() == -1){
            LabelSettingsError.setVisible(true);
            LabelSettingsError.setText("please select setting to change");
            return;
        }
        switch (currentSettingsState){
            case Mutation:
                updateMutationSettings();
                break;
            case Selection:
                updateSelectionSettings();
                break;
            case Elitism:
                updateElitismSettings();
                break;
            case Crossover:
                updateCrossOverSettings();
                break;
        }
    }

    private void updateCrossOverSettings(){
        ComboBox currentMutation = (ComboBox) HBoxGeneralSettings.getChildren().get(3);
        if(currentMutation.getSelectionModel().getSelectedIndex() == -1){
            updateLabelFailure(LabelSettingsError,"please select crossover to change");
            return;
        }
        String crossoverName = (String) currentMutation.getSelectionModel().getSelectedItem();
        Map<String, String> type2Value = getNewConfig();
        try{
            modelRef.UpdateCrossover(crossoverName,type2Value);
            updateLabelSuccess(LabelSettingsError,"Crossover successfully changed.");
        } catch (ExceptionWrongType | ExceptionNotInRange e){
            updateLabelFailure(LabelSettingsError, e.getMessage());
            return;
        }
    }

    private void updateElitismSettings(){
        try{
            modelRef.UpdateElitism(((TextField)HBoxGeneralSettings.getChildren().get(3)).getText());
            updateLabelSuccess(LabelSettingsError,"Elitism successfully changed.");
        }catch (ExceptionWrongType | ExceptionNotInRange e){
            updateLabelFailure(LabelSettingsError,e.getMessage());
            return;
        }
    }

    private void updateSelectionSettings(){
        ComboBox currentMutation = (ComboBox) HBoxGeneralSettings.getChildren().get(3);
        if(currentMutation.getSelectionModel().getSelectedIndex() == -1){
            updateLabelFailure(LabelSettingsError,"please select selection to change");
            return;
        }
        String selectionName = (String) currentMutation.getSelectionModel().getSelectedItem();
        Map<String, String> type2Value = getNewConfig();
        try{
            modelRef.UpdateSelection(selectionName,type2Value);
            updateLabelSuccess(LabelSettingsError,"Selection successfully changed.");
        }
        catch (ExceptionWrongType | ExceptionNotInRange e){
            updateLabelFailure(LabelSettingsError,e.getMessage());
            return;
        }
    }

    private void updateMutationSettings(){
        ComboBox currentMutation = (ComboBox) HBoxGeneralSettings.getChildren().get(3);
        if(currentMutation.getSelectionModel().getSelectedIndex() == -1){
            updateLabelFailure(LabelSettingsError,"please select Mutation to change");
            return;
        }
        int mutationIndex = currentMutation.getSelectionModel().getSelectedIndex();
        Map<String, String> type2Value = getNewConfig();
        try{
            modelRef.UpdateMutation(mutationIndex,type2Value);
            updateLabelSuccess(LabelSettingsError,"Mutation successfully changed.");
        }catch (ExceptionNotInRange | ExceptionWrongType e){
            updateLabelFailure(LabelSettingsError, e.getMessage());
            return;
        }
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

    private Map<String,String> getNewConfig(){
        Map<String, String> type2Value = new HashMap<>();
        for(int i = 4; i < HBoxGeneralSettings.getChildren().size() - 1; i+=2){ //this is to get label and setings
            String key = ((Label)HBoxGeneralSettings.getChildren().get(i)).getText();
            String value;
            Node nodeValue = HBoxGeneralSettings.getChildren().get(i + 1);
            if(nodeValue instanceof TextField){
                value = ((TextField)HBoxGeneralSettings.getChildren().get(i + 1)).getText();
            }
            else {
                value =((ComboBox)HBoxGeneralSettings.getChildren().get(i + 1)).getValue().toString();
            }
            type2Value.put(key,value);
        }
        return type2Value;
    }
}