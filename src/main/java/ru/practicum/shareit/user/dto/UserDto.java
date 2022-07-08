package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;


/**
 * класс для работы с User возвращающий сущность пользователем
 */

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    @Email
    private String email;
}
