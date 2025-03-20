package org.example.todoapi.DTO;

public class LoginResponse {
    private Boolean success;
    private String token;

    public LoginResponse(Boolean success, String token) {
        this.success = success;
        this.token = token;
    }

    public Boolean getSuccess() {
        return success;
    }

    public String getToken() {
        return token;
    }
}
