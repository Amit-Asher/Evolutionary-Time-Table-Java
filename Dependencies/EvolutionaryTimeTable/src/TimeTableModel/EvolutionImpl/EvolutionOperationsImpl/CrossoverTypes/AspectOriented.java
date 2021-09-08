package TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.CrossoverTypes;

import EvolutionModel.Components.Info.FieldInfo;
import EvolutionModel.Operations.Crossover.Crossover;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher.Teacher;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableProblem;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class AspectOriented implements Crossover<TimeTableSolution> {
    private int cuttingPoints;
    private int hours;
    private int days;
    private Random rand = new Random();
    private TimeTableProblem problemRef;
    private eAspectType aspectType;

    public enum eAspectType {
        TEACHER,
        CLASS
    }

    public AspectOriented(int cuttingPoints, int days, int hours,
                          TimeTableProblem problemRef, eAspectType aspectType) {
        this.cuttingPoints = cuttingPoints;
        this.hours = hours;
        this.days = days;
        this.aspectType = aspectType;
        this.problemRef = problemRef;
    }

    @Override
    public int GetNumberOfCuttingPoints() {
        return cuttingPoints;
    }

    @Override
    public String GetName() {
        return "AspectOriented";
    }

    @Override
    public String GetConfiguration() {
        return "Orientation = " + aspectType.name();
    }

    /* Universal methods */

    @Override
    public Pair<TimeTableSolution, TimeTableSolution> Run(TimeTableSolution parent1,
                                                          TimeTableSolution parent2) {
        if(aspectType.equals(eAspectType.CLASS)) {
            return runClassAspectOriented(parent1, parent2);
        }
        else {
            return runTeacherAspectOriented(parent1, parent2);
        }
    }

    private SortedSet<Integer> getRandomCuttingPoints(){
        SortedSet<Integer> cuttingPointsSet = new TreeSet<>();
        int minRange = 1; //need to cut at least one 1 five
        int maxRange = days * hours - 1; //need to cut at least 1 five

        while(cuttingPointsSet.size() < cuttingPoints){
            cuttingPointsSet.add(ThreadLocalRandom.current().nextInt(minRange, maxRange));
        }
        return cuttingPointsSet;
    }

    private void addGensInCurrentSectionToChild(List<Gen> aspectDHForParent1,
                                                List<Gen> preNewChild1, int start, int end) {
        preNewChild1.addAll(aspectDHForParent1.stream().filter(p->{
            return p.getDay() * hours + p.getHour() < end && p.getDay() * hours + p.getHour() >= start;
        }).collect(Collectors.toList()));
    }

    private void commitCrossover(SortedSet<Integer> cuttingPoints, List<Gen> preNewChild1,
                                 List<Gen> preNewChild2, List<Gen> aspectDHForParent1,
                                 List<Gen> aspectDHForParent2) {
        int lastInd = 0;
        for(Integer index : cuttingPoints){

            addGensInCurrentSectionToChild(aspectDHForParent1, preNewChild1, lastInd, index);
            addGensInCurrentSectionToChild(aspectDHForParent2, preNewChild2, lastInd, index);

            /* swap lists references */
            List<Gen> tmp = preNewChild1;
            preNewChild1 = preNewChild2;
            preNewChild2 = tmp;
            lastInd = index;
        }

        addGensInCurrentSectionToChild(aspectDHForParent1, preNewChild1, lastInd, days * hours);
        addGensInCurrentSectionToChild(aspectDHForParent2, preNewChild2, lastInd, days * hours);
    }

    private Pair<TimeTableSolution, TimeTableSolution> buildTimeTableSolutionsOfChilds(
            Pair<List<Gen>, List<Gen>> reduceLists) {
        TimeTableSolution child1 = new TimeTableSolution(reduceLists.getKey());
        TimeTableSolution child2 = new TimeTableSolution(reduceLists.getValue());

        return new Pair<>(child1, child2);
    }


    /* Teacher Aspect */

    private Pair<TimeTableSolution, TimeTableSolution> runTeacherAspectOriented(TimeTableSolution parent1,
                                                                                TimeTableSolution parent2) {
        SortedSet<Integer> cuttingPoints = getRandomCuttingPoints();
        Pair<List<Gen>, List<Gen>> newChildsListsWithDuplicates = new Pair<>(new ArrayList<>(), new ArrayList<>());
        List<Teacher> teachers = problemRef.getTeachers();

        for(Teacher teacher : teachers) {
            Pair<List<Gen>, List<Gen>> teacherDHForParents = getTeacherDHLists(teacher, parent1, parent2);
            commitCrossover(cuttingPoints,
                    newChildsListsWithDuplicates.getKey(),
                    newChildsListsWithDuplicates.getValue(),
                    teacherDHForParents.getKey(),
                    teacherDHForParents.getValue());
        }

        Pair<List<Gen>, List<Gen>> reduceLists = reduceChildsDuplicatesTeachersForEachDHC(newChildsListsWithDuplicates);
        return buildTimeTableSolutionsOfChilds(reduceLists);
    }

    private Pair<List<Gen>, List<Gen>> getTeacherDHLists(Teacher teacher, TimeTableSolution parent1,
                                                         TimeTableSolution parent2) {
        List<Gen> teacherDHForParent1 = getTeacherDHList(teacher, parent1);
        List<Gen> teacherDHForParent2 = getTeacherDHList(teacher, parent2);
        return new Pair<>(teacherDHForParent1, teacherDHForParent2);
    }

    private List<Gen> getTeacherDHList(Teacher teacher, TimeTableSolution parent) {
        return parent.getGens().stream().
                filter(item -> item.getTeacher().equals(teacher)).
                collect(Collectors.toList());
    }

    private Pair<List<Gen>, List<Gen>> reduceChildsDuplicatesTeachersForEachDHC(
            Pair<List<Gen>, List<Gen>> newChildsListsWithDuplicates) {

        List<Gen> reduceList1 = reduceChildDuplicatesTeachersForEachDHC(newChildsListsWithDuplicates.getKey());
        List<Gen> reduceList2 = reduceChildDuplicatesTeachersForEachDHC(newChildsListsWithDuplicates.getValue());

        return new Pair<>(reduceList1, reduceList2);
    }

    private List<Gen> reduceChildDuplicatesTeachersForEachDHC(List<Gen> childWithDuplicates) {
        List<Gen> childWithoutDuplicates = new ArrayList<>();
        for(int day=0; day< days; day++) {
            for(int hour=0; hour< hours; hour++) {
                for(Clas clas : problemRef.getClasses()){
                    addSingleGenToChildFromDuplicatesDHC(childWithDuplicates, childWithoutDuplicates, day, hour, clas);
                }
            }
        }
        return childWithoutDuplicates;
    }

    private void addSingleGenToChildFromDuplicatesDHC(List<Gen> childWithDuplicates,
                                                      List<Gen> childWithoutDuplicates,
                                                      int day, int hour, Clas clas) {
        childWithDuplicates.stream().
                filter(item -> item.getDay() == day && item.getHour() == hour && item.getClas().equals( clas)).
                limit(1).collect(Collectors.toList()).forEach(gen ->
                {
                    Gen newGen = new Gen(gen.getDay(),
                            gen.getHour(),
                            gen.getClas(),
                            gen.getTeacher(),
                            gen.getSubject());
                    childWithoutDuplicates.add(newGen);
                }
        );
    }

    /* Class Aspect */

    private Pair<TimeTableSolution, TimeTableSolution> runClassAspectOriented(TimeTableSolution parent1,
                                                                              TimeTableSolution parent2) {
        SortedSet<Integer> cuttingPoints = getRandomCuttingPoints();
        Pair<List<Gen>, List<Gen>> newChildsListsWithDuplicates = new Pair<>(new ArrayList<>(), new ArrayList<>());
        List<Clas> classes = problemRef.getClasses();

        for(Clas clas : classes) {
            Pair<List<Gen>, List<Gen>> classDHForParents = getClassDHLists(clas, parent1, parent2);
            commitCrossover(cuttingPoints,
                    newChildsListsWithDuplicates.getKey(),
                    newChildsListsWithDuplicates.getValue(),
                    classDHForParents.getKey(),
                    classDHForParents.getValue());
        }

        Pair<List<Gen>, List<Gen>> reduceLists = reduceChildsDuplicatesClassesForEachDHT(newChildsListsWithDuplicates);
        return buildTimeTableSolutionsOfChilds(reduceLists);
    }

    private Pair<List<Gen>, List<Gen>> getClassDHLists(Clas clas, TimeTableSolution parent1,
                                                         TimeTableSolution parent2) {
        List<Gen> classDHForParent1 = getClassDHList(clas, parent1);
        List<Gen> classDHForParent2 = getClassDHList(clas, parent2);
        return new Pair<>(classDHForParent1, classDHForParent2);
    }

    private List<Gen> getClassDHList(Clas clas, TimeTableSolution parent) {
        return parent.getGens().stream().
                filter(item -> item.getClas().equals(clas)).
                collect(Collectors.toList());
    }

    private Pair<List<Gen>, List<Gen>> reduceChildsDuplicatesClassesForEachDHT(
            Pair<List<Gen>, List<Gen>> newChildsListsWithDuplicates) {

        List<Gen> reduceList1 = reduceChildDuplicatesClassesForEachDHT(newChildsListsWithDuplicates.getKey());
        List<Gen> reduceList2 = reduceChildDuplicatesClassesForEachDHT(newChildsListsWithDuplicates.getValue());

        return new Pair<>(reduceList1, reduceList2);
    }

    private List<Gen> reduceChildDuplicatesClassesForEachDHT(List<Gen> childWithDuplicates) {
        List<Gen> childWithoutDuplicates = new ArrayList<>();
        for(int day=0; day< days; day++) {
            for(int hour=0; hour< hours; hour++) {
                for(Teacher teacher : problemRef.getTeachers()){
                    addSingleGenToChildFromDuplicatesDHT(childWithDuplicates, childWithoutDuplicates, day, hour, teacher);
                }
            }
        }
        return childWithoutDuplicates;
    }

    private void addSingleGenToChildFromDuplicatesDHT(List<Gen> childWithDuplicates,
                                                      List<Gen> childWithoutDuplicates,
                                                      int day, int hour, Teacher teacher) {
        childWithDuplicates.stream().
                filter(item -> item.getDay() == day && item.getHour() == hour && item.getTeacher().equals( teacher)).
                limit(1).collect(Collectors.toList()).forEach(gen ->
                {
                    Gen newGen = new Gen(gen.getDay(),
                            gen.getHour(),
                            gen.getClas(),
                            gen.getTeacher(),
                            gen.getSubject());
                    childWithoutDuplicates.add(newGen);
                }
        );
    }

    public static List<FieldInfo> getStaticFields(){
        List<FieldInfo> fields = new ArrayList<>();
        fields.add(new FieldInfo("Cutting Points","1",Integer.TYPE));
        fields.add(new FieldInfo("Orientation", eAspectType.CLASS.name(),eAspectType.class));
        return fields;
    }
}
