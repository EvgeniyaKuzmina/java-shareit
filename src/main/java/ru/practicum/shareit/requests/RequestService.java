package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;

public interface RequestService {
    /*
     * POST /requests — добавить новый запрос вещи. Основная часть запроса — текст запроса, где пользователь описывает,
     * какая именно вещь ему нужна.*/
    ItemRequest createRequest(ItemRequestDto requestDto, Long id) throws ObjectNotFountException;

    /*GET /requests — получить список своих запросов вместе с данными об ответах на них.
     * Для каждого запроса должны указываться описание, дата и время создания и список ответов в формате:
     * id вещи, название, id владельца.
     * Так в дальнейшем, используя указанные id вещей, можно будет получить подробную информацию о каждой вещи.
     *  Запросы должны возвращаться в отсортированном порядке от более новых к более старым.*/
    Page<ItemRequest> getAllRequestsByUserId(Long id, Pageable pageable) throws ObjectNotFountException;

    /*GET /requests/all?from={from}&size={size} — получить список запросов, созданных другими пользователями.
     * С помощью этого эндпоинта пользователи смогут просматривать существующие запросы, на которые они могли бы ответить.
     * Запросы сортируются по дате создания: от более новых к более старым. Результаты должны возвращаться постранично.
     * Для этого нужно передать два параметра: from — индекс первого элемента, начиная с 0, и size — количество элементов
     *  для отображения.*/
    Page<ItemRequest> getAllRequestsCreatedAnotherUsers(Long id, Pageable pageable) throws ObjectNotFountException;

    /*GET /requests/{requestId} — получить данные об одном конкретном запросе вместе с данными об ответах на него в том же
     *  формате, что и в эндпоинте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.*/
    ItemRequest getRequestById(Long id, Long requesterId) throws ObjectNotFountException;


}
