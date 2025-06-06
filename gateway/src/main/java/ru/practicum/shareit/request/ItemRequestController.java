package ru.practicum.shareit.request;

import static ru.practicum.shareit.util.ShareItConstants.USER_ID_HEADER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Controller
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests() {
        log.info("Getting all requests");

        return itemRequestClient.getAllRequests();
    }

    @GetMapping
    public ResponseEntity<Object> getRequestByRequestorId(@RequestHeader(USER_ID_HEADER) long requestorId) {
        log.info("Getting request by requestor id {}", requestorId);

        return itemRequestClient.getRequestByRequestorId(requestorId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestByRequestId(@PathVariable long requestId) {
        log.info("Getting request by request id {}", requestId);

        return itemRequestClient.getRequestByRequestId(requestId);
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(USER_ID_HEADER) long requestorId,
            @RequestBody ItemRequestCreateDto createRequest) {
        log.info("Creating request {} by requestorId {}", createRequest, requestorId);
        return itemRequestClient.createRequest(requestorId, createRequest);
    }

}
