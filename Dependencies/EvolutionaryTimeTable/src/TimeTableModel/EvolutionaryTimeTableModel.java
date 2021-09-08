package TimeTableModel;

import EvolutionEngineImpl.EvolutionEngine;
import EvolutionEngineImpl.EvolutionaryAlgorithmTask;
import EvolutionEngineImpl.HistoryDocument;
import EvolutionModel.Components.Info.FieldInfo;
import EvolutionModel.Components.Rule;
import EvolutionModel.Operations.Crossover.Crossover;
import EvolutionModel.Operations.Mutation.Mutation;
import EvolutionModel.Operations.Selection.Selection;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Gen;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Rules.*;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject.Subject;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher.Teacher;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableProblem;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.TimeTableSolution;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.CrossoverTypes.AspectOriented;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.CrossoverTypes.DayTimeOriented;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.MutationTypes.FlippingMutation;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.MutationTypes.SizerMutation;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.SelectionTypes.RouletteWheel;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.SelectionTypes.Tournament;
import TimeTableModel.EvolutionImpl.EvolutionOperationsImpl.SelectionTypes.Truncation;
import TimeTableModel.Exceptions.*;
import TimeTableModel.jaxb.*;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

/*
 Logic API (Facade)

    Attributes
- EvolutionEngine
- TimeTableProblem

      Methods
+ LoadFile(fileName)

+ RunEvolution()
+ PauseEvolution()
+ ResumeEvolution()
+ StopEvolution()

+ UpdateMutation()
+ UpdateSelection()
+ UpdateElitism()
+ UpdateCrossover()

+ GetSubjects()
+ GetTeachers()
+ GetClasses()
+ GetRules()
+ GetDays()
+ GetHours()

+ GetPopulationSize()
+ GetSelectionTechnique()
+ GetSelectionOptions()
+ GetSelectionConfig()
+ GetCrossoverType()
+ GetCrossoverOptions()
+ GetCrossoverConfig()
+ GetCrossoverCuttingPoints()
+ GetMutationsDetails()
+ GetMutationFieldInfo(idx)

+ GetBestSolution()
+ GetBestSolutionRulesPerformances()
+ GetGenerationHistory()

*/

public class EvolutionaryTimeTableModel {
    private EvolutionEngine<TimeTableSolution> evolutionEngine;
    private TimeTableProblem ttp;
    private final static String JAXB_XML_GAME_PACKAGE_NAME = "TimeTableModel.jaxb";
    private Thread runningThread;
    private int elitismCount;

    public int getElitism(){
        return elitismCount;
    }

    private void convertFromJaxbToLocal(ETTDescriptor descriptor) {
        EvolutionEngine<TimeTableSolution> localEvolutionEngine = new EvolutionEngine<>();
        /* problem import*/
        TimeTableProblem localTtp = new TimeTableProblem();

        /* Crossover */
        Crossover crossover;
        ETTCrossover ettCrossover = descriptor.getETTEvolutionEngine().getETTCrossover();
        String crossoverName = ettCrossover.getName();
        String conf = ettCrossover.getConfiguration();
        switch(crossoverName) {
            case "DayTimeOriented":
                crossover = new DayTimeOriented(
                        ettCrossover.getCuttingPoints(),
                        descriptor.getETTTimeTable().getDays(),
                        descriptor.getETTTimeTable().getHours());

                localEvolutionEngine.setCrossover(crossover);
                break;
            case "AspectOriented":
                Map<String,String> config = splitToKeyValue(conf);
                crossover = new AspectOriented(ettCrossover.getCuttingPoints(),
                        descriptor.getETTTimeTable().getDays(),
                        descriptor.getETTTimeTable().getHours(),
                        localTtp,
                        AspectOriented.eAspectType.valueOf(config.get("Orientation")));
                localEvolutionEngine.setCrossover(crossover);
                break;
            default:
                break;
        }

        /* initialPopulation */
        localEvolutionEngine.setPopulationSize(descriptor.getETTEvolutionEngine().getETTInitialPopulation().getSize());


        /* Selection */
        Selection selection;
        ETTSelection ettSelection = descriptor.getETTEvolutionEngine().getETTSelection();
        String typeSelection = ettSelection.getType();
        try {
            elitismCount = ettSelection.getETTElitism();
        } catch(Exception e) {
            elitismCount = 0;
        }

        if(elitismCount > localEvolutionEngine.getPopulationSize()) {
            throw new ExceptionElitismGreaterThanPopulation(elitismCount, localEvolutionEngine.getPopulationSize());
        }

        switch(typeSelection) {
            case "Truncation":
                Map<String,String> config = splitToKeyValue(ettSelection.getConfiguration());
                selection = new Truncation(Integer.parseInt(config.get("TopPercent")), elitismCount);
                localEvolutionEngine.setSelection(selection);
                break;
            case "RouletteWheel":
                selection = new RouletteWheel(elitismCount);
                localEvolutionEngine.setSelection(selection);
                break;
            case "Tournament":
                config = splitToKeyValue(ettSelection.getConfiguration());
                selection = new Tournament(Double.parseDouble(config.get("pte")), elitismCount);
                localEvolutionEngine.setSelection(selection);
            default:
                break;
        }


        /* days and hours*/
        localTtp.setDays(descriptor.getETTTimeTable().getDays());
        localTtp.setHours(descriptor.getETTTimeTable().getHours());

        /* subjects*/

        Map<Integer, Subject> id2subject = new TreeMap<>();
        List<Integer> idList = new ArrayList<>();
        List<ETTSubject> ettSubjects = descriptor.getETTTimeTable().getETTSubjects().getETTSubject();
        for(ETTSubject ettSubject : ettSubjects){
            int id = ettSubject.getId();
            idList.add(id);
            String name = ettSubject.getName();
            Subject subject = new Subject(name,id);
            id2subject.put(id,subject);
        }
        if(!isFollowingSequentialRule(idList)){
            throw new ExceptionSubjectIdNotSequential();
        }
        localTtp.setSubjects(id2subject);
        idList.clear();

        /* teachers */
        List<Teacher> teachers = new ArrayList<>();
        List<ETTTeacher> ettTeachers = descriptor.getETTTimeTable().getETTTeachers().getETTTeacher();
        for (ETTTeacher ettTeacher : ettTeachers) {
            int id = ettTeacher.getId();
            idList.add(id);
            String name = ettTeacher.getETTName();
            List<Subject> subjects = new ArrayList<>();
            for(ETTTeaches sub : ettTeacher.getETTTeaching().getETTTeaches()){
                if(!localTtp.getSubjects().containsKey(sub.getSubjectId())){
                    throw new ExceptionInvalidSubjectForTeacher(id,sub.getSubjectId());
                }
                subjects.add(localTtp.getSubjects().get(sub.getSubjectId()));
            }
            Teacher teacher = new Teacher(id,name ,subjects);

            teachers.add(teacher);
        }
        if(!isFollowingSequentialRule(idList)){
            throw new ExceptionTeacherIdNotSequential();
        }
        localTtp.setTeachers(teachers);

        /* Rules and weight*/
        localTtp.setHardWeight(descriptor.getETTTimeTable().getETTRules().getHardRulesWeight() / 100.0);
        List<ETTRule> ettRules = descriptor.getETTTimeTable().getETTRules().getETTRule();
        List<Rule> rules = new ArrayList<>();
        for (ETTRule ettRule : ettRules) {
            Rule rule = null;
            String id = ettRule.getETTRuleId();
            String config = ettRule.getETTConfiguration();
            String type = ettRule.getType();
            switch (ettRule.getETTRuleId()) {
                case "Knowledgeable":
                    rule = new Knowledgeable(id,config, Rule.RuleType.valueOf(type));
                    break;
                case "Satisfactory":
                    rule = new Satisfactory(id, Rule.RuleType.valueOf(type));
                    break;
                case "Singularity":
                    rule = new Singularity(id, Rule.RuleType.valueOf(type));
                    break;
                case "TeacherIsHuman":
                    rule = new TeacherIsHuman(id, Rule.RuleType.valueOf(type));
                    break;
                case "DayOffTeacher":
                    rule = new DayOffTeacher(id, Rule.RuleType.valueOf(type), localTtp.getHours() , localTtp.getDays());
                    break;
                case "Sequentiality":
                    Map<String,String> sequentialityConfig = splitToKeyValue(config);
                    int TotalHours = Integer.parseInt(sequentialityConfig.get("TotalHours"));
                    rule = new Sequentiality(id, Rule.RuleType.valueOf(type), TotalHours, localTtp.getHours(), localTtp.getDays());
                    break;
                default:
                    break;
            }
            rules.add(rule);
        }
        String isDupeRule = hasDuplicateRules(rules);
        if(isDupeRule != null){
            throw new ExceptionDuplicateRule(isDupeRule);
        }
        localTtp.setRules(rules);

        /* classes */
        List<ETTClass> ettclasses = descriptor.getETTTimeTable().getETTClasses().getETTClass();
        List<Clas> clases = new ArrayList<>();
        idList.clear();
        for(ETTClass ettclass : ettclasses){

            String name = ettclass.getETTName();
            int id = ettclass.getId();
            idList.add(id);
            Map<Subject,Integer> reqSubj2Hour = new TreeMap<>(new Comparator<Subject>() {
                @Override
                public int compare(Subject s1, Subject s2) {
                    Integer i1 = s1.getId();
                    return  i1.compareTo(s2.getId());
                }
            });
            int totalReqHoursForClass = 0;
            for(ETTStudy study :ettclass.getETTRequirements().getETTStudy()){
                if(!localTtp.getSubjects().containsKey(study.getSubjectId())){
                    throw new ExceptionInvalidSubjectForClass(id,study.getSubjectId());
                }
                Subject sub = localTtp.getSubjects().get(study.getSubjectId());
                reqSubj2Hour.put(sub,study.getHours());
                totalReqHoursForClass += study.getHours();
            }
            if(totalReqHoursForClass > localTtp.getHours() * localTtp.getDays()){
                throw new ExceptionTooManyHoursForClass(id,localTtp.getDays() * localTtp.getDays(),totalReqHoursForClass);
            }
            Clas clas = new Clas(id,name,reqSubj2Hour);
            clases.add(clas);
        }
        if(!isFollowingSequentialRule(idList)){
            throw new ExceptionClassIdNotSequential();
        }
        localTtp.setClasses(clases);

        /* Mutations */
        List<Mutation<TimeTableSolution>> mutations = new ArrayList<>();
        List<ETTMutation> ettmutations = descriptor.getETTEvolutionEngine().getETTMutations().getETTMutation();
        for (ETTMutation ettMutation : ettmutations) {
            Mutation<TimeTableSolution> mutation = null;
            switch (ettMutation.getName()) {
                case "Flipping":
                    double prob = ettMutation.getProbability();
                    conf = ettMutation.getConfiguration();
                    Map<String,String> config = splitToKeyValue(conf);
                    mutation = new FlippingMutation(prob,Integer.parseInt(config.get("MaxTupples")),
                            TimeTableProblem.eGenComponent.valueOf(config.get("Component")),
                            localTtp);
                    break;
                case "Sizer":
                    prob = ettMutation.getProbability();
                    conf = ettMutation.getConfiguration();
                    config = splitToKeyValue(conf);
                    mutation = new SizerMutation(prob,Integer.parseInt(config.get("TotalTupples")),localTtp);
                default:
                    break;
            }

            mutations.add(mutation);
        }
        localEvolutionEngine.setMutations(mutations);

        localEvolutionEngine.setProblemRef(localTtp);
        if(evolutionEngine != null){
            killThread();
        }
        evolutionEngine = localEvolutionEngine;
        ttp = localTtp;
    }

    private String hasDuplicateRules(List<Rule> rules) {
        List<String> name = new ArrayList<>();
        for(Rule rule : rules){
            if(name.contains(rule.GetRuleId())){
                return rule.GetRuleId();
            }
            name.add(rule.GetRuleId());
        }
        return null;
    }

    public void LoadFile(String fileName) throws FileNotFoundException {
        if(!fileName.endsWith(".xml")){
            throw new ExceptionWrongFileType("the file you have entered is not xml type");
        }
        InputStream inputStream = new FileInputStream(new File(fileName));
        ETTDescriptor descriptor;
        try{
            descriptor = deserializeFrom(inputStream);
            convertFromJaxbToLocal(descriptor);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    private static ETTDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(JAXB_XML_GAME_PACKAGE_NAME);
        Unmarshaller u = jc.createUnmarshaller();
        return (ETTDescriptor) u.unmarshal(in);
    }

    public void RunEvolution(Consumer<Integer> curGenerationDelegate, Consumer<Double> curFitnessDelegate,
                             Consumer<Double> curTimeDelegate, Runnable onFinish, Integer numOfGenertaions,
                             Double fitnessLimit, Double timeLimitInSeconds, Integer updateFreq)
    {
        Task<Boolean> newTask = new EvolutionaryAlgorithmTask<TimeTableSolution>(
                curGenerationDelegate, curFitnessDelegate, curTimeDelegate,
                evolutionEngine, numOfGenertaions, fitnessLimit, timeLimitInSeconds, updateFreq);

        newTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            onFinish.run();
        });

        runningThread = new Thread(newTask);
        runningThread.setDaemon(true);
        runningThread.start();
    }

    public List<String> GetSelectionOptions(){
        List<String> res = new ArrayList<String>();
        res.add("Truncation");
        res.add("Roulette Wheel");
        res.add("Tournament");
        return res;
    }

    public List<String> GetCrossoverOptions(){
        List<String> res = new ArrayList<String>();
        res.add("AspectOriented");
        res.add("DayTimeOriented");
        return res;
    }

    public void StopEvolution(){
        if(evolutionEngine.isPause()){
            evolutionEngine.ResumeEvolution();
        }
        if(runningThread !=null) {
            runningThread.interrupt();
        }
    }

    private void killThread(){
        if(runningThread != null && runningThread.isAlive()){
            try {
                if(evolutionEngine.isPause()){
                    evolutionEngine.ResumeEvolution();
                }
                runningThread.interrupt();
                runningThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void PauseEvolution(){
        evolutionEngine.setPause(true);
    }

    public void ResumeEvolution(){
        evolutionEngine.ResumeEvolution();
    }

    /*returns the best solution those far if no solution exist yet return null*/
    public TimeTableSolution GetBestSolution() {
        /* Shallow Cloning */
        List<Gen> gens = new ArrayList<>();
        evolutionEngine.getBestSolution().getGens().forEach(gen -> {
            gens.add(new Gen(gen.getDay(), gen.getHour(), gen.getClas(), gen.getTeacher(), gen.getSubject()));
        });

        return new TimeTableSolution(gens);
    }


    // ************* Option2 Section ****************

    // System Settings

    public Map<Integer, Subject> GetSubjects() {
        return ttp.getSubjects();
    }

    public List<Teacher> GetTeachers() {
        return ttp.getTeachers();
    }

    public List<Clas> GetClasses() {
        return ttp.getClasses();
    }

    public List<Rule> GetRules() {
        return ttp.getRules();
    }

    public int GetDays() {
        return ttp.getDays();
    }

    public int GetHours() {
        return ttp.getHours();
    }


    public int GetPopulationSize() {
        return evolutionEngine.getPopulationSize();
    }

    public String GetSelectionTechnique() {
        return evolutionEngine.getSelection().GetName();
    }

    public String GetSelectionConfig() {
        return evolutionEngine.getSelection().GetConfig();
    }

    public String GetCrossoverType() {
        return evolutionEngine.getCrossover().GetName();
    }

    public int GetCrossoverCuttingPoints() {
        return evolutionEngine.getCrossover().GetNumberOfCuttingPoints();
    }

    public String GetCrossoverConfig() {
        return evolutionEngine.getCrossover().GetConfiguration();
    }

    public class MutationDetails {
        public String name;
        public double probability;
        public String config;

        public MutationDetails(String name, double probability, String config) {
            this.name = name;
            this.probability = probability;
            this.config = config;
        }
    }

    public List<MutationDetails> GetMutationsDetails() {
        List<MutationDetails> mutationDetailsToReturn = new ArrayList<>();
        List<Mutation<TimeTableSolution>> mutations = evolutionEngine.getMutations();
        mutations.forEach(mutation -> {
            mutationDetailsToReturn.add(new MutationDetails(mutation.GetName(),
                    mutation.GetProb(), mutation.GetConfig()));
        });

        return mutationDetailsToReturn;
    }

    public List<FieldInfo> GetMutationFieldInfo(int idx) {
        return evolutionEngine.getMutations().get(idx).getFieldInfo();
    }



    // Algorithm Properties

    private boolean isFollowingSequentialRule(List<Integer> idList){
        Collections.sort(idList);
        int expectedId = 1;
        for(Integer id : idList){
            if(expectedId != id)
                return false;
            expectedId++;
        }
        return true;
    }

    // ************* Option2 Section End **********

    private Map<String,String> splitToKeyValue(String toSplit){
        Map<String,String> res = new HashMap<>();
        for(String pair : toSplit.split(",")) {
            String[] p = pair.trim().split("=");
            res.put(p[0],p[1]);
        }
        return res;
    }

    public List<HistoryDocument> GetGenerationHistory() {

        // may be better with lock object
        while(runningThread.isAlive()) {}

        if (AlgorithmIsRunning()) {
            int endIdx = evolutionEngine.getGenerationHistory().size();
            int beginIdx = (endIdx > 10) ? endIdx - 10 : 0;
            return evolutionEngine.getGenerationHistory().subList(beginIdx, endIdx);
        }
        return evolutionEngine.getGenerationHistory();
    }

    public boolean AlgorithmIsRunning() {
        if(evolutionEngine != null) {
            return evolutionEngine.isPause();
        }
        return false;
    }

    public class RulePerformence {
        public Rule rule = null;
        public Double score = null;
    }

    public List<RulePerformence> GetBestSolutionRulesPerformances() {
        List<RulePerformence> rulesPerformanceOfBestSolution = new ArrayList<>();

        for(Rule rule : ttp.getRules()) {
            RulePerformence rulePerformence = new RulePerformence();
            rulePerformence.rule = rule;
            Double score = rule.Run(GetBestSolution());
            rulePerformence.score = score;
            rulesPerformanceOfBestSolution.add(rulePerformence);
        }

        return rulesPerformanceOfBestSolution;
    }

    public void UpdateMutation(int mutationIndex, Map<String,String> newValues){
        evolutionEngine.getMutations().get(mutationIndex).UpdateConfig(newValues);
    }

    public void UpdateSelection(String selectionName,Map<String,String> newValues){
        if(selectionName.equals("Truncation")){
            try{
                Integer.parseInt(newValues.get("Top Percent"));
            }catch (NumberFormatException e){
                throw new ExceptionWrongType("Top Percent should be integer number");
            }
            if(Integer.parseInt(newValues.get("Top Percent")) > 100 || Integer.parseInt(newValues.get("Top Percent")) < 1)
                throw  new ExceptionNotInRange("Top Percent should be between 1-100");
            evolutionEngine.setSelection(new Truncation(Integer.parseInt(newValues.get("Top Percent")),elitismCount));
        }else if(selectionName.equals("Roulette Wheel"))
        {
            evolutionEngine.setSelection(new RouletteWheel(elitismCount));
        }else if(selectionName.equals("Tournament")){
            try{
                Double.parseDouble(newValues.get("pte"));
            }catch (NumberFormatException e){
                throw new ExceptionWrongType("pte should be double number");
            }
            if(Double.parseDouble(newValues.get("pte")) > 1 || Double.parseDouble(newValues.get("pte")) < 0)
                throw  new ExceptionNotInRange("pte should be between 0-1");
            evolutionEngine.setSelection(new Tournament(Double.parseDouble(newValues.get("pte")),elitismCount));
        }
    }

    public void UpdateElitism(String elitism){
        try{
            Integer.parseInt(elitism);
        }catch (NumberFormatException e){
            throw new ExceptionWrongType("elitism should be integer number");
        }
        if(Integer.parseInt(elitism) > evolutionEngine.getPopulationSize() || Integer.parseInt(elitism) < 0){
            throw new ExceptionNotInRange("elitism should be between 0-" + evolutionEngine.getPopulationSize());
        }
        elitismCount = Integer.parseInt(elitism);
        Map<String,String> toUpdate = new HashMap<>();
        toUpdate.put("Elitism",Integer.toString(elitismCount));
        evolutionEngine.getSelection().UpdateConfig(toUpdate);
    }

    public void UpdateCrossover(String crossoverName,Map<String,String> newValues){
        try{
            Integer.parseInt(newValues.get("Cutting Points"));
        }catch (NumberFormatException e){
            throw new ExceptionWrongType("Cutting Points should be integer number");
        }
        if(Integer.parseInt(newValues.get("Cutting Points")) > evolutionEngine.getPopulationSize() || Integer.parseInt(newValues.get("Cutting Points")) < 1)
            throw  new ExceptionNotInRange("Top Percent should be between 1-" + evolutionEngine.getPopulationSize());
        int cuttingPoints = Integer.parseInt(newValues.get("Cutting Points"));
        if(crossoverName.equals("AspectOriented")){
            AspectOriented.eAspectType newOrientation = AspectOriented.eAspectType.valueOf(newValues.get("Orientation"));
            evolutionEngine.setCrossover(new AspectOriented(cuttingPoints, GetDays(), GetHours(),ttp,newOrientation));
        }else if(crossoverName.equals("DayTimeOriented"))
        {
            evolutionEngine.setCrossover(new DayTimeOriented(cuttingPoints, GetDays(), GetHours()));
        }
    }
}
