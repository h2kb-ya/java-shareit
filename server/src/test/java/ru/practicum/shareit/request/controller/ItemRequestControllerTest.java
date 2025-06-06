package ru.practicum.shareit.request.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestAndAnswersDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.ShareItConstants;

@WebMvcTest(value = ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private ItemRequestMapper itemRequestMapper;

    private final long requestorId = 1L;
    private final long requestId = 1L;
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(
            1L, "Нужна дрель", requestorId, LocalDateTime.now());
    private final ItemRequestAndAnswersDto itemRequestWithAnswers = new ItemRequestAndAnswersDto(
            1L, "Нужна дрель", requestorId, LocalDateTime.now(), List.of());
    private final ItemRequestCreateRequest createRequest = new ItemRequestCreateRequest("Нужна дрель");

    @Test
    void getAllRequests_shouldReturnAllRequests() throws Exception {
        List<ItemRequestDto> requests = List.of(itemRequestDto);

        when(itemRequestService.getAllRequests()).thenReturn(List.of(new ItemRequest()));
        when(itemRequestMapper.toItemRequestDtoList(any())).thenReturn(requests);

        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.id()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.description()));

        verify(itemRequestService).getAllRequests();
        verify(itemRequestMapper).toItemRequestDtoList(any());
    }

    @Test
    void getRequestsByRequestorId_shouldReturnUserRequests() throws Exception {
        List<ItemRequestAndAnswersDto> requests = List.of(itemRequestWithAnswers);

        when(itemRequestService.getRequestsByRequestorId(requestorId)).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(ShareItConstants.USER_ID_HEADER, requestorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestWithAnswers.id()))
                .andExpect(jsonPath("$[0].description").value(itemRequestWithAnswers.description()));

        verify(itemRequestService).getRequestsByRequestorId(requestorId);
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        when(itemRequestService.getRequestById(requestId)).thenReturn(itemRequestWithAnswers);

        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestWithAnswers.id()))
                .andExpect(jsonPath("$.description").value(itemRequestWithAnswers.description()));

        verify(itemRequestService).getRequestById(requestId);
    }

    @Test
    void createRequest_shouldCreateNewRequest() throws Exception {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Нужна дрель");

        when(itemRequestMapper.toItemRequest(any(ItemRequestCreateRequest.class))).thenReturn(itemRequest);
        when(itemRequestService.createRequest(any(ItemRequest.class), anyLong()))
                .thenReturn(itemRequest);
        when(itemRequestMapper.toItemRequestDto(any(ItemRequest.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(ShareItConstants.USER_ID_HEADER, requestorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.id()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.description()));

        verify(itemRequestMapper).toItemRequest(createRequest);
        verify(itemRequestService).createRequest(itemRequest, requestorId);
        verify(itemRequestMapper).toItemRequestDto(itemRequest);
    }

    @Test
    void getRequestById_shouldReturnNotFound_whenRequestNotExists() throws Exception {
        when(itemRequestService.getRequestById(anyLong()))
                .thenThrow(new NotFoundException("Request not found"));

        mockMvc.perform(get("/requests/{requestId}", 999L))
                .andExpect(status().isNotFound());

        verify(itemRequestService).getRequestById(999L);
    }

    @Test
    void getRequestsByRequestorId_shouldReturnEmptyList_whenNoRequests() throws Exception {
        when(itemRequestService.getRequestsByRequestorId(requestorId)).thenReturn(List.of());

        mockMvc.perform(get("/requests")
                        .header(ShareItConstants.USER_ID_HEADER, requestorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(itemRequestService).getRequestsByRequestorId(requestorId);
    }

}
