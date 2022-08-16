package ru.practicum.shareit.requests;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * контроллер для работы с запросами
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class RequestController {

    private static final Integer FROM = 0;
    private static final Integer SIZE = 10;
    private static final String HEADER_REQUEST = "X-Sharer-User-Id"; // заголовок запроса в котором передаётся id владельца вещи
    private final RequestService requestService;
    private final ItemService itemService;

    public RequestController(RequestService requestService, ItemService itemService) {
        this.requestService = requestService;
        this.itemService = itemService;
    }


    // создание нового запроса вещи.
    @PostMapping
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto requestDto,
                                        @RequestHeader(HEADER_REQUEST) Long requesterId) throws ObjectNotFountException, ArgumentNotValidException {
        if (Optional.ofNullable(requestDto.getDescription()).isEmpty() || requestDto.getDescription().isBlank()) {
            log.error("Пустое описание запроса");
            throw new ArgumentNotValidException("Описание запроса не может быть пустым");
        }
        requestDto.setCreated(LocalDateTime.now());
        ItemRequest request = requestService.createRequest(requestDto, requesterId);

        Collection<Item> items = itemService.findAllByRequestId(request.getId());
        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    //Получение списка всех своих запросов вместе с данными об ответах на них.
    @GetMapping
    public Collection<ItemRequestDto> getAllRequestsByUserId(@RequestHeader(HEADER_REQUEST) Long requesterId,
                                                             @RequestParam(required = false) Integer from,
                                                             @RequestParam(required = false) Integer size)
            throws ObjectNotFountException, ArgumentNotValidException {
        if (checkParameterForNull(from, size)) {
            from = FROM;
            size = SIZE;
        }
        if (checkParameterForMin(from, size)) {
            log.error("Указаны неверные параметры для отображения страницы");
            throw new ArgumentNotValidException("Указаны неверные параметры для отображения страницы");
        }
        Pageable pageable = PageRequest.of(from, size);
        Page<ItemRequest> requestPage = requestService.getAllRequestsByUserId(requesterId, pageable);

        return toListItemRequestDto(requestPage);
    }

    // Получение списка запросов, созданных другими пользователями. Эндпоинт GET /requests/all?from={from}&size={size}
    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequestsCreatedAnotherUsers(@RequestHeader(HEADER_REQUEST) Long requesterId,
                                                                        @RequestParam(required = false) Integer from,
                                                                        @RequestParam(required = false) Integer size)
            throws ObjectNotFountException, ArgumentNotValidException {
        if (checkParameterForNull(from, size)) {
            from = FROM;
            size = SIZE;
        }
        if (checkParameterForMin(from, size)) {
            log.error("Указаны неверные параметры для отображения страницы");
            throw new ArgumentNotValidException("Указаны неверные параметры для отображения страницы");
        }
        Pageable pageable = PageRequest.of(from, size);
        Page<ItemRequest> requestPage = requestService.getAllRequestsCreatedAnotherUsers(requesterId, pageable);

        return toListItemRequestDto(requestPage);

    }

    // Получение данных об одном конкретном запросе вместе с данными об ответах на него. Эндпоинт GET /requests/{requestId}
    @GetMapping("/{id}")
    public ItemRequestDto getRequestById(@RequestHeader(HEADER_REQUEST) Long requesterId, @PathVariable Long id)
            throws ObjectNotFountException {

        ItemRequest request = requestService.getRequestById(id, requesterId);
        Collection<Item> items = itemService.findAllByRequestId(request.getId());
        return ItemRequestMapper.toItemRequestDto(request, items);
    }

    // проверяет параметры from и size, что они введены и введены корректно
    private boolean checkParameterForNull(Integer from, Integer size) {
        return from == null || size == null;
    }

    private boolean checkParameterForMin(Integer from, Integer size) {
        return from < 0 || size < 1;
    }

    private Collection<ItemRequestDto> toListItemRequestDto(Page<ItemRequest> requestPage) {
        Collection<ItemRequestDto> requestLis = new ArrayList<>();
        Collection<Item> items = new ArrayList<>();
        requestPage.getContent().forEach(r -> {
            items.addAll(itemService.findAllByRequestId(r.getId()));
            requestLis.add(ItemRequestMapper.toItemRequestDto(r, items));
        });
        return requestLis;
    }

}
