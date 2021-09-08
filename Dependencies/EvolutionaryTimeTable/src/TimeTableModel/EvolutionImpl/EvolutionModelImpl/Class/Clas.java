package TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class;

import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject.Subject;

import java.util.Map;

public class Clas implements Comparable<Clas> {
    private int id;
    private String name;
    private Map<Subject, Integer> requirementsSubject2Hours;

    public Clas(int id, String name,Map<Subject,Integer> requirementsSubject2Hours) {
        this.id = id;
        this.name = name;
        this.requirementsSubject2Hours = requirementsSubject2Hours;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<Subject, Integer> getRequirementsSubject2Hours() {
        return requirementsSubject2Hours;
    }

    @Override
    public int compareTo(Clas o) {
        return id - o.id;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this){
            return true;
        }

        if (!(obj instanceof Clas)) {
            return false;
        }

        final Clas other = (Clas) obj;

        return this.id == other.id;
    }
}
