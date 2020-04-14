package ch.propbuddy.propbuddy.domain;

public class Property {
    final String plz;
    final String address;
    final String areaSqm;
    final String rooms;
    final String price;
    final String link;

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
}
