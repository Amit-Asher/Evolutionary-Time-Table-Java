package TimeTableModel.EvolutionImpl.EvolutionModelImpl;

import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Class.Clas;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Subject.Subject;
import TimeTableModel.EvolutionImpl.EvolutionModelImpl.Teacher.Teacher;

public class Gen {
    private int day;
    private int hour;
    private Clas clas;
    private Teacher teacher;
    private Subject subject;

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setClas(Clas clas) {
        this.clas = clas;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Gen(int day, int hour, Clas clas, Teacher teacher, Subject subject) {
        this.day = day;
        this.hour = hour;
        this.clas = clas;
        this.teacher = teacher;
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Clas getClas() {
        return clas;
    }

    public int getHour() {
        return hour;
    }

    public int getDay() {
        return day;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this){
            return true;
        }

        if (!(obj instanceof Gen)) {
            return false;
        }

        final Gen other = (Gen) obj;

        return this.day == other.day &&
                this.hour == other.hour &&
                this.teacher.equals(other.teacher) &&
                this.clas.equals(other.clas) &&
                this.subject.equals(other.subject);
    }

    public String toString(){
        return "day: " + day + " hour: " + hour +" class: "  + clas.getName() + " teach " + teacher.getName() + " subject "  + subject.getName();
    }
}
