package ch.propbuddy.propbuddy.integration;

import ch.propbuddy.propbuddy.domain.ChatMessage;
import ch.propbuddy.propbuddy.domain.Filter;
import ch.propbuddy.propbuddy.util.CustomStompSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Component
public final class Chatbot {

    private static final Logger logger = LoggerFactory.getLogger(Chatbot.class);

    @Value("${server.port}")
    private int port;
    private final WebSocketStompClient stompClient;
    private StompSession stompSession;
    private volatile boolean connected = false;
    private Filter currentFilter;
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    /**
     * Instantiate Chatbot as a websocket stomp client.
     */
    public Chatbot() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient transport = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(transport);
        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        stompClient.setMessageConverter(mappingJackson2MessageConverter);
        // Start seperate thread which searches for properties with the current filter set
        executor.scheduleAtFixedRate(helloRunnable, 0, 10, TimeUnit.SECONDS);
    }

    Runnable helloRunnable = () -> {
        if(currentFilter != null) System.out.println(currentFilter.toString());;
    };

    /**
     * Shows if Chatbot is connected to the chat.
     *
     * @return true if Chatbot is connected to the chat.
     */
    public boolean isConnected() {
        return this.connected;
    }

    /**
     * Connects to the web socket server. This method is called once when the first user enters the chat.
     */
    public synchronized void connect() {
        StompSessionHandler sessionHandler = new CustomStompSessionHandler();
        try {
            stompSession = stompClient.connect("ws://127.0.0.1:"+port+"/propbuddy",
                    sessionHandler).get();
            stompSession.subscribe("/topic/public", sessionHandler);
            connected = true;
            stompSession.send("/app/chat.addUser",
                    new ChatMessage(ChatMessage.MessageType.JOIN, "", "Chatbot")); //Chatbot enters Chat
        } catch (InterruptedException | ExecutionException e) {
            logger.error("connect exception thrown {}", e.getMessage());
        }
    }

    /**
     * Greets a new joined user.
     *
     * @param newUser name to greet.
     */
    public void greetNewJoinedUser(String newUser) throws InterruptedException {
        sendMessageToChat("Welcome " + newUser + "! What can i do for you?");
        Thread.sleep(400); //TODO: Better alternative than thread sleep
        sendInfoMessageToChat("Search for properties to rent, Search for properties to buy");
    }

    /**
     * Send messages from the Chatbot.
     *
     * @param message which is send from the Chatbot.
     */
    public synchronized void sendMessageToChat(String message) {
        stompSession.send("/app/chat.sendMessage",
                new ChatMessage(ChatMessage.MessageType.CHAT, message, "Chatbot"));
    }

    public synchronized void sendPDFToChat(String message) {
        stompSession.send("/app/chat.sendMessage",
                new ChatMessage(ChatMessage.MessageType.PDF, message, "Chatbot"));
    }

    public synchronized void setCurrentFilter(Filter newCurrentFilter) {
        currentFilter = newCurrentFilter;
        stompSession.send("/app/chat.sendMessage",
                new ChatMessage(ChatMessage.MessageType.FILTER_CHANGED, currentFilter.toString(), null));
    }

    /**
     * Send messages from the Chatbot.
     *
     * @param message which is send from the Chatbot.
     */
    public synchronized void sendInfoMessageToChat(String message) {
        stompSession.send("/app/chat.sendMessage",
                new ChatMessage(ChatMessage.MessageType.INFO, message, "Chatbot"));
    }
}
