package ch.propbuddy.propbuddy.scraper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import ch.propbuddy.propbuddy.domain.Property;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class RealEstateWebScraper {
    final String WEBSITE_URL = "https://www.alle-immobilien.ch/de/mieten/in-";

    public List<String> fetchProperties(String plz, String priceFrom, String priceTo, String roomsFrom, String roomsTo )
            throws IOException {

        String URL = WEBSITE_URL+plz+"/preis-"+priceFrom+"-"+priceTo+"/zimmer-"+roomsFrom+"-"+roomsTo+"/";
        Document doc = Jsoup.connect(URL).get();

        //Display amount of properties found
        Elements propertiesFound = doc.getElementsByClass("search-results__subheadline");
        System.out.println(propertiesFound.text());

        //All Links for every Property
        Element searchResultList = doc.getElementsByClass("search-results__list").get(0);
        //Filter ads
        List<Element> propertyDIVS = searchResultList.getElementsByTag("a")
                                                   .stream()
                                                   .filter(p -> p.childNodeSize() > 1).collect(Collectors.toList());

        //Create Property objects
        List<Property> properties = propertyDIVS.stream().map(property -> {
            String link = property.attr("href");
            if(property.getElementsByClass("tag-holder").size() > 0) {
                String address = property.getElementsByClass("tag-holder").get(0).text();
                String areaSqm = property.getElementsByClass("tag-holder").get(1).text();
                String rooms = property.getElementsByClass("tag-holder").get(2).text();
                String price = property.getElementsByClass("tag-holder").get(3).text();
                return new Property(plz, address, areaSqm, rooms, price, link);
            } else return null;
        }).collect(Collectors.toList());

        return properties.stream().map(Property::getPrice).collect(Collectors.toList());
    }
}