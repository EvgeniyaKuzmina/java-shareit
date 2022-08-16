package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.requests.model.ItemRequest;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    // получение списка всех запросов вещей по id создателя запроса
    Page<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(Long requesterId, Pageable pageable);

    // получение списка всех запросов вещей сделанных другими пользователями, не включая запросы текущего пользователя
    Page<ItemRequest> findAllByRequesterIdNotOrderByCreatedDesc(Long requesterId, Pageable pageable);
}
