package TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.SelectionTypes;

import EvolutionModel.Components.Info.FieldInfo;
import EvolutionModel.Operations.Selection.ElitismMachine;
import EvolutionModel.Operations.Selection.Selection;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Truncation implements Selection<TimeTableSolution> {

    private int percent;
    private int elitismCount;

    public Truncation(int percent, int elitismCount){
        this.percent = percent;
        this.elitismCount = elitismCount;
    }

    @Override
    public String GetName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String GetConfig() {
        return String.format("TopPercent = %d , Elitism = %d", percent,elitismCount);
    }

    @Override
    public List<TimeTableSolution> Run(Map<TimeTableSolution, Double> solution2Fitness) {
        int dictSize = solution2Fitness.size();
        Set<Map.Entry<TimeTableSolution, Double>> entries = solution2Fitness.entrySet();
        List<TimeTableSolution> res = entries.stream().
                sorted((o1, o2) -> Double.compare(o2.getValue(),o1.getValue())).
                limit((int)(dictSize * (percent/ 100.0))).
                map(Map.Entry::getKey).
                collect(Collectors.toList());
        return res;
    }

    @Override
    public List<TimeTableSolution> GetElite(Map<TimeTableSolution, Double> solution2Fitness) {
        return ElitismMachine.GetElite(solution2Fitness, elitismCount);
    }

    @Override
    public List<FieldInfo> getFieldInfo() {
        List<FieldInfo> fields = new ArrayList<>();
        fields.add(new FieldInfo("Top Percent",Integer.toString(percent),Integer.TYPE));
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
            fields.add(new FieldInfo("Top Percent","1",Integer.TYPE));
        return fields;
    }
}