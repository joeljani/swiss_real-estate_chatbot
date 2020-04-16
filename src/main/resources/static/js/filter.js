'use strict';


const filter = (() => {
    let PLZ = "";
    let priceMin = "";
    let priceMax = "";
    let roomsMin = "";
    let roomsMax = "";

    return {
        getPLZ:      () => PLZ,
        getpriceMin: () => priceMin,
        getPriceMax: () => priceMax,
        getRoomsMin: () => roomsMin,
        getRoomsMax: () => roomsMax,
        setPlz:      newPlz      => PLZ      = newPlz,
        setPriceMin: newPriceMin => priceMin = newPriceMin,
        setPriceMax: newPriceMax => priceMax = newPriceMax,
        setRoomsMin:  newRoomsMin => roomsMin = newRoomsMin,
        setRoomsMax: newRoomsMax => roomsMax = newRoomsMax,
        getAllValues: () => [PLZ, priceMin, priceMax, roomsMin, roomsMax],
    }
})();

export {filter};