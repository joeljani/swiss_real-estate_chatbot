package ch.propbuddy.propbuddy.domain;

public class Filter {
    private final String location;
    private final String priceMin;
    private final String priceMax;
    private final String roomsMin;
    private final String roomsMax;

    public Filter(String location, String priceMin, String priceMax, String roomsMin, String roomsMax) {
        this.location = location;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.roomsMin = roomsMin;
        this.roomsMax = roomsMax;
    }

    public String getLocation() {
        return location;
    }

    public String getPriceMin() {
        return priceMin;
    }

    public String getPriceMax() {
        return priceMax;
    }

    public String getRoomsMin() {
        return roomsMin;
    }

    public String getRoomsMax() {
        return roomsMax;
    }

    @Override
    public String toString() {
        return location + "," + priceMin + "," + priceMax + "," + roomsMin + "," + roomsMax;
    }
}
