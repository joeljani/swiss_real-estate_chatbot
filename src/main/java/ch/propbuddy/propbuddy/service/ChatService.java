package ch.propbuddy.propbuddy.service;

import ch.propbuddy.propbuddy.domain.ChatMessage;
import ch.propbuddy.propbuddy.integration.Chatbot;
import ch.propbuddy.propbuddy.scraper.RealEstateWebScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class ChatService {

    @Autowired
    RealEstateWebScraper realEstateWebScraper;

    @Autowired
    Chatbot chatbot;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public void handleTextMessage(ChatMessage message) {
    }

    public void handleTextMessageToChatBot(ChatMessage message) throws IOException {
        logger.debug("handleTextMessageToChatBot");
        if(chatbot.isConnected()) {
            String propertiesFilter = message.getContent();
            List<String> filters = Arrays.asList(propertiesFilter.split(","));
            List<String> data = realEstateWebScraper.fetchProperties(filters.get(0), filters.get(1), filters.get(2), filters.get(3), filters.get(4));
            StringBuilder stringBuilder = new StringBuilder();
            data.forEach(stringBuilder::append);
            chatbot.sendMessageToChat(stringBuilder.toString());
        }
    }

    public void handleAddUser(ChatMessage message) {
        if(!message.getSender().equals("Chatbot")) {
            executorService.execute(() -> {
                if (!chatbot.isConnected()) {
                    try {
                        chatbot.connect();
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    chatbot.greetNewJoinedUser(message.getSender());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void handleRemoveUser(ChatMessage message) {
        logger.debug("handleRemoveUser");
    }

}
