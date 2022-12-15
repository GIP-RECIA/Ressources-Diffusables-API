package fr.recia.ressourcesdiffusablesapi.model.apiresponse;

public class ApiResponse {

    private final long timestamp;
    private String message;
    private Object payload;

    private ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(String message, Object payload) {
        this();
        this.message = message;
        this.payload = payload;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

    public String getPayloadClass() {
        return this.payload.getClass().getSimpleName();
    }

    public Object getPayload() {
        return this.payload;
    }
}
