package ru.practicum.shareit.request.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestAndAnswersDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

@Service
@RequiredArgsConstructor
@Log4j2
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final ItemRequestMapper itemRequestMapper;

    @Override
    public List<ItemRequest> getAllRequests() {
        return itemRequestRepository.findAllByOrderByCreateDateDesc();
    }

    @Override
    public List<ItemRequestAndAnswersDto> getRequestsByRequestorId(Long requestorId) {
        userService.getUserById(requestorId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreateDateDesc(requestorId);

        if (itemRequests.isEmpty()) {
            return List.of();
        }

        List<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .toList();
        List<Item> requestAnswers = itemService.getItemsByItemRequestIds(itemRequestIds);

        return itemRequestMapper.toItemRequestAndAnswersDtoList(itemRequests, requestAnswers);
    }

    @Override
    public ItemRequest createRequest(ItemRequest itemRequest, Long requestorId) {
        User requestor = userService.getUserById(requestorId);
        itemRequest.setRequestor(requestor);

        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequestAndAnswersDto getRequestById(Long requestId) {
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("ItemRequest " + requestId + " not found"));

        List<Item> requestAnswers = itemService.getItemsByItemRequestIds(List.of(itemRequest.getId()));

        return itemRequestMapper.toItemRequestAndAnswersDtoWithItems(itemRequest, requestAnswers);
    }

}
