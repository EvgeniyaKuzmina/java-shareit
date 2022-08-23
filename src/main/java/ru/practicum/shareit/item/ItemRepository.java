package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // постраничное получение вещей по id владельца вещи отсортированные по id вещи в порядке возрастания
    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId, Pageable pageable);

    // получение списка вещей по id владельца вещи отсортированные по id вещи в порядке возрастания
    Collection<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);

    // постраничное получение вещей по содержащейся подстроке в названии или описании вещи.
    List<Item> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableTrue(String nameSearch, String descriptionSearch, Pageable pageable);

    // получение списка вещей по id запроса
    Collection<Item> findAllByItemRequestId(Long requestId);

}

