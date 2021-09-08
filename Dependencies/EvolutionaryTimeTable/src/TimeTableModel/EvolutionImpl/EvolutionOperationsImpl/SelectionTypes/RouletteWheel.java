package TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.SelectionTypes;

import EvolutionModel.Components.Info.FieldInfo;
import EvolutionModel.Operations.Selection.ElitismMachine;
import EvolutionModel.Operations.Selection.Selection;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;

import java.util.*;

public class RouletteWheel implements Selection<TimeTableSolution> {
    private int elitismCount;
    private Random random = new Random();

    public RouletteWheel(int elitismCount) {
        this.elitismCount = elitismCount;
    }

    @Override
    public String GetName() {
        return "Roulette Wheel";
    }

    @Override
    public String GetConfig() {
        return "Elitism = " + elitismCount;
    }

    private class SolutionWrapper {
        public double end;
        public TimeTableSolution solution;
    }

    private Comparator compareSolutionWrappers = new Comparator<SolutionWrapper>() {
        public int compare(SolutionWrapper u1, SolutionWrapper u2)
        {
            return Double.compare(u1.end, u2.end);
        }
    };

    @Override
    public List<TimeTableSolution> Run(Map<TimeTableSolution, Double> solution2Fitness) {
        List<TimeTableSolution> survivors = new ArrayList<>();
        List<SolutionWrapper> rouletteList = new ArrayList<>();
        double rouletteRange = 0;

        for(Map.Entry<TimeTableSolution, Double> entry : solution2Fitness.entrySet()) {
            SolutionWrapper sw = new SolutionWrapper();
            rouletteRange += entry.getValue();
            sw.end = rouletteRange;
            sw.solution = entry.getKey();
            rouletteList.add(sw);
        }

        for(int i=0; i < solution2Fitness.size(); i++) {
            double locOnRoulette = random.nextDouble() * rouletteRange;
            SolutionWrapper sw = new SolutionWrapper();
            sw.solution = null;
            sw.end = locOnRoulette;
            int bsResult = Collections.binarySearch(rouletteList, sw, compareSolutionWrappers);
            int idx = -1 * bsResult - 1;
            survivors.add(rouletteList.get(idx).solution);
        }

        return survivors;
    }

    @Override
    public List<TimeTableSolution> GetElite(Map<TimeTableSolution, Double> solution2Fitness) {
        return ElitismMachine.GetElite(solution2Fitness, elitismCount);
    }

    @Override
    public List<FieldInfo> getFieldInfo() {
        return new ArrayList<>();
    }

    @Override
    public void UpdateConfig(Map<String, String> fieldsToUpdate) {
        if(fieldsToUpdate.containsKey("Elitism")){
            elitismCount = Integer.parseInt(fieldsToUpdate.get("Elitism"));
        }
    }

    public static List<FieldInfo> getStaticFields() {
        return new ArrayList<>();
    }
}
