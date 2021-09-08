package TimeTableModel.EvolutionImpl.EvolutionModelImpl.Rules;

import EvolutionModel.Components.Rule;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Singularity implements Rule<TimeTableSolution> {

    private String id;
    private RuleType type;

    public Singularity(String id, RuleType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public String GetRuleId() {
        return this.id;
    }

    @Override
    public RuleType GetRuleType() {
        return this.type;
    }

    private class classStruct {
        public boolean validClass = true;
        public Set<Pair<Integer, Integer>> allHours = new HashSet<>();
    }

    @Override
    public Double Run(TimeTableSolution solution) {
        Set<Clas> classesSet = solution.getGens().stream().map(Gen::getClas).collect(Collectors.toSet());
        int numOfClasses = classesSet.size();
        int totalCorrect = solution.getGens().size();

        /* create classes map */
        Map<Clas, classStruct> classes = new HashMap<>();
        for(Clas clas : classesSet) {
            classStruct curClasStruct = new classStruct();
            classes.put(clas, curClasStruct);
        }

        double count = 0;

        /* iterate all gens and search for invalid classes */
        for(Gen gen : solution.getGens()) {
            Clas curClas = gen.getClas();
            Pair<Integer, Integer> curHour = new Pair<>(gen.getDay(), gen.getHour());

            if(classes.get(curClas).allHours.contains(curHour)) {
                classes.get(curClas).validClass = false;
                totalCorrect--;
                count++;
            }
            else {
                classes.get(curClas).allHours.add(curHour);
            }
        }
        //System.out.println(count);
        double score = (double)totalCorrect / solution.getGens().size() * 100;
        //System.out.println(score);
//        double score = 100 - count * 5;
//        if(score <= 0) {
//            score = 1;
//        }
        return score;
    }

    @Override
    public Map<String, String> GetConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("RuleType", type.name());
        return config;
    }
}
