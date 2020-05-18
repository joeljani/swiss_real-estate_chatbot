package ch.propbuddy.propbuddy.service;

import ch.propbuddy.propbuddy.domain.ChatMessage;
import ch.propbuddy.propbuddy.domain.Filter;
import ch.propbuddy.propbuddy.domain.Property;
import ch.propbuddy.propbuddy.integration.Chatbot;
import ch.propbuddy.propbuddy.scraper.PDFService;
import ch.propbuddy.propbuddy.scraper.RealEstateWebScraper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    @Autowired
    RealEstateWebScraper realEstateWebScraper;

    @Autowired
    PDFService pdfService;

    @Autowired
    Chatbot chatbot;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile List<String> currentUsers = new CopyOnWriteArrayList<>();

    public void handleTextMessage(ChatMessage message) {
    }

    public void handleTextMessageToChatBot(ChatMessage message) throws IOException {
        logger.debug("handleTextMessageToChatBot");
        if(chatbot.isConnected()) {
            //TODO: Add Dialogflow integration [Make chatbot smart]
        }
    }

    public void handleFilterSearchMessageToChatbot(ChatMessage message) throws IOException, InterruptedException {
        if(chatbot.isConnected()) {
            List<String> filters = Arrays.asList(message.getContent().split(","));
            Filter currentFilter = new Filter(filters.get(0), filters.get(1), filters.get(2), filters.get(3), filters.get(4));
            List<Property> properties = realEstateWebScraper.fetchProperties(currentFilter.getValues());
            String pdfLinkID = pdfService.createPDF(properties);
            chatbot.sendFilterSearchResultToChat(properties, pdfLinkID);
            chatbot.setCurrentFilter(currentFilter);
        }
    }

    public synchronized void handleAddUser(ChatMessage message) {
        if(!message.getSender().equals("Chatbot")) {
            executorService.execute(() -> {
                //TODO: Add Dialogflow integration [Make chatbot smart]
                currentUsers.add(message.getSender());
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

    public synchronized int getNumberOfUsersInChat() {
        return this.currentUsers.size();
    }
    public void handleRemoveUser(ChatMessage message) {
        logger.debug("handleRemoveUser");
    }

}
