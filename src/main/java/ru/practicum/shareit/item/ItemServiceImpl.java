package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ArgumentNotValidException;
import ru.practicum.shareit.exception.ObjectNotFountException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final UserServiceImpl userServiceImpl;
    private final ItemRepository itemRepository;

    // создание вещи
    @Override
    public Item createItem(Item item, Long id) throws ObjectNotFountException {
        User user = userServiceImpl.getUserById(id); // проверяем что пользователь с таким id существует
        item.setOwner(user);
        return itemRepository.save(item);
    }

    // изменение вещи
    @Override
    public Item updateItem(Item updItem, Long id, Long userId) throws ObjectNotFountException, ValidationException {
        userServiceImpl.getUserById(userId); // проверяем что пользователь с таким id существует
        Item item = getItemById(id); // получаем вещь по Id
        if (!Objects.equals(
                item.getOwner().getId(), userId)) { // проверяем что передан id владельца в заголовке
            log.warn("ItemServiceImpl.updateItem: Указан неверный id владельца вещи");
            throw new ObjectNotFountException("Указан неверный id  владельца вещи");
        }

        // обновление вещи
        Optional.ofNullable(updItem.getName()).ifPresent(item::setName);
        Optional.ofNullable(updItem.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(updItem.getAvailable()).ifPresent(item::setAvailable);

        return itemRepository.save(item);
    }

    // удаление вещи по id
    @Override
    public void removeItem(Long id, Long userId) throws ValidationException, ArgumentNotValidException, ObjectNotFountException {
        Item item = getItemById(id); // получаем вещь по Id
        userServiceImpl.getUserById(userId); // проверяем что пользователь с таким id существует

        if (!Objects.equals(item.getOwner().getId(), id)) { // проверяем что передан id владельца в заголовке
            log.warn("ItemServiceImpl.removeItem: Указан неверный id владельца вещи");
            throw new ArgumentNotValidException("Указан неверный id  владельца вещи");
        }

        log.info("ItemServiceImpl.removeItem: Вещь с указанным id {} удалена", id);
        itemRepository.deleteById(id);
    }

    // Просмотр информации о конкретной вещи по её идентификатору
    @Override
    public Item getItemById(Long id) throws ValidationException {
        Optional<Item> itemOpt = itemRepository.findById(id);
        itemOpt.orElseThrow(() -> {
            log.error("ItemServiceImpl.getItemById: Вещи с таким id нет ");
            return new ValidationException("Вещи с таким id нет");
        });

        return itemOpt.get();
    }

    // Просмотр владельцем списка всех его вещей
    @Override
    public Collection<Item> getAllItemByUserId(Long id) throws ObjectNotFountException {
        userServiceImpl.getUserById(id); // проверяем что пользователь с таким id существует
        return itemRepository.findAllByOwnerId(id);
    }


    //Поиск вещи потенциальным арендатором по части названия или описания
    @Override
    public Collection<Item> searchItemByNameOrDescription(String text) {
        return itemRepository.findByDescriptionOrNameContainingIgnoreCase(text, text);
    }
}
