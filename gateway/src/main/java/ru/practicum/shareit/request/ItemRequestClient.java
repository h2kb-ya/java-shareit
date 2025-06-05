package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Service
public class ItemRequestClient extends BaseClient {

    private final static String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getAllRequests() {
        return get("/all");
    }

    public ResponseEntity<Object> createRequest(long requestorID, ItemRequestCreateDto createRequest) {
        return post("", requestorID, createRequest);
    }

    public ResponseEntity<Object> getRequestByRequestId(long requestId) {
        return get("/" + requestId);
    }

    public ResponseEntity<Object> getRequestByRequestorId(long requestorId) {
        return get("", requestorId);
    }
}
