package ch.propbuddy.propbuddy.controller;


import ch.propbuddy.propbuddy.domain.ChatMessage;
import ch.propbuddy.propbuddy.service.ChatService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import javax.sound.sampled.Port;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 * Tests the ChatController
 */
@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ChatControllerTest {

    @MockBean
    private ChatService chatServiceMock;

    private final String WS_URL = "ws://127.0.0.1:8080/propbuddy";
    private CompletableFuture<ChatMessage> completableFuture;
    private StompSession stompSession;

    @Before
    public void setup() throws InterruptedException, ExecutionException, TimeoutException {
        completableFuture = new CompletableFuture<>();
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompSession = stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {}).get(1, SECONDS);
    }


    @Test
    public void testHandleAddUser() throws InterruptedException, ExecutionException, TimeoutException {
        ChatMessage joinMessage = new ChatMessage(ChatMessage.MessageType.JOIN, null, "Joel");

        stompSession.subscribe("/topic/public", new CreateStompFrameHandler());
        stompSession.send("/app/chat.addUser", joinMessage);

        ChatMessage chatMessage = completableFuture.get(5, SECONDS);
        assertEquals("Joel", chatMessage.getSender());
        assertEquals(ChatMessage.MessageType.JOIN, chatMessage.getType());

        verify(chatServiceMock, times(1)).handleAddUser(joinMessage);
    }

    @Test
    public void testHandleRemoveUser() throws InterruptedException, ExecutionException, TimeoutException {
        ChatMessage leaveMessage = new ChatMessage(ChatMessage.MessageType.LEAVE, null, "Joel");

        stompSession.subscribe("/topic/public", new CreateStompFrameHandler());
        stompSession.send("/app/chat.removeUser", leaveMessage);

        ChatMessage chatMessage = completableFuture.get(5, SECONDS);
        assertEquals("Joel", chatMessage.getSender());
        assertEquals(ChatMessage.MessageType.LEAVE, chatMessage.getType());

        verify(chatServiceMock, times(1)).handleRemoveUser(leaveMessage);
    }

    @Test
    public void testHandleChatMessage() throws InterruptedException, ExecutionException, TimeoutException {
        ChatMessage aMessage = new ChatMessage(ChatMessage.MessageType.CHAT, "Hello", "Joel");

        stompSession.subscribe("/topic/public", new CreateStompFrameHandler());
        stompSession.send("/app/chat.sendMessage", aMessage);

        ChatMessage chatMessage = completableFuture.get(5, SECONDS);
        assertEquals("Joel", chatMessage.getSender());
        assertEquals("Hello", chatMessage.getContent());
        assertEquals(ChatMessage.MessageType.CHAT, chatMessage.getType());

        verify(chatServiceMock, times(1)).handleTextMessage(aMessage);
    }

    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class CreateStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return ChatMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture.complete((ChatMessage) o);
        }
    }
}
