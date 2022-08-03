package ru.practicum.shareit.requests;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * класс, отвечающий за запрос вещи
 */


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "item_requests", schema = "public")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "description",  nullable = false)
    private String description;
    @ManyToOne()
    @JoinColumn(name="requester_id")
    private User requester;
    @NotNull
    @Column(name = "created",  nullable = false)
    private LocalDateTime created;

}
