package ru.practicum.shareit.item.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {UserMapper.class, BookingMapper.class})
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "itemRequest", ignore = true)
    Item toItem(ItemCreateRequest createRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "itemRequest", ignore = true)
    void updateItem(@MappingTarget Item item, ItemUpdateRequest updateRequest);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemDto toItemDto(Item item);

    @Mapping(target = "id", source = "item.id")
    ItemOwnerDto toItemOwnerDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments);

    List<ItemDto> toItemDtoList(List<Item> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "createDate", expression = "java(LocalDateTime.now())")
    Comment toComment(CommentCreateRequest createRequest);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "created", source = "createDate")
    CommentDto toCommentDto(Comment comment);
}
