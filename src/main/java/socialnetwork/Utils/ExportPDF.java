package socialnetwork.Utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class ExportPDF {
    private static String DEST = null;
    private static final PdfPTable table = new PdfPTable(3);

    public ExportPDF(String destination) {
        DEST = destination;
        table.addCell("TYPE");
        table.addCell("DESCRIPTION");
        table.addCell("TIMESTAMP");
    }

    public void addRowToTable(String type, String description, String timestamp) {
        table.addCell(type);
        table.addCell(description);
        table.addCell(timestamp);
    }

    public void exportTable() throws DocumentException, FileNotFoundException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(DEST));
        document.open();

        document.add(table);
        table.deleteBodyRows();
        document.close();
    }
}
