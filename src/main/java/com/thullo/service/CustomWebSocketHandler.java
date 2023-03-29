package com.thullo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Service
public class CustomWebSocketHandler<T> extends TextWebSocketHandler {

    @Autowired
    private WebsocketService<T> websocketService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        websocketService.subscribe(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        websocketService.unsubscribe(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        T obj = convertPayloadToObject(payload);

        // perform additional validation or processing on the incoming message

        // broadcast the update to all subscribers
        broadcastUpdate(obj);
    }

    private T convertPayloadToObject(String payload) throws JsonProcessingException {
        return new ObjectMapper().readValue(payload, new TypeReference<>() {
        });
    }

    private void broadcastUpdate(T obj) throws IOException {
        websocketService.notifySubscribers(obj);
    }
}
