package TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.SelectionTypes;

import EvolutionModel.Components.Info.FieldInfo;
import EvolutionModel.Operations.Selection.ElitismMachine;
import EvolutionModel.Operations.Selection.Selection;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Tournament implements Selection<TimeTableSolution> {
    private int elitismCount;
    private Random random = new Random();
    private double pte;

    public Tournament(double pte, int elitismCount) {
        this.elitismCount = elitismCount;
        this.pte = pte;
    }


    @Override
    public String GetName() {
        return "Tournament";
    }

    @Override
    public String GetConfig() {
        return String.format("pte = %.2f , Elitism = %d", pte,elitismCount);
    }

    @Override
    public List<TimeTableSolution> Run(Map<TimeTableSolution, Double> solution2Fitness) {
        List<TimeTableSolution> survivors = new ArrayList<>();
        ArrayList<TimeTableSolution> arrsolution = new ArrayList<>(solution2Fitness.keySet());
        for(int i=0;i<solution2Fitness.size();i++){
            Pair<Integer,Integer> paranteId = generateIdxFromRange(solution2Fitness.size());
            double fitnessParent1 = solution2Fitness.get(arrsolution.get(paranteId.getKey()));
            double fitnessParent2 = solution2Fitness.get(arrsolution.get(paranteId.getValue()));
            if(pte < random.nextDouble()){
                if(fitnessParent1 >fitnessParent2) {
                    survivors.add(arrsolution.get(paranteId.getKey()));
                }
                else{
                    survivors.add(arrsolution.get(paranteId.getValue()));
                }
            }else{
                if(fitnessParent1 >fitnessParent2) {
                    survivors.add(arrsolution.get(paranteId.getValue()));
                }
                else{
                    survivors.add(arrsolution.get(paranteId.getKey()));
                }
            }
        }
        return survivors;
    }

    private Pair<Integer, Integer> generateIdxFromRange(int range) {

        int parent1Idx = random.nextInt(range);
        int parent2Idx;

        do {
            parent2Idx = random.nextInt(range);
        }
        while(parent1Idx == parent2Idx);

        return new Pair<>(parent1Idx, parent2Idx);
    }

    @Override
    public List<TimeTableSolution> GetElite(Map<TimeTableSolution, Double> solution2Fitness) {
        return ElitismMachine.GetElite(solution2Fitness, elitismCount);
    }

    @Override
    public List<FieldInfo> getFieldInfo() {
        List<FieldInfo> fields = new ArrayList<>();
        fields.add(new FieldInfo("pte",Double.toString(pte),Double.TYPE));
        return fields;
    }

    @Override
    public void UpdateConfig(Map<String, String> fieldsToUpdate) {
        if(fieldsToUpdate.containsKey("Elitism")){
            elitismCount = Integer.parseInt(fieldsToUpdate.get("Elitism"));
        }
    }

    public static List<FieldInfo> getStaticFields(){
        List<FieldInfo> fields = new ArrayList<>();
        fields.add(new FieldInfo("pte","0.5",Double.TYPE));
        return fields;
    }
}
