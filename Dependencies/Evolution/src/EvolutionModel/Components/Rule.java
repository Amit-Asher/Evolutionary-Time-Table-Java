package EvolutionModel.Components;

import java.util.Map;

public interface Rule<T extends Solution> {

    public enum RuleType {
        Hard,
        Soft
    }

    public String GetRuleId();
    public RuleType GetRuleType();
    public Double Run(T solution);
    public Map<String, String> GetConfig();
}