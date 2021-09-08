package MainForm.Pages.Solution.TableFactory;


import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject.Subject;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher.Teacher;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Comparator;
import java.util.stream.Collectors;

public class TableFactory {

    public static TableView<RawSolution> CreateTableViewByRaw(TimeTableSolution solution) {

        TableView<RawSolution> tv = new TableView<RawSolution>();
        TableColumn dayColumn = new TableColumn("Day");
        TableColumn hourColumn = new TableColumn("Hour");
        TableColumn classColumn = new TableColumn("Class");
        TableColumn teacherColumn = new TableColumn("Teacher");
        TableColumn subjectColumn = new TableColumn("Subject");

        dayColumn.setCellValueFactory(new PropertyValueFactory<RawSolution, Integer>("day"));
        hourColumn.setCellValueFactory(new PropertyValueFactory<RawSolution, Integer>("hour"));
        classColumn.setCellValueFactory(new PropertyValueFactory<RawSolution, String>("clas"));
        teacherColumn.setCellValueFactory(new PropertyValueFactory<RawSolution, String>("teacher"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<RawSolution, String>("subject"));

        tv.getColumns().add(dayColumn);
        tv.getColumns().add(hourColumn);
        tv.getColumns().add(classColumn);
        tv.getColumns().add(teacherColumn);
        tv.getColumns().add(subjectColumn);

        ObservableList<RawSolution> gens = FXCollections.observableArrayList();
        solution.getGens().stream().
                sorted(Comparator.comparingDouble(g -> g.getSubject().getId())). // S
                sorted(Comparator.comparingDouble(g -> g.getTeacher().getId())). // T
                sorted(Comparator.comparingDouble(g -> g.getClas().getId())). // C
                sorted(Comparator.comparingDouble(Gen::getHour)). // H
                sorted(Comparator.comparingDouble(Gen::getDay)). // D
                forEach((gen) -> {
            gens.add(new RawSolution(
                    gen.getDay(),
                    gen.getHour(),
                    gen.getClas().getName(),
                    gen.getTeacher().getName(),
                    gen.getSubject().getName()));
        });

        tv.setItems(gens);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tv;
    }


    public static GridPane CreateTableViewByTeacher(TimeTableSolution solution, Teacher teacher, int days, int hours) {

        GridPane schedule = new GridPane();
        int horizontalGridCount = days;
        int verticalGridCount = hours;

        setGridConstraints(schedule, horizontalGridCount, verticalGridCount);
        setGridHeaders(schedule, horizontalGridCount, verticalGridCount);
        loadDataToTeacherGrid(solution, schedule, horizontalGridCount, verticalGridCount, teacher);
        schedule.gridLinesVisibleProperty().set(true);
        return schedule;
    }


    public static GridPane CreateTableViewByClass(TimeTableSolution solution, Clas clas, int days, int hours) {

        GridPane schedule = new GridPane();
        int horizontalGridCount = days;
        int verticalGridCount = hours;

        setGridConstraints(schedule, horizontalGridCount, verticalGridCount);
        setGridHeaders(schedule, horizontalGridCount, verticalGridCount);
        loadDataToClassGrid(solution, schedule, horizontalGridCount, verticalGridCount, clas);
        schedule.gridLinesVisibleProperty().set(true);
        return schedule;
    }


    private static void setGridConstraints(GridPane schedule, int horizontalGridCount, int verticalGridCount) {
        for (int j = 0; j < horizontalGridCount + 1; ++j) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0/horizontalGridCount);
            schedule.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < verticalGridCount + 1; ++i) {
            for (int j = 0; j < horizontalGridCount + 1; ++j) {
                final Region grid = new Region();
                grid.setStyle("-fx-background-color: #FFFFFF;");
                grid.setPrefHeight(30);
                grid.setPrefWidth(10);
                schedule.add(grid, j, i);
            }
        }
    }

    private static Label createLabel(String val) {
        Label cell = new Label(val);
        cell.setMaxWidth(Double.MAX_VALUE);
        cell.setAlignment(Pos.CENTER);
        return cell;
    }

    private static void setGridHeaders(GridPane schedule, int horizontalGridCount, int verticalGridCount) {
        schedule.add(createLabel("Hours/Days"), 0, 0);

        for(int i=1; i <= horizontalGridCount; i++) {
            schedule.add(createLabel(Integer.toString(i)), i, 0);
        }

        for(int i=1; i <= verticalGridCount; i++) {
            schedule.add(createLabel(Integer.toString(i)), 0, i);
        }
    }

    private static void loadDataToTeacherGrid(TimeTableSolution solution, GridPane schedule,
                                       int horizontalGridCount, int verticalGridCount,
                                       Teacher teacher) {
        for(int hour=1; hour <= verticalGridCount; hour++) {

            for(int day=1; day <= horizontalGridCount; day++) {
                // java is stupid
                int finalDay = day;
                int finalHour = hour;

                VBox newCell = new VBox();
                solution.getGens().stream().
                        filter(item -> item.getDay() + 1 == finalDay &&
                                item.getHour() + 1 == finalHour &&
                                item.getTeacher().equals(teacher)).
                        collect(Collectors.toList()).forEach(gen ->
                        {
                            Clas clas = gen.getClas();
                            Subject subject = gen.getSubject();
                            String cellVal = clas.getId() + "(" + clas.getName() + ")- " +
                                    subject.getId() + "(" + subject.getName() + ")";

                            newCell.getChildren().add(createLabel(cellVal));
                        }
                );

                schedule.add(newCell,finalDay, finalHour);
            }
        }
    }


    private static void loadDataToClassGrid(TimeTableSolution solution, GridPane schedule,
                                              int horizontalGridCount, int verticalGridCount,
                                              Clas clas) {
        for(int hour=1; hour <= verticalGridCount; hour++) {
            for(int day=1; day <= horizontalGridCount; day++) {
                // java is stupid
                int finalDay = day;
                int finalHour = hour;

                VBox newCell = new VBox();
                solution.getGens().stream().
                        filter(item -> item.getDay() + 1 == finalDay &&
                                item.getHour() + 1 == finalHour &&
                                item.getClas().equals(clas)).
                        collect(Collectors.toList()).forEach(gen ->
                        {
                            Teacher teacher = gen.getTeacher();
                            Subject subject = gen.getSubject();
                            String cellVal = teacher.getId() + "(" + teacher.getName() + ")- " +
                                    subject.getId() + "(" + subject.getName() + ")";

                            newCell.getChildren().add(createLabel(cellVal));
                        }
                );

                schedule.add(newCell,finalDay, finalHour);
            }
        }
    }
}