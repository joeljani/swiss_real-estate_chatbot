package ch.propbuddy.propbuddy.controller;


import ch.propbuddy.propbuddy.domain.ChatMessage;
import ch.propbuddy.propbuddy.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;


@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        logger.debug("sendMessage called, chatMessage: {}", chatMessage);
        chatService.handleTextMessage(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        logger.debug("addUser: {}", chatMessage);
        chatService.handleAddUser(chatMessage);
        return chatMessage;
    }

    @MessageMapping("/chat.removeUser")
    @SendTo("/topic/public")
    public ChatMessage removeUser(@Payload ChatMessage chatMessage) {
        logger.debug("removeUser: {}", chatMessage);
        chatService.handleRemoveUser(chatMessage);
        return chatMessage;
    }
}