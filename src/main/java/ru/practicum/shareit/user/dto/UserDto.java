package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;


/**
 * класс для работы с User возвращающий сущность пользователем
 */

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;
    private String name;
    @Email
    private String email;
}
