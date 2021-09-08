package TimeTableModel.Exceptions;

public class ExceptionTooManyHoursForClass extends RuntimeException{
    int clasId;
    int maxHours;
    int actualRequiredHours;

    public int getClasId() {
        return clasId;
    }

    public int getMaxHours() {
        return maxHours;
    }

    public int getActualRequiredHours() {
        return actualRequiredHours;
    }


    public ExceptionTooManyHoursForClass(int clasId, int maxHours, int actualRequired) {
        this.clasId = clasId;
        this.maxHours = maxHours;
        this.actualRequiredHours = actualRequired;
    }
}
