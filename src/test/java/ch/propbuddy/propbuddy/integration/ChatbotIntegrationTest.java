package ch.propbuddy.propbuddy.integration;


import ch.propbuddy.propbuddy.domain.ChatMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class tests the interaction functionality of the Chatbot
 */
@RunWith(SpringRunner.class)
@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ChatbotIntegrationTest {

    private final String WS_URL = "ws://127.0.0.1:8080/propbuddy";
    private BlockingQueue<ChatMessage> blockingQueue;
    private StompSession stompSession;

    @Before
    public void setup() throws InterruptedException, ExecutionException, TimeoutException {
        blockingQueue = new LinkedBlockingDeque<>();
        WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(createTransportClient()));
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        stompSession = stompClient.connect(WS_URL, new StompSessionHandlerAdapter() {}).get(2, SECONDS);
    }

    /**
     * Tests if the sequence of send and received message is correct.
     *
     * @throws InterruptedException {@link java.util.concurrent.Future#get()}
     */
    @Test
    public void testGetWelcomeMessagesFromChatBot() throws InterruptedException {
        final String firstUserName = "Joel";
        userEntersChat(firstUserName); // 1. User enters chat
        checkReceivedJoinMessage(firstUserName); //2. User must enter chat before Chatbot
        checkReceivedJoinMessage("Chatbot"); //3. Chatbot enters chat if there is user
        chatbotWelcomesUser(firstUserName); //4. Chatbot welcomes user
    }

    private void checkReceivedJoinMessage(String joinedUser) throws InterruptedException {
        ChatMessage receivedJoinMessage = blockingQueue.poll(3, SECONDS);
        assert receivedJoinMessage != null;
        assertEquals(joinedUser, receivedJoinMessage.getSender());
    }

    private void userEntersChat(String username) {
        ChatMessage joinMessageUser = new ChatMessage(ChatMessage.MessageType.JOIN, null, username);
        stompSession.subscribe("/topic/public", new TestStompFrameHandler());
        stompSession.send("/app/chat.addUser", joinMessageUser);
    }

    private void chatbotWelcomesUser(String username) throws InterruptedException {
        ChatMessage receivedChatbotWelcomeMessage = blockingQueue.poll(3, SECONDS);
        assert receivedChatbotWelcomeMessage != null;
        assertEquals("Chatbot", receivedChatbotWelcomeMessage.getSender());
        assertEquals("Welcome " + username + "! What can i do for you?", receivedChatbotWelcomeMessage.getContent());
    }


    private List<Transport> createTransportClient() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    private class TestStompFrameHandler implements StompFrameHandler {
        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return ChatMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            blockingQueue.offer((ChatMessage) o);
        }
    }
}
