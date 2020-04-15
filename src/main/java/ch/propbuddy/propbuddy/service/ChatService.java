package ch.propbuddy.propbuddy.service;

import ch.propbuddy.propbuddy.domain.ChatMessage;
import ch.propbuddy.propbuddy.integration.Chatbot;
import ch.propbuddy.propbuddy.scraper.RealEstateWebScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;


@Service
public class ChatService {

    @Autowired
    RealEstateWebScraper realEstateWebScraper;

    @Autowired
    Chatbot chatbot;

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public void handleTextMessage(ChatMessage message) {
        logger.debug("handleTextMessage");
    }

    public void handleTextMessageToChatBot(ChatMessage message) throws IOException {
        logger.debug("handleTextMessageToChatBot");
        if(chatbot.isConnected()) {
            List<String> data = realEstateWebScraper.fetchProperties("8002", "3000.0", "4500.0", "2.0", "4.0");
            StringBuilder stringBuilder = new StringBuilder();
            data.forEach(stringBuilder::append);
            chatbot.sendMessageToChat(stringBuilder.toString());
        }
    }

    public void handleAddUser(ChatMessage message) {
        if(!message.getSender().equals("Chatbot")) {
            if(!chatbot.isConnected()) {
                chatbot.connect();
            }
            chatbot.greetNewJoinedUser(message.getSender());
        }
        logger.debug("handleAddUser");
    }

    public void handleRemoveUser(ChatMessage message) {
        logger.debug("handleRemoveUser");
    }

}
