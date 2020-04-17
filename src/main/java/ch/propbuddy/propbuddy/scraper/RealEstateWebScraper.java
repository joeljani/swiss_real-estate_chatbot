package ch.propbuddy.propbuddy.scraper;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import ch.propbuddy.propbuddy.domain.Property;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class RealEstateWebScraper {
    final String WEBSITE_URL = "https://www.alle-immobilien.ch/de/mieten/in-";

    public List<Property> fetchProperties(String plz, String priceFrom, String priceTo, String roomsFrom, String roomsTo)
            throws IOException, IndexOutOfBoundsException {

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

        return properties;
    }

    public String createPDF(String plz, String priceFrom, String priceTo, String roomsFrom, String roomsTo) throws IOException {

        List<Property> props = fetchProperties(plz, priceFrom+".0", priceTo+".0", roomsFrom+".0", roomsTo+".0");
        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        String uniqueID = UUID.randomUUID().toString();
        try
        {
            PdfWriter writer = PdfWriter.getInstance(pdfDocument,
                    new FileOutputStream("/Users/joeljani/3.Personal_Projects/immobuddy/PDFServerService/public/PDF/"+uniqueID+".pdf"));
            pdfDocument.open();
            com.itextpdf.text.List list = new com.itextpdf.text.List();
            list.setSymbolIndent(12);
            list.setListSymbol("\u2022");
            props.forEach(p -> {
                try {
                    list.add(createSinglePropertyEntry(p));
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            });
            pdfDocument.add(list);
            pdfDocument.add(new Chunk(""));
            pdfDocument.close();
            writer.close();
            return uniqueID;
        } catch (DocumentException e)
        {
            e.printStackTrace();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private ListItem createSinglePropertyEntry(Property p) throws DocumentException {
        ListItem item = new ListItem();
        item.add(new Paragraph(p.getAddress()));
        item.add(new Paragraph(p.getPrice()));
        Chunk link = new Chunk("Lueg gnauer tiger", FontFactory.getFont(FontFactory.COURIER, 20, Font.ITALIC, new BaseColor(0, 0,
                255)));
        link.setUnderline(1, 1);
        link.setAnchor(p.getLink());
        item.add(link);
        return item;
    }
}