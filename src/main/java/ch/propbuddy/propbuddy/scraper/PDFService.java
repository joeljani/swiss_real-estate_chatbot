package ch.propbuddy.propbuddy.scraper;

import ch.propbuddy.propbuddy.domain.Property;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class PDFService {

    @Value("${PDF_FolderPath}")
    private String PDF_FolderPath;

    public PDFService() {
    }

    /**
     * Creates a PDF File with the given properties.
     * @param properties
     * @return UniqueID which is the name of the PDF File
     * @throws IOException
     */
    public String createPDF(List<Property> properties) throws IOException {

        com.itextpdf.text.Document pdfDocument = new com.itextpdf.text.Document();

        String uniqueID = UUID.randomUUID().toString();
        try
        {
            PdfWriter writer = PdfWriter.getInstance(pdfDocument,
                    new FileOutputStream(PDF_FolderPath + uniqueID + ".pdf"));
            pdfDocument.open();
            com.itextpdf.text.List list = new com.itextpdf.text.List();
            list.setSymbolIndent(12);
            list.setListSymbol("\u2022");
            properties.forEach(p -> {
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
        Chunk link = new Chunk("Go to website", FontFactory.getFont(FontFactory.COURIER, 20, Font.ITALIC, new BaseColor(0, 0,
                255)));
        link.setUnderline(1, 1);
        link.setAnchor(p.getLink());
        item.add(link);
        return item;
    }
}
