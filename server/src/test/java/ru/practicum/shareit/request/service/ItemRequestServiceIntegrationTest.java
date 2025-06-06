package ru.practicum.shareit.request.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestAndAnswersDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

@SpringBootTest
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService target;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserService userService;

    @Test
    void getAllRequests_happyPath_requestsReturned() {
        List<ItemRequest> result = target.getAllRequests();

        assertEquals(4, result.size());

        ItemRequest itemRequest = result.get(0);
        assertNotNull(itemRequest.getRequestor());
        assertEquals("Шреддер", itemRequest.getRequestor().getName());
    }

    @Test
    void getRequestsByRequestorId_happyPath_requestsWithAnswersReturned() {
        long requestorId = 1L;
        List<ItemRequestAndAnswersDto> result = target.getRequestsByRequestorId(requestorId);

        assertEquals(1, result.size());
        assertEquals("Нужны новые катаны для тренировок", result.getFirst().description());
        assertEquals(1, result.getFirst().items().size());
        assertEquals("Катаны", result.getFirst().items().getFirst().name());
    }

    @Test
    void createRequest_happyPath_requestsCreated() {
        long requestorId = userService.getUsers().getFirst().getId();

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужен новый посох для тренировок");
        itemRequest.setCreateDate(LocalDateTime.now());

        ItemRequest createdRequest = target.createRequest(itemRequest, requestorId);

        assertEquals(requestorId, createdRequest.getRequestor().getId());

        ItemRequest storedRequest = itemRequestRepository.findById(createdRequest.getId()).orElseThrow();
        assertEquals(createdRequest.getDescription(), storedRequest.getDescription());
    }

}
