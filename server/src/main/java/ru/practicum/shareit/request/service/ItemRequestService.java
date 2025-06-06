package ru.practicum.shareit.request.service;

import java.util.List;
import ru.practicum.shareit.request.dto.ItemRequestAndAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestService {

    List<ItemRequest> getAllRequests();

    List<ItemRequestAndAnswersDto> getRequestsByRequestorId(Long requestorId);

    ItemRequest createRequest(ItemRequest itemRequest, Long requestorId);

    ItemRequestAndAnswersDto getRequestById(Long requestId);
}
