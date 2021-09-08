package TimeTableModel.Exceptions;

public class ExceptionInvalidSubjectForTeacher extends RuntimeException{
    int teacherId;
    int subjectId;

    public int getTeacherId() {
        return teacherId;
    }

    public int getSubjectId() {
        return subjectId;
    }


    public ExceptionInvalidSubjectForTeacher(int teacherId, int subjectId) {
        this.teacherId = teacherId;
        this.subjectId = subjectId;
    }
}
