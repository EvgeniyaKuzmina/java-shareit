package ru.practicum.shareit.booking;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

/**
 * класс описывающий бронирование вещи
 */


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings", schema = "public")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "start_date_time",  nullable = false)
    private LocalDate start;
    @Column(name = "end_date_time",  nullable = false)
    private LocalDate end;
    @ManyToOne
    @JoinColumn(name="item_id")
    private Item item;
    @ManyToOne
    @JoinColumn(name="booker_id")
    private User booker;
    @Column(name = "status",  nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;


}
