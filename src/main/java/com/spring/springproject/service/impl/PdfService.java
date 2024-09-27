package com.spring.springproject.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.spring.springproject.entities.Order;
import com.spring.springproject.entities.OrderTechnique;
import com.spring.springproject.entities.Technique;
import org.dom4j.DocumentException;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PdfService {
    public static ByteArrayOutputStream generatePdf(Order order) throws DocumentException {
        if (order == null) {
            return null;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();


            // Здесь можно добавить информацию о заказе в PDF
            document.add(new Paragraph("Order ID: " + order.getId()));
            document.add(new Paragraph("User: " + order.getUser().getUserName()));
            document.add(new Paragraph("Date of order: " + order.getOrderDate().getYear() + "-" +
                    order.getOrderDate().getMonth() + "-"
                    + order.getOrderDate().getDay()));
            document.add(new Paragraph("Price: " + order.getAmount()));
            document.add(new Paragraph("Status: " + order.getStatus()));
            document.add(new Paragraph("Count of products: " + order.getOrderTechniques().size()));
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(6); // 6 столбцов: producer,country, model, category,  count, price
            addTableHeader(table);
            addProductsToTable(table, order.getOrderTechniques().stream().toList());
            document.add(table);

            // Добавьте другие необходимые поля о заказе и продуктах

            document.close();
        } catch (com.itextpdf.text.DocumentException e) {
            e.printStackTrace();
        }

        return outputStream;
    }

    private static void addTableHeader(PdfPTable table) {
        table.addCell("Category");
        table.addCell("Model");
        table.addCell("Producer");
        table.addCell("Country");
        table.addCell("Count");
        table.addCell("Price");
    }

    private static void addProductsToTable(PdfPTable table, List<OrderTechnique> techniques) {
        for (OrderTechnique technique : techniques) {
            Technique techniqueInfo = technique.getTechnique();
            table.addCell(techniqueInfo.getCategory().getName()==null?"":techniqueInfo.getCategory().getName());
            table.addCell(techniqueInfo.getModel().getName()==null?"":techniqueInfo.getModel().getName());
            table.addCell(techniqueInfo.getProducer().getName()==null?"":techniqueInfo.getProducer().getName());
            table.addCell(techniqueInfo.getProducer().getCountry()==null?"":techniqueInfo.getProducer().getCountry());
            table.addCell(String.valueOf(technique.getQuantity()));
            table.addCell(techniqueInfo.getPrice().toString());
        }
    }
}
