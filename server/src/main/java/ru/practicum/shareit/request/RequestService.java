package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestService {

    //Добавление нового запроса на вещи.
    ItemRequest createRequest(ItemRequestDto itemRequestDto, Long id);

    //Получение списка своих запросов вместе с данными об ответах на них.
    List<ItemRequest> getAllRequestsByUserId(Long id, Pageable pageable);

    //Получение списка запросов, созданных другими пользователями.
    List<ItemRequest> getAllRequestsCreatedAnotherUsers(Long id, Pageable pageable);

    // Получение данных об одном конкретном запросе вместе с данными об ответах на него в том же
    ItemRequest getRequestById(Long id, Long requesterId);

    void removeRequest(Long requestId);


}
