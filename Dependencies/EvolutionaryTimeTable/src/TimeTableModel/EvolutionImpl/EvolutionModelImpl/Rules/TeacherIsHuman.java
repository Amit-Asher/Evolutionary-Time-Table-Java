package TimeTableModel.EvolutionImpl.EvolutionModelImpl.Rules;

import EvolutionModel.Components.Rule;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher.Teacher;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TeacherIsHuman implements Rule<TimeTableSolution> {

    private String id;
    private RuleType type;

    public TeacherIsHuman(String id, RuleType type) {
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

    private class teacherStruct {
        public boolean validTeacher = true;
        public Set<Pair<Integer, Integer>> allClasses = new HashSet<>();
    }

    @Override
    public Double Run(TimeTableSolution solution) {
        Set<Teacher> teachersSet = solution.getGens().stream().map(Gen::getTeacher).collect(Collectors.toSet());
        int numOfTeachers = teachersSet.size();
        int totalCorrect = solution.getGens().size();

        /* create teachers map */
        Map<Teacher, teacherStruct> teachers = new HashMap<>();
        for(Teacher teacher : teachersSet) {
            teacherStruct curTeacherStruct = new teacherStruct();
            teachers.put(teacher, curTeacherStruct);
        }

        /* iterate all gens and search for invalid teachers */
        for(Gen gen : solution.getGens()) {
            Teacher curTeacher = gen.getTeacher();
            Pair<Integer, Integer> curClass = new Pair<>(gen.getDay(), gen.getHour());
            if(teachers.get(curTeacher).allClasses.contains(curClass)) {
                teachers.get(curTeacher).validTeacher = false;
                totalCorrect--;
            }
            else {
                teachers.get(curTeacher).allClasses.add(curClass);
            }
        }

        return (double)totalCorrect / solution.getGens().size() * 100;
    }

    @Override
    public Map<String, String> GetConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("RuleType", type.name());
        return config;
    }
}
