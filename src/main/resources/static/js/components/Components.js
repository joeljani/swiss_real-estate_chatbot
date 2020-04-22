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

export function createChatbotSuggestionsElement(message, sendPropertyInfoMessage) {
    const messageContainer = document.createElement('div');
    const suggestionsText = document.createTextNode('Some suggestions:');
    const textElement = document.createElement('p');
    textElement.appendChild(suggestionsText)
    messageContainer.appendChild(textElement)
    const messages = message.split(",");
    messages.forEach(m => {
        const button = document.createElement("button");
        button.classList.add("suggestionActionButton");
        button.onclick = () => suggestionActionButtonListener(messageContainer, m, sendPropertyInfoMessage);
        button.innerHTML = m;
        messageContainer.appendChild(button)
    });
    return messageContainer;
}

export function suggestionActionButtonListener(messageContainer, message, sendPropertyInfoMessage) {
    if(message.includes("rent")) {
        createFilterBox(messageContainer, sendPropertyInfoMessage);
    } else if(message.includes("buy")) {
        console.log(propertyFilter.getAllValues())
    }
}

export function createFilterBox(messageContainer, sendPropertyInfoMessage) {
    const filterContainer = document.createElement("div");
    filterContainer.classList.add("filterContainer")
    const plzLabel = document.createElement("label");
    plzLabel.htmlFor = "plz";
    plzLabel.innerHTML = "PLZ";
    const plzInput = document.createElement("input");


    const priceRange = [...Array(20).keys()].map(i => (i*500).toString());
    const priceMinlabel = document.createElement("label");
    priceMinlabel.htmlFor = "priceMin";
    priceMinlabel.innerHTML = "Price Min";
    const priceMinInput = document.createElement("select");
    priceRange.map(i => {
        const option = document.createElement("option");
        option.value = i;
        option.text = i;
        priceMinInput.appendChild(option);
    });

    const priceMaxLabel = document.createElement("label");
    priceMaxLabel.htmlFor = "priceMax";
    priceMaxLabel.innerHTML = "Price Max";
    const priceMaxInput = document.createElement("select");
    priceRange.map(i => {
        const option = document.createElement("option");
        option.value = i;
        option.text = i;
        priceMaxInput.appendChild(option);
    });

    const roomsRange = [...Array(8).keys()].map(i => i.toString()).slice(1);
    const roomsMinLabel = document.createElement("label");
    roomsMinLabel.htmlFor = "roomsMin";
    roomsMinLabel.innerHTML = "Rooms Min";
    const roomsMinInput = document.createElement("select");
    roomsRange.map(i => {
        const option = document.createElement("option");
        option.value = i;
        option.text = i;
        roomsMinInput.appendChild(option);
    });

    const roomsMaxLabel = document.createElement("label");
    roomsMaxLabel.htmlFor = "roomsMax";
    roomsMaxLabel.innerHTML = "Rooms Max";
    const roomsMaxInput = document.createElement("select");
    roomsRange.map(i => {
        const option = document.createElement("option");
        option.value = i;
        option.text = i;
        roomsMaxInput.appendChild(option);
    });

    const submitButton = document.createElement("button");
    submitButton.classList.add("filterSubmitButton");
    submitButton.type = "submit";
    submitButton.innerHTML = "Search!";
    submitButton.onclick = () => {
        propertyFilter.setFilter(plzInput.value, priceMinInput.value, priceMaxInput.value, roomsMinInput.value,
                                 roomsMaxInput.value);
        sendPropertyInfoMessage();
    };


    filterContainer.append(plzLabel, plzInput, priceMinlabel, priceMinInput,
        priceMaxLabel, priceMaxInput, roomsMinLabel, roomsMinInput, roomsMaxLabel, roomsMaxInput, submitButton);

    messageContainer.appendChild(filterContainer);
}

export function createInfoChatMessage(message, messageArea, chatbotAvatar, sendPropertyInfoMessage) {
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
    messageElement.appendChild(createChatbotSuggestionsElement(message.content, sendPropertyInfoMessage));

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