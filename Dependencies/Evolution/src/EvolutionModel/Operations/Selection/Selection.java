package EvolutionModel.Operations.Selection;

import EvolutionModel.Components.Info.FieldInfo;
import EvolutionModel.Components.Solution;

import java.util.List;
import java.util.Map;

public interface Selection <T extends Solution> {
    public String GetName();
    public String GetConfig();
    public List<T> Run(Map<T, Double> solution2Fitness);
    public List<T> GetElite(Map<T, Double> solution2Fitness);
    public List<FieldInfo> getFieldInfo();
    public void UpdateConfig(Map<String, String> fieldsToUpdate);
}