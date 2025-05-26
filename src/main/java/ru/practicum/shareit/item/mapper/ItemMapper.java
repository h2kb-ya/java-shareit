package ru.practicum.shareit.item.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = UserMapper.class)
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "itemRequest", ignore = true)
    Item toItem(CreateItemRequest createRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "itemRequest", ignore = true)
    void updateItem(@MappingTarget Item item, UpdateItemRequest updateRequest);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "requestId", source = "itemRequest.id")
    ItemDto toDto(Item item);

    List<ItemDto> toItemDtoList(List<Item> items);
}
