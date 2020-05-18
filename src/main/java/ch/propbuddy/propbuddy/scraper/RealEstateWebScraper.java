package ch.propbuddy.propbuddy.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import ch.propbuddy.propbuddy.domain.Property;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class RealEstateWebScraper {

    private volatile Document doc;

    final String WEBSITE_URL = "https://www.alle-immobilien.ch/de/mieten/in-";

    public synchronized List<Property> fetchProperties(List<String> propertyFilter)
            throws IOException, IndexOutOfBoundsException, InterruptedException {
        // Reset Document for each new fetch
        doc = null;

        // Initial fetch of website
        String URL = buildURL(propertyFilter);
        System.out.println(URL);
        doc = Jsoup.connect(URL).get();

        int numberOfPages = doc.getElementsByClass("pagination").get(0).childrenSize()-1;
        CountDownLatch doneFetchigSignal = new CountDownLatch(numberOfPages-1); // Skip first page

        // Loop through pagination of website, start from second page
        for (int i = 2; i <= numberOfPages; i++) {
            new JsoupDocumentFetcher(URL+"?pageNum="+i, doneFetchigSignal).run();
        }

        // Wait till every page of search results got fetched
        doneFetchigSignal.await();
        System.out.println(doc);
        String plz = propertyFilter.get(0);
        return buildProperties(plz, doc, numberOfPages);
    }


    private List<Property> buildProperties(String plz, Document doc, int numberOfPages) {
        //Display amount of properties found
        String propertiesFoundText = doc.getElementsByClass("search-results__subheadline").text();
        int numPropertiesFound = Integer.parseInt(propertiesFoundText.replaceAll("\\D+",""));
        System.out.println("num props found: " + numPropertiesFound);

        //All Links for every Property
        Element searchResultList = doc.getElementsByClass("search-results__list").get(0);
        for (int i = 1; i < numberOfPages; i++) {
            searchResultList.appendChild(doc.getElementsByClass("search-results__list").get(i));
        }

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

        System.out.println(properties.size());

        return properties;
    }


    public List<Property> getNewProperties(List<Property> oldList, List<Property> newList) {
        List<Property> newProperties = new ArrayList<>();
        for(int i = 0; i < oldList.size(); i++) {
            if (newList.get(i) != null) {
                Property p1 = oldList.get(i);
                Property p2 = newList.get(i);
                if(!p1.getPlz().equals(p2.getPlz())
                        && !p1.getPrice().equals(p2.getPrice())
                        && !p1.getAddress().equals(p2.getAddress())
                        && !p1.getAreaSqm().equals(p2.getAreaSqm())
                        && !p1.getRooms().equals(p2.getRooms())
                        && !p1.getLink().equals(p2.getLink())) {
                    newProperties.add(p2);
                }
            }
        }
        return newProperties;
    }

    private String buildURL(List<String> propertyFilter) {
        String plz = propertyFilter.get(0) + ".0";
        String priceFrom = propertyFilter.get(1) + ".0";
        String priceTo = propertyFilter.get(2) + ".0";
        String roomsFrom = propertyFilter.get(3) + ".0";
        String roomsTo = propertyFilter.get(4) + ".0";
        return WEBSITE_URL+plz+"/preis-"+priceFrom+"-"+priceTo+"/zimmer-"+roomsFrom+"-"+roomsTo+"/";
    }

    private final class JsoupDocumentFetcher implements Runnable {
        private final String URL;
        private CountDownLatch doneFetchigSignal;

        public JsoupDocumentFetcher(String url, CountDownLatch doneFetchigSignal) {
            this.URL = url;
            this.doneFetchigSignal = doneFetchigSignal;
        }

        @Override
        public void run() {
            try {
                appendNextPageDocument();
            } catch (IOException e) {
                e.printStackTrace();
            }
            doneFetchigSignal.countDown();
        }

        /**
         * Appends a new Document, which is the next page of the search results, to the parent document.
         * @throws IOException
         */
        private void appendNextPageDocument() throws IOException {
            Document nextPageDocument = Jsoup.connect(URL).get();
            if (nextPageDocument.getElementsByClass("search-results__subheadline").text().equals("")) {
            } else {
                System.out.println("appending");
                doc.appendChild(nextPageDocument);
            }
        }
    }
}