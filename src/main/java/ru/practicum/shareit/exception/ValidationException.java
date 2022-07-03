package ru.practicum.shareit.exception;

public class ValidationException extends Exception {

    private final String messages;

    public ValidationException(String messages) {
        super(messages);
        this.messages = messages;
    }

    @Override
    public String getMessage() {
        return messages;
    }
}
