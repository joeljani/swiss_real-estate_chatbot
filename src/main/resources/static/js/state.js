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

    return {
        getPLZ:      () => PLZ,
        getpriceMin: () => priceMin,
        getPriceMax: () => priceMax,
        getRoomsMin: () => roomsMin,
        getRoomsMax: () => roomsMax,
        setFilter: (newPlz, newPriceMin, newPriceMax, newRoomsMin, newRoomsMax) => {
            PLZ      = newPlz;
            priceMin = newPriceMin;
            priceMax = newPriceMax;
            roomsMin = newRoomsMin;
            roomsMax = newRoomsMax;
            notifyListeners();
        },
        getAllValues: () => getAllFilterValues(),
        addListener: element => listeners.push(element),
        removeListener: element => listeners.filter(e => e !== element) // Prevent memory leak
    }
})();

export {propertyFilter};