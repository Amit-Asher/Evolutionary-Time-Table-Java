package TimeTableModel.EvolutionImpl.EvolutionModelImpl.Rules;

import EvolutionModel.Components.Rule;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject.Subject;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;

import java.util.*;
import java.util.stream.Collectors;

public class Sequentiality implements Rule<TimeTableSolution> {

    private String id;
    private RuleType type;
    private int TotalHours;
    private int hours;
    private int days;

    public Sequentiality(String id, RuleType type, int totalHours, int hours, int days) {
        this.id = id;
        this.type = type;
        this.TotalHours = totalHours;
        this.hours = hours;
        this.days = days;
    }

    @Override
    public String GetRuleId() {
        return id;
    }

    @Override
    public RuleType GetRuleType() {
        return type;
    }

    private class ClasSubject {
        public Clas clas = null;
        public Subject subject = null;

        public ClasSubject(Clas clas, Subject subject) {
            this.clas = clas;
            this.subject = subject;
        }
    }

    private enum eState {
        SWITCH_CLASS,
        SWITCH_SUBJECT,
        SWITCH_DAY,
        SWITCH_HOUR,
        FOUND_MATCH
    }

    private eState getState(Gen prev, Gen cur) {
        eState stateToReturn = null;
        if      (!cur.getClas().equals(prev.getClas()))         stateToReturn = eState.SWITCH_CLASS;
        else if (!cur.getSubject().equals(prev.getSubject()))   stateToReturn = eState.SWITCH_SUBJECT;
        else if (cur.getDay() != prev.getDay())                 stateToReturn = eState.SWITCH_DAY;
        else if (cur.getHour() != prev.getHour() + 1)           stateToReturn = eState.SWITCH_HOUR;
        else                                                    stateToReturn = eState.FOUND_MATCH;
        return stateToReturn;
    }

    @Override
    public Double Run(TimeTableSolution solution) {

        List<Gen> gens = solution.getGens();
        List<Clas> classes = gens.stream().map(Gen::getClas).distinct().collect(Collectors.toList());
        List<Subject> subjects = gens.stream().map(Gen::getSubject).distinct().collect(Collectors.toList());

        double prefectScore = classes.size() * subjects.size();
        List<Gen> sortedGens = solution.getGens().stream().
                sorted(Comparator.comparingDouble(Gen::getHour)). // H
                sorted(Comparator.comparingDouble(Gen::getDay)). // D
                sorted(Comparator.comparingDouble(g -> g.getSubject().getId())). // S
                sorted(Comparator.comparingDouble(g -> g.getClas().getId())). // C
                collect(Collectors.toList());

        Clas curClass = null;
        Subject curSubject = null;
        int prevHour = Integer.MAX_VALUE;
        int prevDay = Integer.MAX_VALUE;
        int countSerie = 1;
        Set<ClasSubject> subjectsFailed = new HashSet<>();

        for(int i=0; i < sortedGens.size(); i++) {
            Gen curGen = sortedGens.get(i);
            Gen prevGen = new Gen(prevDay, prevHour, curClass, null, curSubject);
            switch(getState(prevGen, curGen)) {
                case SWITCH_CLASS:
                    countSerie = 1;
                    prevHour = curGen.getHour();
                    curClass = curGen.getClas();
                    curSubject = curGen.getSubject();
                    prevDay = curGen.getDay();
                    break;
                case SWITCH_SUBJECT:
                    countSerie = 1;
                    prevHour = curGen.getHour();
                    curSubject = curGen.getSubject();
                    prevDay = curGen.getDay();
                    break;
                case SWITCH_DAY:
                    countSerie = 1;
                    prevHour = curGen.getHour();
                    prevDay = curGen.getDay();
                    break;
                case SWITCH_HOUR:
                    countSerie = 1;
                    prevHour = curGen.getHour();
                    break;
                case FOUND_MATCH:
                    countSerie++;
                    prevHour = curGen.getHour();
                    if(countSerie > TotalHours) {
                        subjectsFailed.add(new ClasSubject(curGen.getClas(), curGen.getSubject()));
                    }
                    break;
                default:
                    break;
            }
        }

        double score = prefectScore - subjectsFailed.size();
        return (score / prefectScore) * 100;
    }

    @Override
    public Map<String, String> GetConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("RuleType", type.name());
        config.put("Total Hours", Integer.toString(TotalHours));
        return config;
    }
}
