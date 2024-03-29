package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * контроллер для работы с запросами
 */
@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class RequestController {

    private static final String HEADER_REQUEST = "X-Sharer-User-Id"; // заголовок запроса в котором передаётся id владельца вещи
    private final RequestService requestService;
    private final ItemService itemService;

    public RequestController(RequestServiceImpl requestService, ItemServiceImpl itemService) {
        this.requestService = requestService;
        this.itemService = itemService;
    }


    // создание нового запроса вещи.
    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto itemRequestDto,
                                        @RequestHeader(HEADER_REQUEST) Long requesterId) {
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = requestService.createRequest(itemRequestDto, requesterId);

        Collection<Item> items = itemService.findAllByRequestId(itemRequest.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    //Получение списка всех своих запросов вместе с данными об ответах на них. Эндпоинт GET /request/?from={from}&size={size}
    @GetMapping
    public Collection<ItemRequestDto> getAllRequestsByUserId(@RequestHeader(HEADER_REQUEST) Long requesterId,
                                                             @RequestParam String from,
                                                             @RequestParam String size) {

        int page = Integer.parseInt(from) / Integer.parseInt(size);
        Pageable pageable = PageRequest.of(page, Integer.parseInt(size));
        List<ItemRequest> itemRequestPage = requestService.getAllRequestsByUserId(requesterId, pageable);

        return toListItemRequestDto(itemRequestPage);
    }

    // Получение списка запросов, созданных другими пользователями. Эндпоинт GET /request/all?from={from}&size={size}
    @GetMapping("/all")
    public Collection<ItemRequestDto> getAllRequestsCreatedAnotherUsers(@RequestHeader(HEADER_REQUEST) Long requesterId,
                                                                        @RequestParam String from,
                                                                        @RequestParam String size) {

        int page = Integer.parseInt(from) / Integer.parseInt(size);
        Pageable pageable = PageRequest.of(page, Integer.parseInt(size));
        List<ItemRequest> itemRequestPage = requestService.getAllRequestsCreatedAnotherUsers(requesterId, pageable);

        return toListItemRequestDto(itemRequestPage);

    }

    // Получение данных об одном конкретном запросе вместе с данными об ответах на него. Эндпоинт GET /request/{requestId}
    @GetMapping("/{id}")
    public ItemRequestDto getRequestById(@RequestHeader(HEADER_REQUEST) Long requesterId, @PathVariable Long id) {

        ItemRequest itemRequest = requestService.getRequestById(id, requesterId);
        Collection<Item> items = itemService.findAllByRequestId(itemRequest.getId());
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    // преобразовывает список ItemRequest в список ItemRequestDto
    private Collection<ItemRequestDto> toListItemRequestDto(List<ItemRequest> itemRequestPage) {
        Collection<ItemRequestDto> requestLis = new ArrayList<>();
        Collection<Item> items = new ArrayList<>();
        itemRequestPage.forEach(r -> {
            items.addAll(itemService.findAllByRequestId(r.getId()));
            requestLis.add(ItemRequestMapper.toItemRequestDto(r, items));
        });
        return requestLis;
    }

}
