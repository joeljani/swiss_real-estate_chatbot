'use strict';

import {propertyFilter} from './state.js'
import {
    createChatBotAvatar,
    createChatMessage,
    createInfoChatMessage,
    createLeaveOrJoinedMessage,
    createPDFLink
} from "./components/Components.js";


/**
 * Handles websocket (SockJS) messages
 */
export const ChatController = (() => {

    let stompClient = null;
    let username = null;
    let checkUserNameOnFirstJoin = true;
    let isCalledFromLeaveChatButton = false; // needed because location.reload()

    function connect(event) {
        username = document.getElementById('name').value.trim();
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        const socket = new SockJS('/propbuddy');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);
        event.preventDefault();
    }

    function onConnected() {
        stompClient.subscribe('/topic/public', onMessageReceived);
        stompClient.send("/app/chat.addUser", {},
            JSON.stringify({sender: username, type: 'JOIN'})
        );
        connectingElement.classList.add('hidden');
    }

    function onError(error) {
        connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
        connectingElement.style.color = 'red';
    }

    function sendMessage() {
        const messageContent = messageInput.value.trim();
        const chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';

    }

    function sendMessageToChatbot() {
        const messageContent = messageInput.value.trim();
        const chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessageToChatBot", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }

    function onMessageReceived(payload) {
        const message = JSON.parse(payload.body);
        console.log(message)

        if (checkUserNameOnFirstJoin) {
            username = message.sender;
            checkUserNameOnFirstJoin = false;
        }

        switch (message.type) {
            case 'JOIN':
                message.content = message.sender + ' has joined the chat!';
                createLeaveOrJoinedMessage(message, messageArea);
                break;
            case 'LEAVE':
                message.content = message.sender + ' has left the chat!';
                createLeaveOrJoinedMessage(message, messageArea);
                break;
            case 'CHAT':
                if (message.sender === 'Chatbot') createChatMessage(message, messageArea, createChatBotAvatar());
                else createChatMessage(message, messageArea,null);
                break;
            case 'INFO':
                createInfoChatMessage(message, messageArea, createChatBotAvatar(), sendPropertyInfoMessage);
                break;
            case 'PDF':
                createPDFLink(message, messageArea, createChatBotAvatar());
                break;
            case 'FILTER_CHANGED':
                setCurrentFilter(message.content);
                break;
            case 'PDF_PROPS_UPDATED':
                createPDFLink(message, messageArea, createChatBotAvatar());
                break;
        }
    }

    function setCurrentFilter(messageContent) {
        const list = messageContent.split(',');
        propertyFilter.setFilter(list[0], list[1], list[2], list[3], list[4]);
    }

    function sendPropertyInfoMessage() {
        const messageContent = propertyFilter.getAllValues().join(',')
        const chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendPropertyInfoToChatBot", {}, JSON.stringify(chatMessage));
    }

    function leaveChatOnLeaveButtonClicked() {
        const chatMessage = {
            sender: username,
            content: "",
            type: 'LEAVE'
        };
        stompClient.send("/app/chat.removeUser", {}, JSON.stringify(chatMessage));
        isCalledFromLeaveChatButton = true;
        location.reload();
    }

    function leaveChatOnWindowClosed() {
        if (!isCalledFromLeaveChatButton) {
            const chatMessage = {
                sender: username,
                content: "",
                type: 'LEAVE'
            };
            stompClient.send("/app/chat.removeUser", {}, JSON.stringify(chatMessage));
        }
        isCalledFromLeaveChatButton = false;
    }

    return {
        connect: connect,
        onConnected: onConnected,
        onError: onError,
        sendMessage: sendMessage,
        sendMessageToChatbot: sendMessageToChatbot,
        onMessageReceived: onMessageReceived,
        sendPropertyInfoMessage: sendPropertyInfoMessage,
        leaveChatOnLeaveButtonClicked: leaveChatOnLeaveButtonClicked,
        leaveChatOnWindowClosed: leaveChatOnWindowClosed
    }
})();

/**
 * Binds existing html element to an event listener
 * @param id of html element
 * @param eventListenerType
 * @param eventListener
 * @param options of the eventlistener
 * @returns {HTMLElement}
 */
const bindEventListenerToElement = (id, eventListenerType, eventListener, options) => {
    const component = document.getElementById(id);
    if(eventListenerType && eventListener) {
        if(options) component.addEventListener(eventListenerType, eventListener, options);
        else component.addEventListener(eventListenerType, eventListener);
    }
    return component;
};

const usernamePage  = bindEventListenerToElement('username-page');
const chatPage = bindEventListenerToElement('chat-page');
const usernameForm = bindEventListenerToElement('usernameForm', 'submit', ChatController.connect, true);
const messageForm = bindEventListenerToElement('messageForm', 'submit', ChatController.sendMessage, true);
const messageInput = bindEventListenerToElement('message');
const messageArea = bindEventListenerToElement('messageArea');
const connectingElement = document.querySelector('.connecting');
const leaveButton = bindEventListenerToElement('leave-button', 'click', ChatController.leaveChatOnLeaveButtonClicked);
const sendMessageButton = bindEventListenerToElement('sendMessageButton', 'click', ChatController.sendMessage);
const sendToChatbotButton = bindEventListenerToElement('sendToChatbotButton', 'click', ChatController.sendMessageToChatbot);
const toggleInformationButton = bindEventListenerToElement('toggle-information', 'click', () => console.log(propertyFilter.getAllValues()))

window.addEventListener('beforeunload', ChatController.leaveChatOnWindowClosed);