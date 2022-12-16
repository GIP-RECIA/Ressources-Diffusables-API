package fr.recia.ressourcesdiffusablesapi.model.apiresponse;

public class ApiError {

    private final Throwable exception;

    public ApiError(Throwable exception) {
        this.exception = exception;
    }

    public String getExceptionName() {
        return this.exception.getClass().getSimpleName();
    }

    public String getExceptionMessage() {
        return this.exception.getMessage();
    }

    public String getExceptionLocalizedMessage() {
        return this.exception.getLocalizedMessage();
    }
}
