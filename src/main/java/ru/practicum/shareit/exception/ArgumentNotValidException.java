package ru.practicum.shareit.exception;

public class ArgumentNotValidException extends Exception {

    private final String messages;

    public ArgumentNotValidException(String messages) {
        super(messages);
        this.messages = messages;
    }

    @Override
    public String getMessage() {
        return messages;
    }
}