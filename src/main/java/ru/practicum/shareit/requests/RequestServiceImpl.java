package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.mapper.ItemRequestMapper;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;

    @Override
    public ItemRequest createRequest(ItemRequestDto requestDto, Long id) throws ObjectNotFountException {
        User user = userService.getUserById(id);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(requestDto, user);
        return requestRepository.save(itemRequest);
    }

    @Override
    public Page<ItemRequest> getAllRequestsByUserId(Long id, Pageable pageable) throws ObjectNotFountException {
        userService.getUserById(id); // проверяем что пользователь с указанным id есть
        return requestRepository.findAllByRequesterIdOrderByCreatedDesc(id, pageable);
    }

    @Override
    public Page<ItemRequest> getAllRequestsCreatedAnotherUsers(Long id, Pageable pageable) throws ObjectNotFountException {
        userService.getUserById(id); // проверяем что пользователь с указанным id есть
        return requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(id, pageable);
    }

    @Override
    public ItemRequest getRequestById(Long id, Long requesterId) throws ObjectNotFountException {
        userService.getUserById(requesterId); // проверяем что пользователь с указанным id есть
        Optional<ItemRequest> itemRequest = requestRepository.findById(id);
        itemRequest.orElseThrow(() -> {
            log.warn("Запроса с указанным id {} нет", id);
            return new ObjectNotFountException("Запроса с указанным id " + id + " нет");
        });

        log.warn("Запрос с указанным id {} получен", id);
        return itemRequest.get();
    }
}
