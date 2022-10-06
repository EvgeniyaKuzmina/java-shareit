package ru.practicum.shareit.exception;

public class ArgumentNotValidException extends RuntimeException {

    public ArgumentNotValidException(String messages) {
        super(messages);
    }

}
