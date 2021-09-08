package TimeTableModel.Exceptions;

public class ExceptionDuplicateRule extends RuntimeException{
    String ruleName;

    public String getRuleName() {
        return ruleName;
    }

    public ExceptionDuplicateRule(String ruleName) {
        this.ruleName = ruleName;
    }
}
