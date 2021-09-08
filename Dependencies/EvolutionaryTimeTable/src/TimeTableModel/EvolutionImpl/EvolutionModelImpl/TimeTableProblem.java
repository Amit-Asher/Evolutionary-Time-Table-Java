package TimeTableModel.EvolutionImpl.EvolutionModelImpl;

import EvolutionModel.Components.Problem;
import EvolutionModel.Components.Rule;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject.Subject;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher.Teacher;

import java.util.*;

public class TimeTableProblem implements Problem<TimeTableSolution> {
    private int days;
    private int hours;
    private List<Teacher> teachers = new ArrayList<>();
    private Map<Integer, Subject> subjects = new TreeMap<>();
    private List<Clas> classes = new ArrayList<>();
    private List<Rule> rules = new ArrayList<>();
    private double hardWeight;
    private Random random = new Random();
    public enum eGenComponent{
        T,
        S,
        C,
        H,
        D
    }

    public Map<eGenComponent,Integer> getComponent2RangeMap(){
        Map<eGenComponent,Integer> res = new HashMap<>();
        res.put(eGenComponent.C,classes.size());
        res.put(eGenComponent.D,days);
        res.put(eGenComponent.H,hours);
        res.put(eGenComponent.S,subjects.size());
        res.put(eGenComponent.T,teachers.size());
        return res;
    }
    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public Map<Integer, Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Map<Integer, Subject> subjects) {
        this.subjects = subjects;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<Teacher> teachers) {
        this.teachers = teachers;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public void setHardWeight(double hardWeight) {
        this.hardWeight = hardWeight;
    }

    public List<Clas> getClasses() {
        return classes;
    }

    public void setClasses(List<Clas> classes) {
        this.classes = classes;
    }

    @Override
    public TimeTableSolution GenerateRandomSolution() {

        /* calculate number of gens */
        int numOfGens = 0;
        for(Clas clas : classes) {
            for(Integer value : clas.getRequirementsSubject2Hours().values()) {
                numOfGens += value;
            }
        }

        /* generate all gens */
        Set<Gen> genSet = new HashSet<>();
        Gen gen = null;
        while(genSet.size() != numOfGens) {
            genSet.add(GenerateRandomGen());
        }

        List<Gen> genList = new ArrayList<>(genSet);
        return new TimeTableSolution(genList);
    }

    public Gen GenerateRandomGen(){

        int day = random.nextInt(this.days);
        int hour = random.nextInt(this.hours);
        Teacher teacher = teachers.get(random.nextInt(teachers.size()));
        Clas clas = classes.get(random.nextInt(classes.size()));
        Subject subject = teacher.getSubjectsCanTeach().get(random.nextInt(teacher.getSubjectsCanTeach().size()));
        Gen gen = new Gen(day, hour, clas, teacher, subject);
        return gen;
    }

    @Override
    public Double CalculateFitness(TimeTableSolution solution) {

        double hardGrade = 0;
        double softGrade = 0;
        int hardCount = 0;
        int softCount = 0;
        double tmp = 0;

        for(Rule rule : rules) {
            switch(rule.GetRuleType()) {
                case Hard:
                    tmp = rule.Run(solution);
                    hardGrade += tmp;
                    hardCount++;
                    break;
                case Soft:
                    tmp = rule.Run(solution);
                    softGrade += tmp;
                    softCount++;
                    break;
                default:
                    break;
            }
        }

        return hardWeight * (hardGrade / hardCount) + (1 - hardWeight) * (softGrade / softCount);
    }
}
