'use strict';

import {propertyFilter} from "../state.js";
import {ChatController} from "../main.js";

export const FilterElement = (() => {

    const filterElementID = Date.now().toString();
    let hidden = false;
    const priceRange = [...Array(20).keys()].map(i => (i*500).toString());
    const roomsRange = [...Array(8).keys()].map(i => i.toString()).slice(1);

    const createFilterBox = (messageContainer) => {
        const filterContainer = document.createElement("div");
        filterContainer.id = filterElementID;
        filterContainer.classList.add("filterContainer");
        const plzLabel = document.createElement("label");
        plzLabel.htmlFor = "plz";
        plzLabel.innerHTML = "PLZ";
        const plzInput = document.createElement("input");
        plzInput.classList.add("plz");
        plzInput.classList.add("location-input");
        plzInput.setAttribute("placeholder", "PLZ, City, Region");

        const [priceMinLabel, priceMinSelectionContainer, priceMinValue] =
            createFilterSelection("priceMin", "Min. Price", priceRange);
        const [priceMaxLabel, priceMaxSelectionContainer, priceMaxValue] =
            createFilterSelection("priceMax", "Max. Price", priceRange);

        const [roomsMinLabel, roomsMinSelectionContainer, roomsMinValue] =
            createFilterSelection("roomsMin", "Min. Rooms", roomsRange);
        const [roomsMaxLabel, roomsMaxSelectionContainer, roomsMaxValue] =
            createFilterSelection("roomsMax", "Max. Rooms", roomsRange);

        const submitButton = document.createElement("button");
        submitButton.classList.add("filterSubmitButton");
        submitButton.type = "submit";
        submitButton.innerHTML = "Search!";

        submitButton.onclick = () => {
            if(propertyFilter.setFilter(plzInput.value, priceMinValue.value,
                priceMaxValue.value, roomsMinValue.value, roomsMaxValue.value)) {
                console.log("got herre")
                ChatController.sendPropertyInfoMessage();
            }
        };

        filterContainer.append(plzLabel, plzInput, priceMinLabel, priceMinSelectionContainer,
            priceMaxLabel, priceMaxSelectionContainer, roomsMinLabel, roomsMinSelectionContainer,
            roomsMaxLabel, roomsMaxSelectionContainer, submitButton);

        messageContainer.appendChild(filterContainer);
        hidden = false;
    };

    const hide = () => {
        if(!hidden) {
            document.getElementById(filterElementID).style.display = "none";
            hidden = true;
        } else {
            document.getElementById(filterElementID).style.display = "";
            hidden = false;
        }
    };

    const existsAlready = () => document.getElementById(filterElementID) != null;

    return {
        createFilterBox: createFilterBox,
        existsAlready: () => existsAlready(),
        hide: () => hide()
    }

})();

const createFilterSelection = (labelForName, labelInnerHtml, valueRange) => {
    const label = document.createElement("label");
    label.htmlFor = labelForName;
    label.innerHTML = labelInnerHtml;
    const selectionContainer = document.createElement('div');
    selectionContainer.classList.add('pure-css-select-style');
    selectionContainer.classList.add('theme-default');
    selectionContainer.classList.add(labelForName + '-container')
    const selection = document.createElement("select");
    selection.classList.add(labelForName)
    valueRange.map(i => {
        const option = document.createElement("option");
        option.value = i;
        option.text = i;
        selection.appendChild(option);
    });
    selectionContainer.appendChild(selection)
    return [label, selectionContainer, selection];
};