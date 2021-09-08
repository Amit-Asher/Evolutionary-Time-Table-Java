package EvolutionEngineImpl;

import EvolutionModel.Components.Problem;
import EvolutionModel.Components.Rule;
import EvolutionModel.Components.Solution;
import EvolutionModel.Operations.Crossover.Crossover;
import EvolutionModel.Operations.Mutation.Mutation;
import EvolutionModel.Operations.Selection.Selection;
import javafx.application.Platform;
import javafx.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

public class EvolutionEngine<T extends Solution> {

    private List<Rule> rules = new ArrayList<>();
    private List<Mutation<T>> mutations = new ArrayList<>();
    private Selection<T> selection;
    private Crossover<T> crossover;
    private int populationSize;
    private Random random = new Random();
    private Problem<T> problemRef;
    private boolean isPause = false;
    private T bestSolution = null;
    private int currentGenration = 0;
    private final Object ReadWriteToHistorySync = new Object();

    private final Object pausingLock = new Object();
    private double bestFitness = 0;

    private Instant timeStart;
    private Instant startTimeInPause;
    private long sumTimeInPause;

    private List<HistoryDocument> historyDocuments = new ArrayList<>();
    private List<HistoryDocument> historyDocumentsToReturn = null;

    public Object getPausingLock() {
        return pausingLock;
    }

    public int getCurrentGenration() {
        return currentGenration;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public List<HistoryDocument> getGenerationHistory() {
        synchronized (ReadWriteToHistorySync) {
            return historyDocumentsToReturn;
        }
    }

    private void addToFitnessHistory(HistoryDocument historyDocument) {
        synchronized (ReadWriteToHistorySync) {
            historyDocuments.add(historyDocument);
        }
    }

    private boolean isEven(int num) {
        return num % 2 == 0;
    }


    public void setProblemRef(Problem<T> problemRef) {
        this.problemRef = problemRef;
    }


    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public Crossover<T> getCrossover() {
        return crossover;
    }

    public void setCrossover(Crossover<T> crossover) {
        this.crossover = crossover;
    }

    public List<Mutation<T>> getMutations() {
        return mutations;
    }

    public void setMutations(List<Mutation<T>> mutations) {
        this.mutations = mutations;
    }

    public Selection<T> getSelection() {
        return selection;
    }

    public void setSelection(Selection<T> selection) {
        this.selection = selection;
    }

    /* ---------------- Evolution Algorithm ----------------- */

    public T getBestSolution() {
        return bestSolution;
    }

    private List<T> createInitialPopulation() {
        List<T> curPopulation = new ArrayList<>();
        for(int i=0; i < populationSize; i++) {
            curPopulation.add(problemRef.GenerateRandomSolution());
        }
        return curPopulation;
    }

    private Map<T, Double> createSolution2FitnessDictionary(List<T> solutions) {
        Map<T, Double> solution2Fitness = new HashMap<>();
        double maxfitness = 0;

        for(T solution : solutions){
            double currFitness = problemRef.CalculateFitness(solution);
            if(maxfitness < currFitness) {
                bestSolution = solution;
                maxfitness = currFitness;
            }
            solution2Fitness.put(solution,currFitness);
        }
        return solution2Fitness;
    }

    private Pair<Integer, Integer> generateIdxFromRange(int range) {

        int parent1Idx = random.nextInt(range);
        int parent2Idx;

        do {
            parent2Idx = random.nextInt(range);
        }
        while(parent1Idx == parent2Idx);

        return new Pair<>(parent1Idx, parent2Idx);
    }

    private List<T> commitCrossovers(List<T> survivors, int numOfCrossoversToCommit) {
        List<T> newGeneration = new ArrayList<>();
        T parent1 = null;
        T parent2 = null;

        for(int i= 0; i < numOfCrossoversToCommit; i++) {
            Pair<Integer, Integer> parentsIdx = generateIdxFromRange(survivors.size());
            parent1 = survivors.get(parentsIdx.getKey());
            parent2 = survivors.get(parentsIdx.getValue());
            Pair<T, T> newChilds = crossover.Run(parent1, parent2);
            newGeneration.add(newChilds.getKey());
            newGeneration.add(newChilds.getValue());
        }

        return newGeneration;
    }

    private List<T> createNewGeneration(List<T> survivors, List<T> elite) {
        int numOfChildrenToCreate =  populationSize - elite.size();
        int numOfCrossoversToCommit = getNumberOfCroosoverToCommit(numOfChildrenToCreate);
        List<T> newGeneration = commitCrossovers(survivors, numOfCrossoversToCommit);
        HandleOddNumberOfChildren(newGeneration, numOfChildrenToCreate);
        mutateNewGeneration(newGeneration); // bug
        newGeneration.addAll(elite);
        return newGeneration;
    }

    private int getNumberOfCroosoverToCommit(int numOfChildrenToCreate) {
        int numOfCrossoversToCommit = numOfChildrenToCreate / 2;
        if(!isEven(numOfChildrenToCreate)) numOfCrossoversToCommit++;
        return numOfCrossoversToCommit;
    }

    private void HandleOddNumberOfChildren(List<T> newGeneration, int numOfChildrenToCreate) {
        if(!isEven(numOfChildrenToCreate)) newGeneration.remove(newGeneration.size() - 1);
    }

    private void mutateNewGeneration(List<T> curPopulation) {
        for(int i=0; i < curPopulation.size(); i++) {
            for (Mutation<T> mutation : mutations) {
                mutation.Run(curPopulation.get(i));
            }
        }
    }

    private void sortPopulationByFitness(List<T> curPopulation,
                                         Map<T, Double> solution2Fitness) {
        curPopulation.sort((s1, s2) -> {
            Double f2 = solution2Fitness.get(s2);
            return f2.compareTo(solution2Fitness.get(s1));
        });
    }

    private void addFinalGererationToFitnessHistory(boolean isAdded) {
        if(!isAdded){
            addToFitnessHistory(new HistoryDocument(currentGenration, bestSolution, bestFitness));
        }
    }

    private void setInitialSettings() {
        setPause(false);
        currentGenration = 0;
        //generationFitnessHistory = new ArrayList<>();
        timeStart = Instant.now();
        sumTimeInPause = 0;
    }

    private boolean updateFitnessHistory(int updateEvery) {
        if(currentGenration % updateEvery == 0) {
            addToFitnessHistory(new HistoryDocument(currentGenration, bestSolution, bestFitness));
            return true;
        }
        return false;
    }

    /* -------------- Evolution Algorithm Thread ------------------ */

    private boolean threadWasInterrupted() {
        if (Thread.currentThread().isInterrupted()) {
            setPause(false);
            return true;
        }
        return false;
    }

    private boolean generationCond(Integer numOfGenerations) {
        return numOfGenerations != null && currentGenration >= numOfGenerations;
    }

    private boolean fitnessCond(Double stopFitness) {
        return stopFitness != null && bestFitness >= stopFitness;
    }

    private boolean TimeCond(Double stopTimeInSeconds) {
        double timePassedInMillis = (double) Duration.between(timeStart, Instant.now()).toMillis() - sumTimeInPause;
        double timePassedInSeconds = timePassedInMillis / 1000;
        return stopTimeInSeconds != null && timePassedInSeconds >= stopTimeInSeconds;
    }

    private boolean stopConditionOccurred(Integer numOfGenerations, Double stopFitness, Double stopTimeInSeconds) {
        return generationCond(numOfGenerations) || fitnessCond(stopFitness) || TimeCond(stopTimeInSeconds);
    }

    public void RunAlgorithm(Consumer<Integer> generationChange, Consumer<Double> fitnessUpdate,
                             Consumer<Double> timeUpdate, Integer numOfGenerations, Double stopFitness,
                             Double stopTimeInSeconds, Integer updateEvery){
        setInitialSettings();
        List<T> curPopulation = createInitialPopulation();
        Map<T, Double> solution2Fitness = createSolution2FitnessDictionary(curPopulation);
        addToFitnessHistory(new HistoryDocument(0, bestSolution, solution2Fitness.get(bestSolution)));
        bestFitness = solution2Fitness.get(bestSolution);
        boolean isAdded = false;
        while(!stopConditionOccurred(numOfGenerations,stopFitness,stopTimeInSeconds)) {
            checkPause();
            if(threadWasInterrupted()) break;
            System.out.println(Thread.currentThread().getName());
            List<T> elite = selection.GetElite(solution2Fitness);
            List<T> survivors = selection.Run(solution2Fitness);
            curPopulation = createNewGeneration(survivors, elite);
            solution2Fitness = createSolution2FitnessDictionary(curPopulation);
            bestFitness = solution2Fitness.get(bestSolution);
            isAdded = updateFitnessHistory(updateEvery);
            currentGenration++;
            updateConsumers(generationChange,fitnessUpdate, timeUpdate);
        }
        problemRef.CalculateFitness(bestSolution);
        setFinalHistoryDocuments(isAdded);
    }

    private void setFinalHistoryDocuments(boolean isAdded) {
        addFinalGererationToFitnessHistory(isAdded);
        Collections.sort(historyDocuments);
        historyDocumentsToReturn = historyDocuments;
        historyDocuments = new ArrayList<>();
    }

    private void updateConsumers(Consumer<Integer> generationChange, Consumer<Double> fitnessUpdate,
                                 Consumer<Double> timeUpdate){
        Platform.runLater(()->{
            generationChange.accept(currentGenration);
            fitnessUpdate.accept(bestFitness);
            double timePassedInMillis = (double)Duration.between(timeStart, Instant.now()).toMillis() - sumTimeInPause;
            double timePassedInSeconds = timePassedInMillis / 1000;
            timeUpdate.accept(timePassedInSeconds);
        });
    }

    private void checkPause() {
        synchronized (pausingLock) {
            if(isPause) {
                while (isPause) {
                    try {
                        startTimeInPause = Instant.now();
                        pausingLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sumTimeInPause += Duration.between(startTimeInPause, Instant.now()).toMillis();
            }
        }
    }

    public void ResumeEvolution() {
        synchronized (pausingLock) {
            isPause = false;
            pausingLock.notifyAll();
        }
    }
}