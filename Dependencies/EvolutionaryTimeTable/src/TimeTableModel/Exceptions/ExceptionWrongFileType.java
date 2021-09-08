package TimeTableModel.Exceptions;
public class ExceptionWrongFileType extends RuntimeException{
    public ExceptionWrongFileType(String msg){
        super(msg);
    }
}