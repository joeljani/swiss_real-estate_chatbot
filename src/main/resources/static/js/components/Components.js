import {propertyFilter} from "../state.js";

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

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
    const messageText = document.createTextNode("Lueged t wohnige a playboys");
    const pdfLink = document.createElement('a');
    pdfLink.appendChild(messageText);
    pdfLink.setAttribute('href', 'http://localhost:3880/'+message.content);
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
    textElement.appendChild(suggestionsText)
    messageContainer.appendChild(textElement)
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

export function suggestionActionButtonListener(messageContainer, message) {
    if(message.includes("rent")) {
        createFilterBox(messageContainer);
    } else if(message.includes("buy")) {
        console.log(propertyFilter.getAllValues())
    }
}

export function createFilterBox(messageContainer) {
    const filterContainer = document.createElement("div");
    filterContainer.classList.add("filterContainer")
    const plzLabel = document.createElement("label");
    plzLabel.htmlFor = "plz";
    plzLabel.innerHTML = "PLZ";
    const plzInput = document.createElement("input");

    const priceMinlabel = document.createElement("label");
    priceMinlabel.htmlFor = "priceMin";
    priceMinlabel.innerHTML = "Price Min";
    const priceMinInput = document.createElement("input");

    const priceMaxLabel = document.createElement("label");
    priceMaxLabel.htmlFor = "priceMax";
    priceMaxLabel.innerHTML = "Price Max";
    const priceMaxInput = document.createElement("input");

    const roomsMinLabel = document.createElement("label");
    roomsMinLabel.htmlFor = "roomsMin";
    roomsMinLabel.innerHTML = "Rooms Min";
    const roomsMinInput = document.createElement("input");

    const roomsMaxLabel = document.createElement("label");
    roomsMaxLabel.htmlFor = "roomsMax";
    roomsMaxLabel.innerHTML = "Rooms Max";
    const roomsMaxInput = document.createElement("input");

    const submitButton = document.createElement("button");
    submitButton.classList.add("filterSubmitButton")
    submitButton.type = "submit";
    submitButton.innerHTML = "Search!";
    //submitButton.onclick = () => ChatController.sendPropertyInfoMessage();

    plzInput.oninput = event => propertyFilter.setPlz(event.target.value);
    priceMinInput.oninput = event => propertyFilter.setPriceMin(event.target.value);
    priceMaxInput.oninput = event => propertyFilter.setPriceMax(event.target.value);
    roomsMinInput.oninput = event => propertyFilter.setRoomsMin(event.target.value);
    roomsMaxInput.oninput = event => propertyFilter.setRoomsMax(event.target.value);

    filterContainer.append(plzLabel, plzInput, priceMinlabel, priceMinInput,
        priceMaxLabel, priceMaxInput, roomsMinLabel, roomsMinInput, roomsMaxLabel, roomsMaxInput, submitButton);

    messageContainer.appendChild(filterContainer);
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