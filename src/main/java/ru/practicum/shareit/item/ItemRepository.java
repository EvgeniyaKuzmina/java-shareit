package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // поиск всех вещей по id владельца
    Collection<Item> findAllByOwnerId(Long ownerId);

    // поиск вещи по содержащейся подстроке в названии или описании вещи.
    Collection<Item> findByNameOrDescriptionContainingIgnoreCase(String nameSearch, String descriptionSearch);


}

