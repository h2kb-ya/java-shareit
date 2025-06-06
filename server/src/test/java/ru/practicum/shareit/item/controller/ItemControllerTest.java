package ru.practicum.shareit.item.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.ShareItConstants.USER_ID_HEADER;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private ItemMapper itemMapper;

    private final long userId = 1L;
    private final long itemId = 10L;
    private final User user =  new User(userId, "Донателло", "email");
    private final Item item = new Item(itemId, "Палка", "Техно посох", true, user, null);
    private final ItemDto itemDto = new ItemDto(itemId, "Палка", "Техно посох", true, null);
    private final ItemOwnerDto itemOwnerDto = new ItemOwnerDto(itemId, "Палка", "Техно посох", true, null, null, List.of());

    @Test
    void createItem_shouldCreateNewItem() throws Exception {
        ItemCreateRequest request = new ItemCreateRequest("Палка", "Техно посох", true, null);

        when(itemMapper.toItem(any(ItemCreateRequest.class))).thenReturn(item);
        when(itemService.createItem(anyLong(), any(Item.class), any())).thenReturn(item);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, userId)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.id()))
                .andExpect(jsonPath("$.name").value(itemDto.name()))
                .andExpect(jsonPath("$.description").value(itemDto.description()))
                .andExpect(jsonPath("$.available").value(itemDto.available()));

        verify(itemMapper).toItem(request);
        verify(itemService).createItem(userId, item, request.requestId());
        verify(itemMapper).toItemDto(item);
    }

    @Test
    void updateItem_shouldUpdateItem() throws Exception {
        ItemUpdateRequest request = new ItemUpdateRequest("Ветка", "Посох-2", false);
        Item updatedItem = new Item(itemId, "Ветка", "Посох-2", false, user, null);
        ItemDto updatedDto = new ItemDto(itemId, "Векта", "Посох-2", false, null);

        when(itemService.getItemById(itemId)).thenReturn(item);
        when(itemService.updateItem(userId, item)).thenReturn(updatedItem);
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(updatedDto);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, userId)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedDto.id()))
                .andExpect(jsonPath("$.name").value(updatedDto.name()))
                .andExpect(jsonPath("$.description").value(updatedDto.description()))
                .andExpect(jsonPath("$.available").value(updatedDto.available()));

        verify(itemService).getItemById(itemId);
        verify(itemService).updateItem(userId, item);
        verify(itemMapper).toItemDto(updatedItem);
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        when(itemService.getItemDetailsForOwner(itemId, userId)).thenReturn(itemOwnerDto);

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemOwnerDto.id()))
                .andExpect(jsonPath("$.name").value(itemOwnerDto.name()))
                .andExpect(jsonPath("$.description").value(itemOwnerDto.description()))
                .andExpect(jsonPath("$.available").value(itemOwnerDto.available()));

        verify(itemService).getItemDetailsForOwner(itemId, userId);
    }

    @Test
    void getAllItemsByUserId_shouldReturnUserItems() throws Exception {
        when(itemService.getItemsByUser(userId)).thenReturn(List.of(item));
        when(itemMapper.toItemDtoList(List.of(item))).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items")
                        .header(USER_ID_HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.id()))
                .andExpect(jsonPath("$[0].name").value(itemDto.name()));

        verify(itemService).getItemsByUser(userId);
        verify(itemMapper).toItemDtoList(List.of(item));
    }

    @Test
    void searchAvailableItems_shouldReturnMatchingItems() throws Exception {
        String text = "посох";

        when(itemService.searchAvailableItems(text)).thenReturn(List.of(item));
        when(itemMapper.toItemDtoList(List.of(item))).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", text))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.id()))
                .andExpect(jsonPath("$[0].name").value(itemDto.name()));

        verify(itemService).searchAvailableItems(text);
        verify(itemMapper).toItemDtoList(List.of(item));
    }

    @Test
    void addComment_shouldAddComment() throws Exception {
        CommentCreateRequest request = new CommentCreateRequest("Отличная вещь!");
        Comment comment = new Comment(1L, "Отличная вещь!", LocalDateTime.now(), item, user);
        CommentDto commentDto = new CommentDto(1L, "Отличная вещь!", itemId, userId, user.getName(), LocalDateTime.now());

        when(itemMapper.toComment(any(CommentCreateRequest.class))).thenReturn(comment);
        when(itemService.sendComment(comment, userId, itemId)).thenReturn(comment);
        when(itemMapper.toCommentDto(comment)).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(USER_ID_HEADER, userId)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.id()))
                .andExpect(jsonPath("$.text").value(commentDto.text()))
                .andExpect(jsonPath("$.authorName").value(commentDto.authorName()));

        verify(itemMapper).toComment(request);
        verify(itemService).sendComment(comment, userId, itemId);
        verify(itemMapper).toCommentDto(comment);
    }
}
