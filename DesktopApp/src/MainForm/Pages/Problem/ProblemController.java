package MainForm.Pages.Problem;

import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject.Subject;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher.Teacher;
import TimeTableModel.EvolutionaryTimeTableModel;
import TimeTableModel.EvolutionaryTimeTableModel.MutationDetails;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.List;

public class ProblemController {

    private EvolutionaryTimeTableModel modelRef;

    @FXML
    private TableView<SubjectTableView> TableViewSubjects;

    @FXML
    private TableView<TeachersTableView> TableViewTeachers;

    @FXML
    private TableView<ClassesTableView> TableViewClasses;

    @FXML
    private TableView<RulesTableView> TableViewRules;

    @FXML
    private VBox VboxMain;

    @FXML
    private Label labelPopulationSize;

    @FXML
    private Label labelSelectionTechnique;

    @FXML
    private Label labelSelectionConfig;

    @FXML
    private Label labelCrossoverType;

    @FXML
    private Label labelCrossoverCuttingPoints;

    @FXML
    private Label labelCrossoverConfig;

    @FXML
    private VBox VBoxMutations;

    @FXML
    private TableColumn<SubjectTableView, Integer> SubjectsTableColumnID;

    @FXML
    private TableColumn<SubjectTableView, String> SubjectsTableColumnName;

    @FXML
    private TableColumn<TeachersTableView, String> TeachersTableColumnTeacherID;

    @FXML
    private TableColumn<TeachersTableView, String> TeachersTableColumnTeacherName;

    @FXML
    private TableColumn<TeachersTableView, String> TeachersTableColumnSubjectID;

    @FXML
    private TableColumn<TeachersTableView, String> TeachersTableColumnSubjectName;

    @FXML
    private TableColumn<ClassesTableView, String> ClassesTableColumnClassID;

    @FXML
    private TableColumn<ClassesTableView, String> ClassesTableColumnClassName;

    @FXML
    private TableColumn<ClassesTableView, String> ClassesTableColumnSubjectID;

    @FXML
    private TableColumn<ClassesTableView, String> ClassesTableColumnSubjectName;

    @FXML
    private TableColumn<ClassesTableView, String> ClassesTableColumnHours;

    @FXML
    private TableColumn<RulesTableView, String> RulesTableColumnName;

    @FXML
    private TableColumn<RulesTableView, String> RulesTableColumnType;


    public class SubjectTableView {
        private final SimpleStringProperty name;
        private final SimpleIntegerProperty id;

        public SubjectTableView(String name, int id) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleIntegerProperty(id);
        }

        public String getName() {
            return name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public int getId() {
            return id.get();
        }

        public void setId(int id) {
            this.id.set(id);
        }
    }

    public class TeachersTableView {
        private final SimpleStringProperty teacherId;
        private final SimpleStringProperty teacherName;
        private final SimpleStringProperty subjectId;
        private final SimpleStringProperty subjectName;

        public TeachersTableView(String teacherid, String teachername, String subjectid, String subjectname) {
            this.teacherId = new SimpleStringProperty(teacherid);
            this.teacherName = new SimpleStringProperty(teachername);
            this.subjectId = new SimpleStringProperty(subjectid);
            this.subjectName = new SimpleStringProperty(subjectname);
        }

        public String getTeacherId() {
            return teacherId.get();
        }

        public void setTeacherId(String teacherId) {
            this.teacherId.set(teacherId);
        }

        public String getTeacherName() {
            return teacherName.get();
        }

        public void setTeacherName(String teacherName) {
            this.teacherName.set(teacherName);
        }

        public String getSubjectId() {
            return subjectId.get();
        }

        public void setSubjectId(String subjectId) {
            this.subjectId.set(subjectId);
        }

        public String getSubjectName() {
            return subjectName.get();
        }

        public void setSubjectName(String subjectName) {
            this.subjectName.set(subjectName);
        }
    }

    public class ClassesTableView {
        private final SimpleStringProperty classId;
        private final SimpleStringProperty className;
        private final SimpleStringProperty subjectId;
        private final SimpleStringProperty subjectName;
        private final SimpleStringProperty subjectHours;

        public ClassesTableView(String classId, String className, String subjectId, String subjectName, String subjectHours) {
            this.classId = new SimpleStringProperty(classId);
            this.className = new SimpleStringProperty(className);
            this.subjectId = new SimpleStringProperty(subjectId);
            this.subjectName = new SimpleStringProperty(subjectName);
            this.subjectHours = new SimpleStringProperty(subjectHours);
        }

        public String getClassId() {
            return classId.get();
        }

        public void setClassId(String classId) {
            this.classId.set(classId);
        }

        public String getClassName() {
            return className.get();
        }

        public void setClassName(String className) {
            this.className.set(className);
        }

        public String getSubjectId() {
            return subjectId.get();
        }

        public void setSubjectId(String subjectId) {
            this.subjectId.set(subjectId);
        }

        public String getSubjectName() {
            return subjectName.get();
        }

        public void setSubjectName(String subjectName) {
            this.subjectName.set(subjectName);
        }

        public String getSubjectHours() {
            return subjectHours.get();
        }

        public void setSubjectHours(String subjectHours) {
            this.subjectHours.set(subjectHours);
        }
    }

    public class RulesTableView {
        private final SimpleStringProperty ruleName;
        private final SimpleStringProperty ruleType;

        public RulesTableView(String ruleName, String ruleType) {
            this.ruleName = new SimpleStringProperty(ruleName);
            this.ruleType = new SimpleStringProperty(ruleType);
        }

        public String getRuleName() {
            return ruleName.get();
        }

        public void setRuleName(String ruleName) {
            this.ruleName.set(ruleName);
        }

        public String getRuleType() {
            return ruleType.get();
        }

        public void setRuleType(String ruleType) {
            this.ruleType.set(ruleType);
        }
    }


    private boolean isLoaded = false;

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    public VBox getVboxMain() {
        return VboxMain;
    }

    public void setDescriptorRef(EvolutionaryTimeTableModel modelRef) {
        this.modelRef = modelRef;
    }

    private void insertMutationsToPage() {

        List<MutationDetails> mutations = modelRef.GetMutationsDetails();
        VBoxMutations.getChildren().clear();

        String keyMutationName = "Mutation: ";
        String keyMutationProbability = "Probability: ";
        String keyMutationConfig = "Configurations: ";

        for(MutationDetails mutation : mutations) {
            VBox vboxMutation = new VBox();
            vboxMutation.setSpacing(5);

            HBox mutationName = new HBox();
            mutationName.setPadding(new Insets(0, 10, 0, 10));
            mutationName.setSpacing(10);
            mutationName.getChildren().add(new Label(keyMutationName));
            mutationName.getChildren().add(new Label(mutation.name));
            vboxMutation.getChildren().add(mutationName);

            HBox mutationProbability = new HBox();
            mutationProbability.setPadding(new Insets(0, 10, 0, 10));
            mutationProbability.setSpacing(10);
            mutationProbability.getChildren().add(new Label(keyMutationProbability));
            mutationProbability.getChildren().add(new Label(Double.toString(mutation.probability)));
            vboxMutation.getChildren().add(mutationProbability);

            HBox mutationConfiguration = new HBox();
            mutationConfiguration.setPadding(new Insets(0, 10, 0, 10));
            mutationConfiguration.setSpacing(10);
            mutationConfiguration.getChildren().add(new Label(keyMutationConfig));
            mutationConfiguration.getChildren().add(new Label(mutation.config));
            vboxMutation.getChildren().add(mutationConfiguration);

            VBoxMutations.getChildren().add(vboxMutation);
        }
    }

    private void setTableHeight(TableView tableview) {
        tableview.setFixedCellSize(25);
        tableview.prefHeightProperty().bind(tableview.fixedCellSizeProperty().multiply(Bindings.size(tableview.getItems()).add(1.03)));
        tableview.minHeightProperty().bind(tableview.prefHeightProperty());
        tableview.maxHeightProperty().bind(tableview.prefHeightProperty());
    }

    private void setSubjectsTable() {
        ObservableList<SubjectTableView> subjects = FXCollections.observableArrayList();

        modelRef.GetSubjects().forEach((key, val) -> {
            subjects.add(new SubjectTableView(val.getName(), key));
        });

        SubjectsTableColumnID.setCellValueFactory(new PropertyValueFactory<SubjectTableView, Integer>("id"));
        SubjectsTableColumnName.setCellValueFactory(new PropertyValueFactory<SubjectTableView, String>("name"));
        TableViewSubjects.setItems(subjects);
        setTableHeight(TableViewSubjects);
    }

    private void setTeachersTable() {
        ObservableList<TeachersTableView> teachersSubjects = FXCollections.observableArrayList();



        modelRef.GetTeachers().
                stream().
                sorted(Comparator.comparingInt(Teacher::getId)).
                forEach(teacher -> {
                    final boolean[] first = {true};
                    teacher.getSubjectsCanTeach().
                            stream().
                            sorted(Comparator.comparingInt(Subject::getId)).
                            forEach(subject -> {
                                if (first[0]) {
                                    teachersSubjects.add(new TeachersTableView(Integer.toString(teacher.getId()), teacher.getName(), Integer.toString(subject.getId()), subject.getName()));
                                    first[0] = false;
                                } else {
                                    teachersSubjects.add(new TeachersTableView("", "", Integer.toString(subject.getId()), subject.getName()));
                                }
                            });
                });

        TeachersTableColumnTeacherID.setCellValueFactory(new PropertyValueFactory<TeachersTableView, String>("teacherId"));
        TeachersTableColumnTeacherName.setCellValueFactory(new PropertyValueFactory<TeachersTableView, String>("teacherName"));
        TeachersTableColumnSubjectID.setCellValueFactory(new PropertyValueFactory<TeachersTableView, String>("subjectId"));
        TeachersTableColumnSubjectName.setCellValueFactory(new PropertyValueFactory<TeachersTableView, String>("subjectName"));
        TableViewTeachers.setItems(teachersSubjects);
        setTableHeight(TableViewTeachers);
    }

    private void setClassesTable() {
        ObservableList<ClassesTableView> classesSubjects = FXCollections.observableArrayList();

        modelRef.GetClasses().
                stream().
                sorted(Comparator.comparingInt(Clas::getId)).
                forEach(clas -> {
                    final boolean[] first = {true};
                    clas.getRequirementsSubject2Hours().entrySet().
                            stream().
                            sorted(Comparator.comparingInt(e -> e.getKey().getId())).
                            forEach(e -> {
                                Subject subject = e.getKey();
                                int hours = e.getValue();
                                if (first[0]) {
                                    classesSubjects.add(new ClassesTableView(Integer.toString(clas.getId()), clas.getName(), Integer.toString(subject.getId()), subject.getName(), Integer.toString(hours)));
                                    first[0] = false;
                                } else {
                                    classesSubjects.add(new ClassesTableView("", "", Integer.toString(subject.getId()), subject.getName(), Integer.toString(hours)));
                                }
                            });
                });

        ClassesTableColumnClassID.setCellValueFactory(new PropertyValueFactory<ClassesTableView, String>("classId"));
        ClassesTableColumnClassName.setCellValueFactory(new PropertyValueFactory<ClassesTableView, String>("className"));
        ClassesTableColumnSubjectID.setCellValueFactory(new PropertyValueFactory<ClassesTableView, String>("subjectId"));
        ClassesTableColumnSubjectName.setCellValueFactory(new PropertyValueFactory<ClassesTableView, String>("subjectName"));
        ClassesTableColumnHours.setCellValueFactory(new PropertyValueFactory<ClassesTableView, String>("subjectHours"));
        TableViewClasses.setItems(classesSubjects);
        setTableHeight(TableViewClasses);

    }

    private void setRulesTable() {
        ObservableList<RulesTableView> rules = FXCollections.observableArrayList();

        modelRef.GetRules().forEach(rule -> {
                    rules.add(new RulesTableView(rule.GetRuleId(), rule.GetRuleType().name()));
                });

        RulesTableColumnName.setCellValueFactory(new PropertyValueFactory<RulesTableView, String>("ruleName"));
        RulesTableColumnType.setCellValueFactory(new PropertyValueFactory<RulesTableView, String>("ruleType"));
        TableViewRules.setItems(rules);
        setTableHeight(TableViewRules);
    }

    public void loadData() {

        /* Evolution engine settings */
        this.labelPopulationSize.setText(Integer.toString(modelRef.GetPopulationSize()));
        this.labelSelectionTechnique.setText(modelRef.GetSelectionTechnique());
        this.labelSelectionConfig.setText(modelRef.GetSelectionConfig());
        this.labelCrossoverType.setText(modelRef.GetCrossoverType());
        this.labelCrossoverCuttingPoints.setText(Integer.toString(modelRef.GetCrossoverCuttingPoints()));
        this.labelCrossoverConfig.setText(modelRef.GetCrossoverConfig());
        insertMutationsToPage();

        /* Problem settings */

        setSubjectsTable();
        setTeachersTable();
        setClassesTable();
        setRulesTable();

        isLoaded = true;
    }
}