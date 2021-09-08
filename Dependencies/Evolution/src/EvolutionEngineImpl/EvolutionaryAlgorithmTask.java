package EvolutionEngineImpl;

import EvolutionModel.Components.Solution;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class EvolutionaryAlgorithmTask<T extends Solution> extends Task<Boolean> {

    private Consumer<Integer> curGenerationDelegate;
    private Consumer<Double> curFitnessDelegate;
    private Consumer<Double> curTimeDelegate;

    private EvolutionEngine<T> evolutionEngine;

    private Integer numOfGenerations;
    private Integer updateEvery;
    private Double stopFitness;
    private Double stopTimeInSeconds;

    public EvolutionaryAlgorithmTask(Consumer<Integer> curGenerationDelegate,
                                     Consumer<Double> curFitnessDelegate,
                                     Consumer<Double> curTimeDelegate,
                                     EvolutionEngine<T> evolutionEngine,
                                     Integer numOfGenerations, Double stopFitness,
                                     Double timeLimitInSeconds, Integer updateEvery) {

        this.curGenerationDelegate = curGenerationDelegate;
        this.curFitnessDelegate = curFitnessDelegate;
        this.curTimeDelegate = curTimeDelegate;
        this.evolutionEngine = evolutionEngine;
        this.numOfGenerations = numOfGenerations;
        this.stopFitness = stopFitness;
        this.stopTimeInSeconds = timeLimitInSeconds;
        this.updateEvery = updateEvery;
        // TODO: Wait For next Season
    }


    @Override
    protected Boolean call() throws Exception {
        evolutionEngine.RunAlgorithm(curGenerationDelegate, curFitnessDelegate, curTimeDelegate,
                numOfGenerations, stopFitness, stopTimeInSeconds, updateEvery);
        return true;
    }
}
