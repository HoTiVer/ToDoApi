package org.example.todoapi.DTO;

import org.example.todoapi.entity.Note;

public class ApiResponse {
    private boolean success;
    private String message;
    private Note data;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(boolean success, String message, Note data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Note getData() { return data; }
}
