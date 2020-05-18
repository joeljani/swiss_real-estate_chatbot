package ch.propbuddy.propbuddy.integration;

import ch.propbuddy.propbuddy.domain.ChatMessage;
import ch.propbuddy.propbuddy.domain.Filter;
import ch.propbuddy.propbuddy.domain.Property;
import ch.propbuddy.propbuddy.scraper.PDFService;
import ch.propbuddy.propbuddy.scraper.RealEstateWebScraper;
import ch.propbuddy.propbuddy.util.CustomStompSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;


@Component
public final class Chatbot {

    private static final Logger logger = LoggerFactory.getLogger(Chatbot.class);

    @Autowired
    private RealEstateWebScraper webScraper;

    @Autowired
    private PDFService pdfService;

    @Value("${server.port}")
    private int port;
    private final WebSocketStompClient stompClient;
    private StompSession stompSession;
    private volatile boolean connected = false;
    private Filter currentFilter;
    private List<Property> currentProperties;
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Instantiate Chatbot as a websocket stomp client.
     */
    public Chatbot() {
        logger.debug("Chat bot initialized");
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient transport = new SockJsClient(transports);
        stompClient = new WebSocketStompClient(transport);
        MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
        stompClient.setMessageConverter(mappingJackson2MessageConverter);
        //executor.scheduleAtFixedRate(helloRunnable, 0, 10, TimeUnit.SECONDS);
    }

    /**
     * Starts a seperate thread which searches for properties with the current filter set. If the properties have
     * changed (new properties found), the chatbot updates the chat participants with the new properties as pdf.
     */
    Runnable helloRunnable = () -> {
        if(currentFilter != null) {
            try {
                List<Property> fetchedProperties = webScraper.fetchProperties(currentFilter.getValues());
                if(currentProperties == null) currentProperties = fetchedProperties; // First fetch
                List<Property> newProperties =  webScraper.getNewProperties(currentProperties, fetchedProperties);
                if(newProperties.size() != 0) {
                    currentProperties = fetchedProperties;
                    sendPropertiesPDFUpdate(pdfService.createPDF(newProperties));
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
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
    public void connect() {
        StompSessionHandler sessionHandler = new CustomStompSessionHandler();
        try {
            stompSession = stompClient.connect("ws://127.0.0.1:"+port+"/propbuddy", sessionHandler).get();
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
     * Send general messages from the Chatbot.
     *
     * @param message which is send from the Chatbot.
     */
    public synchronized void sendMessageToChat(String message) {
        stompSession.send("/app/chat.sendMessage",
                new ChatMessage(ChatMessage.MessageType.CHAT, message, "Chatbot"));
    }

    public synchronized void sendFilterSearchResultToChat(List<Property> properties, String pdfLinkID) {
        String message = properties.stream().map(p -> p.toString()).collect(Collectors.joining(" ;"));
        message = message.concat(" ;PDF:"+pdfLinkID);
        System.out.println(message);
        stompSession.send("/app/chat.sendMessage",
                new ChatMessage(ChatMessage.MessageType.PDF, message, "Chatbot"));
    }


    public synchronized void sendPDFToChat(String message) {
        stompSession.send("/app/chat.sendMessage",
                new ChatMessage(ChatMessage.MessageType.PDF, message, "Chatbot"));
    }

    public synchronized void sendPropertiesPDFUpdate(String message) {
        stompSession.send("/app/chat.sendMessage",
                new ChatMessage(ChatMessage.MessageType.PDF_PROPS_UPDATED, message, "Chatbot"));
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
