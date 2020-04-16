import {filter} from './filter.js'
import {component} from "./component.js";

'use strict';


const usernamePage  = component('username-page');
const chatPage = component('chat-page');
const usernameForm = component('usernameForm', 'submit', connect, true);
const messageForm = component('messageForm', 'submit', sendMessage, true);
const messageInput = component('message');
const messageArea = component('messageArea');
const connectingElement = document.querySelector('.connecting');
const leaveButton = component('leave-button', 'click', leaveChatOnLeaveButtonClicked);
const sendMessageButton = component('sendMessageButton', 'click', sendMessage);
const sendToChatbotButton = component('sendToChatbotButton', 'click', sendMessageToChatbot);


var stompClient = null;
var username = null;


const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

/****************** If app is used on a small screen <= 20em (320px) ******************/

if (window.innerWidth * 0.03125 <= 20) {
    console.log("small screen used");
    leaveButton.textContent = "X";
    leaveButton.style.backgroundColor = "#ffffff";
    leaveButton.style.color = "red";
    leaveButton.style.width = "30px";
    leaveButton.style.whiteSpace = "normal";
    leaveButton.style.fontSize = "16px";
    leaveButton.style.padding = "0px";
    leaveButton.style.boxShadow = "0 0 0 0 rgba(0, 0, 0, 0)";
}

/***********************************************************************************.*/


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
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);
    // Tell your username to the server
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


var checkUserNameOnFirstJoin = true;

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);

    if (checkUserNameOnFirstJoin) {
        username = message.sender;
        checkUserNameOnFirstJoin = false;
    }

    if (message.type === 'JOIN') {
        message.content = message.sender + ' has joined the chat!';
        createLeaveOrJoinedMessage(message)
    } else if (message.type === 'LEAVE') {
        message.content = message.sender + ' has left the chat!';
        createLeaveOrJoinedMessage(message)
    } else if (message.type === 'CHAT') {
        if (message.sender === 'Chatbot') {
            createChatMessage(message, createChatBotAvatar());
        } else {
            createChatMessage(message, null);
        }
    } else if (message.type === 'INFO') {
        createInfoChatMessage(message, createChatBotAvatar());
    }
}

function createLeaveOrJoinedMessage(message) {
    const messageElement = document.createElement('li');
    messageElement.classList.add('event-message');

    const messageText = document.createTextNode(message.content);
    const textElement = document.createElement('p');

    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function createChatMessage(message, chatbotAvatar) {
    let messageElement;
    if (chatbotAvatar != null) {
        messageElement = chatbotAvatar;
    } else {
        messageElement = document.createElement('li');
        messageElement.classList.add('chat-message');
        const avatarElement = document.createElement('i');
        const avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);
        messageElement.appendChild(avatarElement);
    }

    messageElement.appendChild(createUserNameText(message.sender));
    messageElement.appendChild(createUserMessageElement(message.content));

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function createUserNameText(username) {
    const usernameElement = document.createElement('span');
    const usernameText = document.createTextNode(username);
    usernameElement.appendChild(usernameText);
    return usernameElement;
}

function createUserMessageElement(message) {
    const messageText = document.createTextNode(message);
    const textElement = document.createElement('p');
    textElement.appendChild(messageText);
    return textElement;
}

function createChatbotSuggestionsElement(message) {
    const messageContainer = document.createElement('div');
    const messages = message.split(",");
    messages.forEach(m => {
            const button = document.createElement("button");
            button.classList.add("suggestionActionButton");
            button.onclick = () => suggestionActionButtonListener(messageContainer, m);
            button.innerHTML = m;
            messageContainer.appendChild(button)
    })
    return messageContainer;
}

function suggestionActionButtonListener(messageContainer, message) {
    if(message.includes("rent")) {
        createFilterBox(messageContainer);
    } else if(message.includes("buy")) {
        console.log(filter.getAllValues())
    }
}

function createFilterBox(messageContainer) {
    const filterContainer = document.createElement("div");
    const plzLabel = document.createElement("label");
    plzLabel.htmlFor = "plz";
    plzLabel.innerHTML = "Ort";

    const plzInput = document.createElement("input");
    plzInput.name = "plz";

    const priceMinlabel = document.createElement("label");
    const priceMinInput = document.createElement("input");

    const priceMaxLabel = document.createElement("label");
    const priceMaxInput = document.createElement("input");

    const roomsMinLabel = document.createElement("label");
    const roomsMinInput = document.createElement("input");

    const roomsMaxLabel = document.createElement("label");
    const roomsMaxInput = document.createElement("input");

    const submitButton = document.createElement("button");
    submitButton.type = "submit";
    submitButton.innerHTML = "Search!";
    submitButton.onclick = () => sendPropertyInfoMessage();

    plzInput.oninput = event => onInputListenerPLZ(event);
    priceMinInput.oninput = event => onInputListenerPriceMin(event);
    priceMaxInput.oninput = event => onInputListenerPriceMax(event);
    roomsMinInput.oninput = event => onInputListenerRoomsMin(event);
    roomsMaxInput.oninput = event => onInputListenerRoomsMax(event);

    filterContainer.append(plzLabel, plzInput, priceMinlabel, priceMinInput,
        priceMaxLabel, priceMaxInput, roomsMinLabel, roomsMinInput, roomsMaxLabel, roomsMaxInput, submitButton);



    //STYLING
    for(let i = 0; i < filterContainer.childElementCount; i++) {
        filterContainer.children[i].classList.add("filterInput");
    }


    messageContainer.appendChild(filterContainer)
}

function onInputListenerPLZ(event) {
    filter.setPlz(event.target.value);
}
function onInputListenerPriceMin(event) {
    filter.setPriceMin(event.target.value);
}
function onInputListenerPriceMax(event) {
    filter.setPriceMax(event.target.value);
}
function onInputListenerRoomsMin(event) {
    filter.setRoomsMin(event.target.value);
}
function onInputListenerRoomsMax(event) {
    filter.setRoomsMax(event.target.value);
}

function sendPropertyInfoMessage() {
    const messageContent = filter.getPLZ() + "," +
                           filter.getpriceMin() + "," +
                           filter.getPriceMax() + "," +
                           filter.getRoomsMin() + "," +
                           filter.getRoomsMax();
    const chatMessage = {
        sender: username,
        content: messageContent,
        type: 'CHAT'
    };
    stompClient.send("/app/chat.sendMessageToChatBot", {}, JSON.stringify(chatMessage));
}

function createInfoChatMessage(message, chatbotAvatar) {
    let messageElement;
    if (chatbotAvatar != null) {
        messageElement = chatbotAvatar;
    } else {
        messageElement = document.createElement('li');
        messageElement.classList.add('chat-message');
        const avatarElement = document.createElement('i');
        const avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);
        messageElement.appendChild(avatarElement);
    }

    messageElement.appendChild(createUserNameText(message.sender));
    messageElement.appendChild(createChatbotSuggestionsElement(message.content));

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function createChatBotAvatar() {
    let messageElement = document.createElement('li');
    messageElement.classList.add('chat-message');

    let avatarcontainer = document.createElement('i');
    let avatarElement = document.createElement('img');
    avatarElement.setAttribute('src', 'https://icon-library.net/images/chatbot-icon/chatbot-icon-8.jpg');
    avatarElement.setAttribute('alt', 'na');
    avatarElement.setAttribute('height', '48px');
    avatarElement.setAttribute('width', '48px');
    avatarcontainer.appendChild(avatarElement)
    messageElement.appendChild(avatarcontainer);

    return messageElement;
}

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    let index = Math.abs(hash % colors.length);
    return colors[index];
}

var isCalledFromLeaveChatButton = false; // needed because location.reload()

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

window.addEventListener('beforeunload', leaveChatOnWindowClosed);