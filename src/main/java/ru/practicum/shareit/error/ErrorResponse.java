package ru.practicum.shareit.error;

public class ErrorResponse {
    // название ошибки
    String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
