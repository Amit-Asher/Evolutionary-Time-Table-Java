package EvolutionModel.Components.Info;

public class FieldInfo {
    private String fieldName;
    private String fieldValue;
    private Class fieldClass;

    public FieldInfo(String fieldName, String fieldValue, Class fieldType) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.fieldClass = fieldType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public Class getFieldClass() {
        return fieldClass;
    }
}
