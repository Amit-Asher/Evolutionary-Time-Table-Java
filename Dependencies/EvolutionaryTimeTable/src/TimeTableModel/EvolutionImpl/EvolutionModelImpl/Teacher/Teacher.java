package TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher;

import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject.Subject;

import java.util.List;

public class Teacher implements Comparable<Teacher> {
    private int id;
    private String name;
    private List<Subject> subjectsCanTeach;

    public Teacher(int id, String name, List<Subject> subjectsCanTeach) {
        this.id = id;
        this.name = name;
        this.subjectsCanTeach = subjectsCanTeach;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Subject> getSubjectsCanTeach() {
        return subjectsCanTeach;
    }

    @Override
    public int compareTo(Teacher o) {
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

        if (!(obj instanceof Teacher)) {
            return false;
        }

        final Teacher other = (Teacher) obj;

        return this.id == other.id ;
    }
}
