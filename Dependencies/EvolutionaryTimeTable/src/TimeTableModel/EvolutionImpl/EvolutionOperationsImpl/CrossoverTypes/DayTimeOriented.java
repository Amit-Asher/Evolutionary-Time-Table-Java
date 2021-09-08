package TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.CrossoverTypes;

import EvolutionModel.Components.Info.FieldInfo;
import EvolutionModel.Operations.Crossover.Crossover;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DayTimeOriented implements Crossover<TimeTableSolution> {
    private int cuttingPoints;
    private int hours;
    private int days;
    private Random rand = new Random();

    public DayTimeOriented( int cuttingPoints, int days, int hours) {
        this.cuttingPoints = cuttingPoints;
        this.days = days;
        this.hours = hours;
    }

    @Override
    public int GetNumberOfCuttingPoints() {
        return cuttingPoints;
    }

    @Override
    public String GetName() {
        return "DayTimeOriented";
    }

    @Override
    public String GetConfiguration() {
        return "No Configuration";
    }

    @Override
    public Pair<TimeTableSolution, TimeTableSolution> Run(TimeTableSolution parent1, TimeTableSolution parent2) {
        SortedSet<Integer> cuttingPoints = getRandomCuttingPoints();
        List<Gen> child1 = new ArrayList<>();
        List<Gen> child2 = new ArrayList<>();
        int lastInd = 0;
        List<Gen> curr1 =  parent1.getGens();
        List<Gen> curr2 =  parent2.getGens();
        List<Gen> buffer = new ArrayList<>();
        for(Integer index : cuttingPoints){
            int finalLastInd = lastInd;

            child1.addAll(curr1.stream().filter(p->{
                return p.getDay() * hours + p.getHour() < index && p.getDay() * hours + p.getHour() >= finalLastInd;
            }).collect(Collectors.toList()));

            child2.addAll(curr2.stream().filter(p->{
                return p.getDay() * hours + p.getHour() < index && p.getDay() * hours + p.getHour() >= finalLastInd;
            }).collect(Collectors.toList()));

            List<Gen> tmp = curr1;
            curr1 = curr2;
            curr2 = tmp;
            lastInd = index;
        }
        int finalLastInd1 = lastInd;
        child1.addAll(curr1.stream().filter(p ->
                (p.getDay() * hours + p.getHour() >= finalLastInd1))
                .collect(Collectors.toList()));
        child2.addAll(curr2.stream().filter(p ->
                (p.getDay() * hours + p.getHour() >= finalLastInd1))
                .collect(Collectors.toList()));

        /* clone */
        List<Gen> finalChild1 = new ArrayList<>();
        for(Gen gen : child1) {
            finalChild1.add(new Gen(gen.getDay(),gen.getHour(),gen.getClas(),gen.getTeacher(),gen.getSubject()));
        }

        List<Gen> finalChild2 = new ArrayList<>();
        for(Gen gen : child2) {
            finalChild2.add(new Gen(gen.getDay(),gen.getHour(),gen.getClas(),gen.getTeacher(),gen.getSubject()));
        }

        TimeTableSolution s1 = new TimeTableSolution(finalChild1);
        TimeTableSolution s2 = new TimeTableSolution(finalChild2);
        return new Pair<TimeTableSolution,TimeTableSolution>(s1,s2);
    }

    private SortedSet<Integer> getRandomCuttingPoints(){
        SortedSet<Integer> cuttingPointsSet = new TreeSet<>();
        int minRange = 1;//need to cut at least one 1 five
        int maxRange = days * hours - 1;//need to cut at least 1 five

        while(cuttingPointsSet.size() < cuttingPoints){
            cuttingPointsSet.add(ThreadLocalRandom.current().nextInt(minRange, maxRange));
        }
        return cuttingPointsSet;
    }
    public static List<FieldInfo> getStaticFields(){
        List<FieldInfo> fields = new ArrayList<>();
        fields.add(new FieldInfo("Cutting Points","1",Integer.TYPE));
        return fields;
    }
}
