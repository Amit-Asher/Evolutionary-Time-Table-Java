package TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.MutationTypes;

import EvolutionModel.Components.Info.FieldInfo;
import EvolutionModel.Operations.Mutation.Mutation;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableProblem;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;
import TimeTableModel.Exceptions.ExceptionNotInRange;
import TimeTableModel.Exceptions.ExceptionWrongType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SizerMutation implements Mutation<TimeTableSolution> {

    private double probability;
    private int totalTupples;
    private TimeTableProblem proRef;
    private Random rnd = new Random();

    public SizerMutation(double probability, int totalTupples,TimeTableProblem probRef) {
        this.probability = probability;
        this.totalTupples = totalTupples;
        this.proRef = probRef;
    }

    @Override
    public String GetName() {
        return "Sizer Mutation";
    }

    @Override
    public double GetProb() {
        return this.probability;
    }

    @Override
    public String GetConfig() {
        return "Total Tupples = " + this.totalTupples;
    }

    @Override
    public void Run(TimeTableSolution solutionToMutate) {
        if(rnd.nextDouble() < probability){
            List<Gen> gens = solutionToMutate.getGens();
            if(totalTupples > 0) {
                int maxToadd = totalTupples + gens.size() > proRef.getDays() * proRef.getHours() ?
                        proRef.getDays() * proRef.getHours() - gens.size() : totalTupples;

                if(maxToadd <= 0){
                    return;
                }
                maxToadd = rnd.nextInt(maxToadd); // make sure not insert 0
                for(int i =0; i < maxToadd; i++){
                    gens.add(proRef.GenerateRandomGen());
                }
            }
            else {
                int maxToRemove = totalTupples - gens.size() < proRef.getDays() ? gens.size() - proRef.getDays() : totalTupples;
                if(maxToRemove >= 0){
                    return;
                }
                maxToRemove = rnd.nextInt(maxToRemove * - 1);
                for(int i =0 ;i < maxToRemove; i++){
                    gens.remove(rnd.nextInt(gens.size()));
                }
            }
        }
    }

    @Override
    public List<FieldInfo> getFieldInfo() {
        List<FieldInfo> fields = new ArrayList<>();
        fields.add(new FieldInfo("Probability",Double.toString(probability),Double.TYPE));
        fields.add(new FieldInfo("Total Tupples",Integer.toString(totalTupples),Integer.TYPE));
        return fields;
    }

    @Override
    public void UpdateConfig(Map<String, String> fieldsToUpdate) {
        try{
            Double.parseDouble(fieldsToUpdate.get("Probability"));
        }
        catch (NumberFormatException e){
            throw new ExceptionWrongType("Probability should be double number");
        }
        try{
            Integer.parseInt(fieldsToUpdate.get("Total Tupples"));
        }
        catch (NumberFormatException e){
            throw new ExceptionWrongType("Total Tupples should be Integer number");
        }
        if(Double.parseDouble(fieldsToUpdate.get("Probability")) > 1 || Double.parseDouble(fieldsToUpdate.get("Probability")) < 0){
            throw  new ExceptionNotInRange("probabilty should be between 0-1");
        }
        if(Integer.parseInt(fieldsToUpdate.get("Total Tupples")) < 1)
            throw  new ExceptionNotInRange("total Tupples should be atleast 1");
        if(fieldsToUpdate.containsKey("Probability")){
            probability = Double.parseDouble(fieldsToUpdate.get("Probability"));
        }
        if(fieldsToUpdate.containsKey("Total Tupples")){
            totalTupples = Integer.parseInt(fieldsToUpdate.get("Total Tupples"));
        }
    }
}