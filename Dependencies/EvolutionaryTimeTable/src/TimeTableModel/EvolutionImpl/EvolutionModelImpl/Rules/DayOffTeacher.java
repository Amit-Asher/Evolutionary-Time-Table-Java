package TimeTableModel.EvolutionImpl.EvolutionModelImpl.Rules;

import EvolutionModel.Components.Rule;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher.Teacher;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DayOffTeacher implements Rule<TimeTableSolution> {
    private String id;
    private RuleType type;
    private int hours;
    private int days;

    public DayOffTeacher(String id, RuleType type, int hours, int days) {
        this.id = id;
        this.type = type;
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

    private int getTeacherTotalHoursInShortestDay(TimeTableSolution solution, Teacher teacher) {
        int[] hoursInDays = new int[days];
        for(Gen gen : solution.getGens()) {
            if(gen.getTeacher().equals(teacher)) {
                hoursInDays[gen.getDay()]++;
            }
        }

        int hoursInShortestDay = hoursInDays[0];
        for(int day=1; day < days; day++) {
            hoursInShortestDay = Math.min(hoursInDays[day], hoursInShortestDay);
        }

        return hoursInShortestDay;
    }


    @Override
    public Double Run(TimeTableSolution solution) {
        Set<Teacher> teachersSet = solution.getGens().stream().map(Gen::getTeacher).collect(Collectors.toSet());

        double solutionFitness = 0;
        for(Teacher teacher : teachersSet) {
            int teacherTotalHoursInShortestDay = getTeacherTotalHoursInShortestDay(solution, teacher);
            double teacherGrade = 100 - ((double)teacherTotalHoursInShortestDay / hours) * 100;
            solutionFitness += teacherGrade;
        }

        return solutionFitness / teachersSet.size();
    }

    @Override
    public Map<String, String> GetConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("RuleType", type.name());
        return config;
    }
}
