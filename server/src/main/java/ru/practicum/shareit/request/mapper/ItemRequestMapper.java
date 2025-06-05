package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAndAnswersDto;
import ru.practicum.shareit.request.dto.ItemRequestAnswerDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", expression = "java(LocalDateTime.now())")
    ItemRequest toItemRequest(ItemRequestCreateRequest createRequest);

    @Mapping(target = "requestorId", source = "requestor.id")
    @Mapping(target = "created", source = "createDate")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Mapping(target = "itemId", source = "id")
    @Mapping(target = "ownerId", source = "owner.id")
    ItemRequestAnswerDto toItemRequestAnswerDto(Item item);

    @Mapping(target = "requestorId", source = "itemRequest.requestor.id")
    @Mapping(target = "created", source = "itemRequest.createDate")
    @Mapping(target = "items", source = "answers")
    ItemRequestAndAnswersDto toItemRequestAndAnswersDto(ItemRequest itemRequest, List<ItemRequestAnswerDto> answers);

    default List<ItemRequestAndAnswersDto> toItemRequestAndAnswersDtoList(List<ItemRequest> itemRequests, List<Item> answers) {
        if (itemRequests == null) {
            return List.of();
        }

        Map<Long, List<ItemRequestAnswerDto>> answersMap = answers.stream()
                .filter(item -> item.getItemRequest() != null)
                .collect(Collectors.groupingBy(
                        item -> item.getItemRequest().getId(),
                        Collectors.mapping(this::toItemRequestAnswerDto, Collectors.toList())
                ));

        return itemRequests.stream()
                .map(request -> toItemRequestAndAnswersDto(
                        request,
                        answersMap.getOrDefault(request.getId(), List.of())
                ))
                .toList();
    }

    default ItemRequestAndAnswersDto toItemRequestAndAnswersDtoWithItems(ItemRequest itemRequest, List<Item> items) {
        return toItemRequestAndAnswersDto(
                itemRequest,
                items.stream()
                        .map(this::toItemRequestAnswerDto)
                        .toList()
        );
    }

    List<ItemRequestDto> toItemRequestDtoList(List<ItemRequest> itemRequests);

}