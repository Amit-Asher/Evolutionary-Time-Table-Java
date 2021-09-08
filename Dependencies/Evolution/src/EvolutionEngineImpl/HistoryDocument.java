package EvolutionEngineImpl;

import EvolutionModel.Components.Solution;

public class HistoryDocument implements Comparable<HistoryDocument>{
    private int generationDoc;
    private Solution solutionDoc;
    private double bestFitnessDoc;

    public HistoryDocument(int generationDoc, Solution solutionDoc, double bestFitnessDoc) {
        this.generationDoc = generationDoc;
        this.solutionDoc = solutionDoc;
        this.bestFitnessDoc = bestFitnessDoc;
    }


    public Solution getSolutionDoc() {
        return solutionDoc;
    }

    public double getBestFitnessDoc() {
        return bestFitnessDoc;
    }

    public int getGenerationDoc() {
        return generationDoc;
    }

    @Override
    public int compareTo(HistoryDocument o) {
        return this.generationDoc - o.generationDoc;
    }
}
