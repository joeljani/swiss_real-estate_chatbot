package ch.propbuddy.propbuddy.domain;

public class Property {
    private final String plz;
    private final String address;
    private final String areaSqm;
    private final String rooms;
    private final String price;
    private final String link;

    public Property(String plz, String address, String areaSqm, String rooms, String price, String link) {
        this.plz = plz;
        this.address = address;
        this.areaSqm = areaSqm;
        this.rooms = rooms;
        this.price = price;
        this.link = link;
    }

    public String getPlz() {
        return plz;
    }

    public String getAddress() {
        return address;
    }

    public String getAreaSqm() {
        return areaSqm;
    }

    public String getRooms() {
        return rooms;
    }

    public String getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return plz+","+address+","+areaSqm+","+rooms+","+price+","+link;
    }
}
