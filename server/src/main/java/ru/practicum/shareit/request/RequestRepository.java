package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    // получение списка всех запросов вещей по id создателя запроса
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId, Pageable pageable);

    // получение списка всех запросов вещей сделанных другими пользователями, не включая запросы текущего пользователя
    List<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Long requesterId, Pageable pageable);
}
