package EvolutionModel.Components;

public interface Problem <T extends Solution> {
    public T GenerateRandomSolution();
    public Double CalculateFitness(T solution);
}