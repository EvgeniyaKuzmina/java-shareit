package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;

/**
 * класс описывающий пользователей
 */

@Builder
@Data
public class User {

    private Long id;
    private String name;
    @Email
    private String email;
}
