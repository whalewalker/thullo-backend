package com.thullo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class WebsocketService<T> {
    private final List<WebSocketSession> subscribers = new CopyOnWriteArrayList<>();

    public void subscribe(WebSocketSession session) {
        subscribers.add(session);
    }

    public void unsubscribe(WebSocketSession session) {
        subscribers.remove(session);
    }

    public void notifySubscribers(T t) throws IOException {
        TextMessage message = new TextMessage(new ObjectMapper().writeValueAsString(t));
        for (WebSocketSession subscriber : subscribers) {
            subscriber.sendMessage(message);
        }
    }
}
