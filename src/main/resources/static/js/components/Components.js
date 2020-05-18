'use strict';

import {propertyFilter} from "../state.js";
import {FilterElement} from "./FilterElement.js";

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

export function createFoundPropertiesMessage(message, messageArea, chatbotAvatar) {
    const messageContainer = document.createElement('div');
    const suggestionsText = document.createTextNode('Some suggestions:');
    const textElement = document.createElement('p');
    textElement.appendChild(suggestionsText);
    messageContainer.appendChild(textElement);

    const pdfLinkID = message.content.substring(message.content.indexOf("PDF:")+4);

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
    let messageText = "";
    if(message.type === 'PDF_PROPS_UPDATED') {
        messageText = document.createTextNode("New properties have been found!");
    } else {
        messageText = document.createTextNode("See the properties!");
    }
    const pdfLink = document.createElement('a');
    pdfLink.appendChild(messageText);
    pdfLink.setAttribute('href', 'http://localhost:3880/'+pdfLinkID); //PDF Server
    pdfLink.setAttribute("target", "_blank")
    messageElement.appendChild(pdfLink);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

export function createPDFLink(message, messageArea, chatbotAvatar) {
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
    let messageText = "";
    if(message.type === 'PDF_PROPS_UPDATED') {
        messageText = document.createTextNode("New properties have been found!");
    } else {
        messageText = document.createTextNode("See the properties!");
    }
    const pdfLink = document.createElement('a');
    pdfLink.appendChild(messageText);
    pdfLink.setAttribute('href', 'http://localhost:3880/'+message.content); //PDF Server
    pdfLink.setAttribute("target", "_blank")
    messageElement.appendChild(pdfLink);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

export function createLeaveOrJoinedMessage(message, messageArea) {
    const messageElement = document.createElement('li');
    messageElement.classList.add('event-message');

    const messageText = document.createTextNode(message.content);
    const textElement = document.createElement('p');

    textElement.appendChild(messageText);
    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

export function createChatMessage(message, messageArea, chatbotAvatar) {
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

export function createUserNameText(username) {
    const usernameElement = document.createElement('span');
    const usernameText = document.createTextNode(username);
    usernameElement.appendChild(usernameText);
    return usernameElement;
}

export function createUserMessageElement(message) {
    const messageText = document.createTextNode(message);
    const textElement = document.createElement('p');
    textElement.appendChild(messageText);
    return textElement;
}

export function createChatbotSuggestionsElement(message) {
    const messageContainer = document.createElement('div');
    const suggestionsText = document.createTextNode('Some suggestions:');
    const textElement = document.createElement('p');
    textElement.appendChild(suggestionsText);
    messageContainer.appendChild(textElement);
    const messages = message.split(",");
    messages.forEach(message => {
        const button = document.createElement("button");
        button.classList.add("suggestionActionButton");
        button.onclick = () => suggestionActionButtonListener(messageContainer, message);
        button.innerHTML = message;
        messageContainer.appendChild(button)
    });
    return messageContainer;
}

export function suggestionActionButtonListener(messageContainer, message) {
    if(message.includes("rent")) {
        if(!FilterElement.existsAlready()) FilterElement.createFilterBox(messageContainer);
        else FilterElement.hide();
    } else if(message.includes("buy")) {
        console.log(propertyFilter.getAllValues())
    }
}

export function createInfoChatMessage(message, messageArea, chatbotAvatar) {
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

export function createChatBotAvatar() {
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