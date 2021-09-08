package TimeTableModel.Exceptions;

public class ExceptionInvalidSubjectForClass extends RuntimeException{
    int clasId;
    int subjectId;


    public int getClasId() {
        return clasId;
    }

    public int getSubjectId() {
        return subjectId;
    }
    public ExceptionInvalidSubjectForClass(int clasId, int subjectId) {
        this.clasId = clasId;
        this.subjectId = subjectId;
    }
}
