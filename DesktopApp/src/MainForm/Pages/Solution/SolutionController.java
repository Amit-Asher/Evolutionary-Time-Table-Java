package MainForm.Pages.Solution;

import EvolutionEngineImpl.HistoryDocument;
import MainForm.Pages.Home.HomeController;
import MainForm.Pages.Solution.TableFactory.TableFactory;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher.Teacher;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;
import TimeTableModel.EvolutionaryTimeTableModel;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class SolutionController implements Initializable {

    private EvolutionaryTimeTableModel modelRef;
    private HomeController homeController;
    private TimeTableSolution tts = null;
    private List<HistoryDocument> historyDocuments = null;
    private int currentHistoryIdx = 0;

    @FXML
    private Label LabelHistoryMsg;

    @FXML
    private Label LabelGenerationCount;

    @FXML
    private Button ButtonBack;

    @FXML
    private Button ButtonForward;

    @FXML
    private VBox VboxMain;

    @FXML
    private ComboBox<String> ComboBoxDisplayType;

    @FXML
    private ComboBox<String> ComboBoxComponent;

    @FXML
    private VBox VBoxRules;

    @FXML
    private Label LabelComponent;

    @FXML
    private VBox VBoxTimeTable;

    public VBox getVboxMain() {
        return VboxMain;
    }

    public void setDescriptorRef(EvolutionaryTimeTableModel modelRef) {
        this.modelRef = modelRef;
    }

    private void setTableViewByTeacher(Teacher teacher) {
        VBoxTimeTable.getChildren().clear();
        VBoxTimeTable.getChildren().add(TableFactory.CreateTableViewByTeacher(tts, teacher,
                modelRef.GetDays(), modelRef.GetHours()));
    }

    private void setTableViewByClass(Clas clas) {
        VBoxTimeTable.getChildren().clear();
        VBoxTimeTable.getChildren().add(TableFactory.CreateTableViewByClass(tts, clas,
                modelRef.GetDays(), modelRef.GetHours()));
    }


    private void handleComboBoxComponent() {
        switch (ComboBoxDisplayType.getValue()) {
            case "Teachers":
                Teacher selectedTeacher = modelRef.GetTeachers().stream().
                        filter(teacher -> teacher.getName().equals(ComboBoxComponent.getValue())).
                        findAny().orElse(null);
                setTableViewByTeacher(selectedTeacher);
                break;
            case "Classes":
                Clas selectedClass = modelRef.GetClasses().stream().
                        filter(clas -> clas.getName().equals(ComboBoxComponent.getValue())).
                        findAny().orElse(null);
                setTableViewByClass(selectedClass);
                break;
            case "Raw":
                break;
        }
    }


    private void handleComboBoxDisplayType() {
        switch(ComboBoxDisplayType.getValue()) {
            case "Teachers":
                LabelComponent.setText("Teacher: ");
                LabelComponent.setVisible(true);
                ComboBoxComponent.setVisible(true);
                ComboBoxComponent.getItems().clear();
                modelRef.GetTeachers().forEach(teacher -> ComboBoxComponent.getItems().add(teacher.getName()));
                break;
            case "Classes":
                LabelComponent.setText("Class: ");
                LabelComponent.setVisible(true);
                ComboBoxComponent.setVisible(true);
                ComboBoxComponent.getItems().clear();
                modelRef.GetClasses().forEach(clas -> ComboBoxComponent.getItems().add(clas.getName()));
                break;
            case "Raw":
                LabelComponent.setVisible(false);
                ComboBoxComponent.setVisible(false);
                VBoxTimeTable.getChildren().clear();
                VBoxTimeTable.getChildren().add(TableFactory.CreateTableViewByRaw(tts));
                break;
        }
    }

    public void LoadBestSolution() {
        tts = modelRef.GetBestSolution();
        drawRules();
    }

    private void drawRules() {
        VBoxRules.getChildren().clear();
        List<EvolutionaryTimeTableModel.RulePerformence> rulesPerformances = modelRef.GetBestSolutionRulesPerformances();
        rulesPerformances.forEach(rulePerformence -> {
            HBox HBoxRule = new HBox();
            HBoxRule.getChildren().add(new Label("Rule name: " + rulePerformence.rule.GetRuleId() + " | "));

            Map<String, String> config = rulePerformence.rule.GetConfig();
            config.entrySet().stream().forEach(e -> {
                HBoxRule.getChildren().add(new Label(e.getKey() + "=" + e.getValue()+ "; "));
            });

            Double score =  rulePerformence.rule.Run(tts);
            HBoxRule.getChildren().add(new Label("| Score: " + Double.toString(score)));
            VBoxRules.getChildren().add(HBoxRule);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String[] displayTypes = { "Teachers", "Classes", "Raw" };
        ComboBoxDisplayType.setItems(FXCollections.observableArrayList(displayTypes));

        EventHandler<ActionEvent> eventDisplayType = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                handleComboBoxDisplayType();
            }
        };
        ComboBoxDisplayType.setOnAction(eventDisplayType);


        EventHandler<ActionEvent> eventComponent = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                handleComboBoxComponent();
            }
        };
        ComboBoxComponent.setOnAction(eventComponent);

        LabelComponent.setVisible(false);
        ComboBoxComponent.setVisible(false);

        ButtonBack.setVisible(false);
        ButtonForward.setVisible(false);
        LabelGenerationCount.setVisible(false);
        LabelHistoryMsg.setVisible(false);
    }


    public void EnableTimeTraverse() {
        ButtonBack.setVisible(true);
        ButtonForward.setVisible(true);
        LabelGenerationCount.setVisible(true);
        LabelHistoryMsg.setVisible(true);
        historyDocuments = modelRef.GetGenerationHistory();
        currentHistoryIdx = historyDocuments.size() - 1;
        LabelGenerationCount.setText(Integer.toString(historyDocuments.get(currentHistoryIdx).getGenerationDoc()));
    }

    public void DisableTimeTraverse() {
        ButtonBack.setVisible(false);
        ButtonForward.setVisible(false);
        LabelGenerationCount.setVisible(false);
        LabelHistoryMsg.setVisible(false);
    }


    @FXML
    void OnClickBack(ActionEvent event) {
        if(currentHistoryIdx != 1) {
            currentHistoryIdx--;
            tts = (TimeTableSolution) historyDocuments.get(currentHistoryIdx).getSolutionDoc();
            drawRules();
            VBoxTimeTable.getChildren().clear();
            LabelGenerationCount.setText(Integer.toString(historyDocuments.get(currentHistoryIdx).getGenerationDoc()));
        }
    }

    @FXML
    void OnClickForward(ActionEvent event) {
        if(currentHistoryIdx != historyDocuments.size() - 1) {
            currentHistoryIdx++;
            tts = (TimeTableSolution) historyDocuments.get(currentHistoryIdx).getSolutionDoc();
            drawRules();
            VBoxTimeTable.getChildren().clear();
            LabelGenerationCount.setText(Integer.toString(historyDocuments.get(currentHistoryIdx).getGenerationDoc()));
        }
    }
}