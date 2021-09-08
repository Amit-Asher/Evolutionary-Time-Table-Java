package TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.MutationTypes;

import EvolutionModel.Components.Info.FieldInfo;
import EvolutionModel.Operations.Mutation.Mutation;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject.Subject;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher.Teacher;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableProblem;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;
import TimeTableModel.Exceptions.ExceptionNotInRange;
import TimeTableModel.Exceptions.ExceptionWrongType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FlippingMutation implements Mutation<TimeTableSolution> {

    private double probability;
    private int maxTuple;
    private TimeTableProblem.eGenComponent componentToChange;
    private Random rnd = new Random();
    private TimeTableProblem proRef;

    public FlippingMutation(double probability, int maxTuple, TimeTableProblem.eGenComponent comp, TimeTableProblem probRef) {
        this.probability = probability;
        this.maxTuple = maxTuple;
        this.componentToChange = comp;
        this.proRef = probRef;
    }

    @Override
    public String GetName() {
        return "Flipping Mutation";
    }

    @Override
    public double GetProb() {
        return probability;
    }

    @Override
    public String GetConfig() {
        return String.format("MaxTupples = %d, Component = %s",maxTuple,componentToChange);
    }

    @Override
    public void Run(TimeTableSolution solutionToMutate) {

        if(rnd.nextDouble() <= probability){
            List<Gen> genList = solutionToMutate.getGens();
            int numOfGensToChange = rnd.nextInt(maxTuple + 1);
            for(int i =0; i < numOfGensToChange; i++){
                Gen genToChange = genList.get(rnd.nextInt(genList.size()));
                switch (componentToChange) {
                    case C:
                        List<Clas> clasList = proRef.getClasses();
                        Clas tmpclas = clasList.get(rnd.nextInt(clasList.size()));
                        genToChange.setClas(tmpclas);
                        break;
                    case D:
                        genToChange.setDay(rnd.nextInt(proRef.getDays()));
                        break;
                    case H:
                        genToChange.setHour(rnd.nextInt(proRef.getHours()));
                        break;
                    case S:
                        Map<Integer, Subject> subjectList = proRef.getSubjects();
                        int rand = rnd.nextInt(subjectList.size()) + 1;
                        Subject tmpsub = subjectList.get(rand);
                        genToChange.setSubject(tmpsub);
                        break;
                    case T:
                        List<Teacher> teacherList = proRef.getTeachers();
                        Teacher tmpTeach = teacherList.get(rnd.nextInt(teacherList.size()));
                        genToChange.setTeacher(tmpTeach);
                        break;
                }
            }
        }
    }

    @Override
    public List<FieldInfo> getFieldInfo() {
        List<FieldInfo> fields = new ArrayList<>();
        fields.add(new FieldInfo("Probability",Double.toString(probability),Double.TYPE));
        fields.add(new FieldInfo("Max Tupples",Integer.toString(maxTuple),Integer.TYPE));
        fields.add(new FieldInfo("Component",componentToChange.name(),componentToChange.getClass()));
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
            Integer.parseInt(fieldsToUpdate.get("Max Tupples"));
        }
        catch (NumberFormatException e){
            throw new ExceptionWrongType("Max Tupples should be Integer number");
        }
        if(Double.parseDouble(fieldsToUpdate.get("Probability")) > 1 || Double.parseDouble(fieldsToUpdate.get("Probability")) < 0){
            throw  new ExceptionNotInRange("Probability should be between 0-1");
        }

        if(fieldsToUpdate.containsKey("Probability")){
            probability = Double.parseDouble(fieldsToUpdate.get("Probability"));
        }
        if(fieldsToUpdate.containsKey("Max Tupples")){
            maxTuple = Integer.parseInt(fieldsToUpdate.get("Max Tupples"));
        }
        if(fieldsToUpdate.containsKey("Component")){
            componentToChange = TimeTableProblem.eGenComponent.valueOf(fieldsToUpdate.get("Component"));
        }
    }
}
