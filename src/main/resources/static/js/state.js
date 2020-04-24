'use strict';


// Observable
const propertyFilter = (() => {

    const listeners = [];

    let PLZ = "";
    let priceMin = "";
    let priceMax = "";
    let roomsMin = "";
    let roomsMax = "";

    const getAllFilterValues = () => [PLZ, priceMin, priceMax, roomsMin, roomsMax];
    const notifyListeners = () => listeners.forEach(element => {
        if(element instanceof HTMLElement) element.innerHTML = getAllFilterValues().join(',')
    });

    const isValidFilter = (plz, priceMin, priceMax, roomsMin, roomsMax) => {
        let errorCount = 0;
        if(plz === "") {
            const locationInput = document.getElementsByClassName('location-input')[0];
            locationInput.classList.add('red-placeHolder');
            locationInput.placeholder = "Must not be empty";
            errorCount++;
        }
        if(parseInt(priceMin) > parseInt(priceMax)) {
            document.getElementsByClassName('priceMin-container')[0].style.border = '1px solid red';
            errorCount++;
        }
        if(parseInt(roomsMin) > parseInt(roomsMax)) {
            document.getElementsByClassName('roomsMin-container')[0].style.border = '1px solid red';
            errorCount++;
        }
        if(errorCount !== 0) createFilterErrorMessage();
        if(errorCount === 0) removeErrorValidation();

        return errorCount === 0;
    };

    const createFilterErrorMessage = () => {
        if(document.getElementById('filterErrorMessage') === null) {
            const errorField = document.createElement('span');
            errorField.id = "filterErrorMessage";
            errorField.style.fontSize = '10px';
            errorField.innerText = "Filter is not set correctly";
            errorField.classList.add('filterErrorMessageField');
            document.getElementsByClassName('filterContainer')[0].appendChild(errorField) //TODO: Work with id instead of filterContainer
        }
    };

    const removeErrorValidation = () => {
        removeFilterErrorMessage();
        const locationInput = document.getElementsByClassName('location-input')[0];
        locationInput.placeholder = "";
        locationInput.classList.remove('red-placeHolder');
        document.getElementsByClassName('priceMin-container')[0].style.border = '';
        document.getElementsByClassName('roomsMin-container')[0].style.border = '';
    };

    const removeFilterErrorMessage = () => {
        const filterErrorMessage = document.getElementById('filterErrorMessage');
        if(filterErrorMessage !== null) filterErrorMessage.remove()
    };


    const setFilter = (newPlz, newPriceMin, newPriceMax, newRoomsMin, newRoomsMax) => {
        if(isValidFilter(newPlz, newPriceMin, newPriceMax, newRoomsMin, newRoomsMax)) {
            PLZ      = newPlz;
            priceMin = newPriceMin;
            priceMax = newPriceMax;
            roomsMin = newRoomsMin;
            roomsMax = newRoomsMax;
            notifyListeners();
            return true;
        } else return false;
    };

    return {
        setFilter: (newPlz, newPriceMin, newPriceMax, newRoomsMin, newRoomsMax) =>
            setFilter(newPlz, newPriceMin, newPriceMax, newRoomsMin, newRoomsMax),
        getAllValues: () => getAllFilterValues(),
        addListener: element => listeners.push(element),
        removeListener: element => listeners.filter(e => e !== element) // Prevent memory leak
    }
})();

export {propertyFilter};