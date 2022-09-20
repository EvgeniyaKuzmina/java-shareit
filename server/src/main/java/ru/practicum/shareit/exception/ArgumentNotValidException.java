package ru.practicum.shareit.exception;

public class ArgumentNotValidException extends Exception {

    public ArgumentNotValidException(String messages) {
        super(messages);
    }

}