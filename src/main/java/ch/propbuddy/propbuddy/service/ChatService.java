package ch.propbuddy.propbuddy.service;

import ch.propbuddy.propbuddy.domain.ChatMessage;
import ch.propbuddy.propbuddy.util.CustomStompSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.websocket.ContainerProvider;
import javax.websocket.WebSocketContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public void handleTextMessage(ChatMessage message) {
        logger.debug("handleTextMessage");
    }

    public void handleAddUser(ChatMessage message) {
        logger.debug("handleAddUser");
    }

    public void handleRemoveUser(ChatMessage message) {
        logger.debug("handleRemoveUser");
    }

}
