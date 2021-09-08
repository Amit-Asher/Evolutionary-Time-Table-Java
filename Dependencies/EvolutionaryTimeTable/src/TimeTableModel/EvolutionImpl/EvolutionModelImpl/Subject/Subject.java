package TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject;

public class Subject implements Comparable<Subject> {
    private String name;
    private int id;

    public Subject(String name,int id){
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public int compareTo(Subject o) {
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

        if (!(obj instanceof Subject)) {
            return false;
        }

        final Subject other = (Subject) obj;

        return this.id == other.id ;
    }
}
