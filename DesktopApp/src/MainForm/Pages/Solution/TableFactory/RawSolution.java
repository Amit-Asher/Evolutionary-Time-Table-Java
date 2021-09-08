package MainForm.Pages.Solution.TableFactory;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class RawSolution {
    private final SimpleIntegerProperty day;
    private final SimpleIntegerProperty hour;
    private final SimpleStringProperty clas;
    private final SimpleStringProperty teacher;
    private final SimpleStringProperty subject;

    public RawSolution(Integer day, Integer hour,
                       String clas, String teacher,
                       String subject) {
        this.day = new SimpleIntegerProperty(day);
        this.hour = new SimpleIntegerProperty(hour);
        this.clas = new SimpleStringProperty(clas);
        this.teacher = new SimpleStringProperty(teacher);
        this.subject = new SimpleStringProperty(subject);
    }

    public Integer getDay() {
        return day.get();
    }

    public void setDay(Integer day) {
        this.day.set(day);
    }

    public Integer getHour() {
        return hour.get();
    }

    public void setHour(Integer hour) {
        this.hour.set(hour);
    }

    public String getClas() {
        return clas.get();
    }

    public void setClas(String clas) {
        this.clas.set(clas);
    }

    public String getTeacher() {
        return teacher.get();
    }

    public void setTeacher(String teacher) {
        this.teacher.set(teacher);
    }

    public String getSubject() {
        return subject.get();
    }

    public void setSubject(String subject) {
        this.subject.set(subject);
    }

}
