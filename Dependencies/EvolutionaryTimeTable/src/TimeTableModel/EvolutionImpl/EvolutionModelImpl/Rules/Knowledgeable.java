package TimeTableModel.EvolutionImpl.EvolutionModelImpl.Rules;

import EvolutionModel.Components.Rule;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;

import java.util.HashMap;
import java.util.Map;

public class Knowledgeable implements Rule<TimeTableSolution> {

    private String id;
    private RuleType type;

    public Knowledgeable(String id, String config, RuleType type){
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

    @Override
    public Double Run(TimeTableSolution solution) {
        int wrongTeacher = 0;

        for( Gen gen : solution.getGens()){
            if(!gen.getTeacher().getSubjectsCanTeach().contains(gen.getSubject())){
                wrongTeacher++;
            }
        }
        return 100 - ((double)wrongTeacher / solution.getGens().size() * 100);
    }

    @Override
    public Map<String, String> GetConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("RuleType", type.name());
        return config;
    }
}
