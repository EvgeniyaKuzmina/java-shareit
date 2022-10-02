package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.request.dto.RequestDto;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Optional;

/**
 * контроллер для работы с запросами
 */
@Controller
@RequestMapping(path = "/requests")
@Slf4j
@Validated
@RequiredArgsConstructor
public class RequestController {

    private static final String FROM = "0";
    private static final String SIZE = "10";
    private static final String HEADER_REQUEST = "X-Sharer-User-Id"; // заголовок запроса в котором передаётся id владельца вещи
    private final RequestClient requestClient;


    // создание нового запроса вещи.
    @PostMapping
    public ResponseEntity<Object> createRequest(@Valid @RequestBody RequestDto itemRequestDto,
                                                @RequestHeader(HEADER_REQUEST) Long requesterId) throws ArgumentNotValidException {
        if (Optional.ofNullable(itemRequestDto.getDescription()).isEmpty() || itemRequestDto.getDescription().isBlank()) {
            log.warn("gateway: RequestController.createRequest: Пустое описание запроса");
            throw new ArgumentNotValidException("Описание запроса не может быть пустым");
        }
        return requestClient.createRequest(itemRequestDto, requesterId);
    }

    //Получение списка всех своих запросов вместе с данными об ответах на них. Эндпоинт GET /request/?from={from}&size={size}
    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUserId(@RequestHeader(HEADER_REQUEST) Long requesterId,
                                                         @RequestParam(defaultValue = FROM) @PositiveOrZero String from,
                                                         @RequestParam(defaultValue = SIZE) @Positive String size) {

        return requestClient.getAllRequestsByUserId(requesterId, from, size);
    }

    // Получение списка запросов, созданных другими пользователями. Эндпоинт GET /request/all?from={from}&size={size}
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsCreatedAnotherUsers(@RequestHeader(HEADER_REQUEST) Long requesterId,
                                                                    @RequestParam(defaultValue = FROM) @PositiveOrZero String from,
                                                                    @RequestParam(defaultValue = SIZE) @Positive String size) {
        return requestClient.getAllRequestsCreatedAnotherUsers(requesterId, from, size);

    }

    // Получение данных об одном конкретном запросе вместе с данными об ответах на него. Эндпоинт GET /request/{requestId}
    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(HEADER_REQUEST) Long requesterId, @PathVariable Long id) {
        return requestClient.getRequestById(requesterId, id);
    }


}
