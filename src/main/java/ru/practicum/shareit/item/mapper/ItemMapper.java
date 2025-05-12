package ru.practicum.shareit.item.mapper;

import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CreateItemRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequest;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
public class ItemMapper {

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.id())
                .name(itemDto.name())
                .description(itemDto.description())
                .isAvailable(itemDto.available())
                .owner(itemDto.owner())
                .build();
    }

    public Item toItem(CreateItemRequest createRequest) {
        return Item.builder()
                .name(createRequest.name())
                .description(createRequest.description())
                .isAvailable(createRequest.available())
                .build();
    }

    public static Item toItem(UpdateItemRequest updateRequest) {
        Item.ItemBuilder builder = Item.builder()
                .name(updateRequest.name())
                .description(updateRequest.description());

        if (updateRequest.available() != null) {
            builder.isAvailable(updateRequest.available());
        }

        return builder.build();
    }

    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.isAvailable(), item.getOwner());
    }

    public List<ItemDto> toDto(List<Item> items) {
        return items.stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }
}
