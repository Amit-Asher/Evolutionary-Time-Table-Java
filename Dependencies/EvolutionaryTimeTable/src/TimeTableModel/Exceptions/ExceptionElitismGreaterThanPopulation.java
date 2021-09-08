package TimeTableModel.Exceptions;

public class ExceptionElitismGreaterThanPopulation extends RuntimeException {

    private int elitismCount;
    private int populationSize;

    public ExceptionElitismGreaterThanPopulation(int elitismCount, int populationSize) {
        this.elitismCount = elitismCount;
        this.populationSize = populationSize;
    }

    public int getElitismCount() {
        return elitismCount;
    }

    public int getPopulationSize() {
        return populationSize;
    }
}
