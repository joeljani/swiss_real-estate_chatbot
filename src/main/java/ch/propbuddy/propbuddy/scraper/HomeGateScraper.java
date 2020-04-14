package ch.propbuddy.propbuddy.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HomeGateScraper {

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://www.alle-immobilien.ch/de/mieten/in-8002-zürich/preis--3500.0/zimmer-3.0-/").get();
        Elements elements = doc.getElementsByClass("search-results__subheadline");
        System.out.println(elements.text());

        Elements searchResults = doc.getElementsByClass("tag-holder");
        for (Element e : searchResults) {
            System.out.println("All text:" + e.text());
        }
        System.out.println(searchResults.size());
    }

}