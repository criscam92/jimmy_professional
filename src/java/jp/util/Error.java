package jp.util;

public class Error {

    private String classError = "";

    public Error() {
    }

    public Error(String classError) {
        this.classError = classError;
    }

    public String getClassError() {
        return classError;
    }

    public void setClassError(String classError) {
        this.classError = classError;
    }

    public void cleanError() {
        setClassError("");
    }

    public void addError() {
        setClassError("ui-state-error");
    }
}
