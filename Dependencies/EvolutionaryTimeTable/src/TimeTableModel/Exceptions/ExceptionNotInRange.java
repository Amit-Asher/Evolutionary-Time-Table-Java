package TimeTableModel.Exceptions;

public class ExceptionNotInRange extends RuntimeException{
    public ExceptionNotInRange(String msg){
        super(msg);
    }
}
