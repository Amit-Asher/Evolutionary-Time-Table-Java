package TimeTableModel.EvolutionImpl.EvolutionModelImpl.Rules;

import EvolutionModel.Components.Rule;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject.Subject;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Satisfactory implements Rule<TimeTableSolution> {

    private String id;
    private RuleType type;

    public Satisfactory(String id, RuleType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public String GetRuleId() {
        return this.id;
    }

    @Override
    public RuleType GetRuleType() {
        return this.type;
    }

    @Override
    public Double Run(TimeTableSolution solution) {
        double totalSlnScore = 0;
        List<Gen> gens = solution.getGens();
        int totalHoursOfSolution = 0;
        List<Clas> clases = solution.getGens().stream().map(Gen::getClas).distinct().collect(Collectors.toList());

        /* DEBUG */
//        System.out.println();
//        for(Clas clas : clases){
//            for(Map.Entry<Subject,Integer> sub2hr : clas.getRequirementsSubject2Hours().entrySet()) {
//                long slnHrOfSubjectInClass = gens.stream().filter(p ->p.getClas() == clas && sub2hr.getKey() == p.getSubject()).count();
//                System.out.println(clas.getName() + ": " + sub2hr.getKey().getName() + "=" +  slnHrOfSubjectInClass + "/" + sub2hr.getValue());
//            }
//        }

        /* ------------------------ */

        for(Clas clas : clases){
            for(Map.Entry<Subject,Integer> sub2hr : clas.getRequirementsSubject2Hours().entrySet()){
                totalHoursOfSolution += sub2hr.getValue();
            }
        }

        for(Clas clas :clases ){
            for(Map.Entry<Subject,Integer> sub2hr :clas.getRequirementsSubject2Hours().entrySet()){
                long slnHrOfSubjectInClass = gens.stream().filter(p ->p.getClas() == clas && sub2hr.getKey() == p.getSubject()).count();
                int reqHrOfSubjInClass = sub2hr.getValue();
                //double relativeGradeOfSubjInClass = (double) slnHrOfSubjectInClass / reqHrOfSubjInClass * 100;
                double relativeGradeOfSubjInClass = (double) slnHrOfSubjectInClass / reqHrOfSubjInClass;
                relativeGradeOfSubjInClass *= 100;
                double finalGradeOfSubjInClass;
//                System.out.println(sub2hr.getKey().getName() + ": " + relativeGradeOfSubjInClass);
//                if(relativeGradeOfSubjInClass <= 50 || relativeGradeOfSubjInClass >= 150) {
//                    finalGradeOfSubjInClass = 0;
//                }
//                else if(relativeGradeOfSubjInClass > 50 && relativeGradeOfSubjInClass < 100) {
//                    finalGradeOfSubjInClass = relativeGradeOfSubjInClass;
//                }
//                else if(relativeGradeOfSubjInClass > 100 && relativeGradeOfSubjInClass < 150) {
//                    finalGradeOfSubjInClass = 100 - (relativeGradeOfSubjInClass - 100);
//                }
//                else {
//                    finalGradeOfSubjInClass = 100;
//                }
//                System.out.println(sub2hr.getKey().getName() + " (final): " + finalGradeOfSubjInClass);


                if(relativeGradeOfSubjInClass <= 100){
                    finalGradeOfSubjInClass = relativeGradeOfSubjInClass;
                }else if(relativeGradeOfSubjInClass < 200){
                    finalGradeOfSubjInClass = 100 - (relativeGradeOfSubjInClass - 100);
                }else{
                    finalGradeOfSubjInClass = 0;
                }

                double wightOfSubjInClass = (double)reqHrOfSubjInClass / totalHoursOfSolution;
                totalSlnScore += wightOfSubjInClass * finalGradeOfSubjInClass;

                double reqForSubj = sub2hr.getValue();
                if(slnHrOfSubjectInClass == 0) {
                    totalSlnScore -= 30;
                }
            }
        }
        //System.out.println(totalSlnScore);
        return totalSlnScore;
    }

    @Override
    public Map<String, String> GetConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("RuleType", type.name());
        return config;
    }
}
