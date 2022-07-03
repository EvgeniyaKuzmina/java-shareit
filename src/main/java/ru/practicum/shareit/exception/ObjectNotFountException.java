package ru.practicum.shareit.exception;

public class ObjectNotFountException extends Exception {

    private final String messages;

    public ObjectNotFountException(String messages) {
        super(messages);
        this.messages = messages;
    }

    @Override
    public String getMessage() {
        return messages;
    }
}
