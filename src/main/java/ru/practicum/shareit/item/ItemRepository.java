package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // постраничное получение вещей по id владельца вещи отсортированные по id вещи в порядке возрастания
    Page<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId, Pageable pageable);

    // получение списка вещей по id владельца вещи отсортированные по id вещи в порядке возрастания
    Collection<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    // постраничное получение вещей по содержащейся подстроке в названии или описании вещи.
    Page<Item> findByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String nameSearch, String descriptionSearch, Pageable pageable);

    // получение списка вещей по id запроса
    Collection<Item> findAllByRequestId(Long requestId);

}

