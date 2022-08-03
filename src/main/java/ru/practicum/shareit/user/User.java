package ru.practicum.shareit.user;

import lombok.*;
import ru.practicum.shareit.booking.Booking;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * класс описывающий пользователей
 */

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "name",  nullable = false)
    private String name;
    @Email
    @NotNull
    @Column(name = "email",  nullable = false)
    private String email;


}
