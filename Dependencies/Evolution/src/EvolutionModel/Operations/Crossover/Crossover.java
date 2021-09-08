package EvolutionModel.Operations.Crossover;

import EvolutionModel.Components.Solution;
import javafx.util.Pair;

public interface Crossover<T extends Solution> {
    public int GetNumberOfCuttingPoints();
    public String GetName();
    public String GetConfiguration();
    public Pair<T, T> Run(T parent1, T parent2);
}
