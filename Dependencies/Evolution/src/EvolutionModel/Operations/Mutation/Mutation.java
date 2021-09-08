package EvolutionModel.Operations.Mutation;

import EvolutionModel.Components.Info.FieldInfo;
import EvolutionModel.Components.Solution;

import java.util.List;
import java.util.Map;

public interface Mutation<T extends Solution> {
    public String GetName();
    public double GetProb();
    public String GetConfig();
    public void Run(T solutionToMutate);
    public List<FieldInfo> getFieldInfo();
    public void UpdateConfig(Map<String, String> fieldsToUpdate);
}