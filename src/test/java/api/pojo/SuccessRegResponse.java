package api.pojo;

public class SuccessRegResponse {

    private Integer id;
    private String token;

    public SuccessRegResponse(Integer id, String token) {
        this.id = id;
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}
