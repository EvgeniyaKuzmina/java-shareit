package ru.practicum.shareit.exception;

public class ConflictException  extends Exception {

    private final String messages;

    public ConflictException(String messages) {
        super(messages);
        this.messages = messages;
    }

    @Override
    public String getMessage() {
        return messages;
    }
}
