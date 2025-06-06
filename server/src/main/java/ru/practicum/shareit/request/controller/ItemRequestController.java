package ru.practicum.shareit.request.controller;

import static ru.practicum.shareit.util.ShareItConstants.USER_ID_HEADER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestAndAnswersDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper itemRequestMapper;

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests() {
        log.info("Request: GET /requests");

        return itemRequestMapper.toItemRequestDtoList(itemRequestService.getAllRequests());
    }

    @GetMapping
    public List<ItemRequestAndAnswersDto> getRequestsByRequestorId(@RequestHeader(USER_ID_HEADER) final Long requestorId) {
        log.info("Request: GET /requests by requestorId: {}", requestorId);

        return itemRequestService.getRequestsByRequestorId(requestorId);
    }

    @GetMapping("{requestId}")
    public ItemRequestAndAnswersDto getRequestById(@PathVariable("requestId") final Long requestId) {
        log.info("Request: GET /requests by requestId: {}", requestId);

        return itemRequestService.getRequestById(requestId);
    }

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader(USER_ID_HEADER) final Long requestorId,
            @RequestBody ItemRequestCreateRequest createRequest) {
        log.info("Request: POST /requests, data: {} by requestorId {}", createRequest, requestorId);
        final ItemRequest itemRequest = itemRequestMapper.toItemRequest(createRequest);

        return itemRequestMapper.toItemRequestDto(itemRequestService.createRequest(itemRequest, requestorId));
    }

}
