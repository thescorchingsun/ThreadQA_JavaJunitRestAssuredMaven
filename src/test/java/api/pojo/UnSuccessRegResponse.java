package api.pojo;

public class UnSuccessRegResponse {

    private String error;

    public UnSuccessRegResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
