package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * класс, отвечающий за запрос вещи
 */


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "item_requests", schema = "public")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne()
    @JoinColumn(name = "requester_id")
    private User requester;
    @NotNull
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @ElementCollection
    @CollectionTable(name = "items", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "id")
    private Set<Long> itemsId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ItemRequest that = (ItemRequest) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
