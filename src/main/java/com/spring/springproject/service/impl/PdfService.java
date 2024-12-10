package com.spring.springproject.service.impl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.spring.springproject.dto.PaymentPdfDto;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public static final String PAYMENT_DIRECTORY = "D:/springproject/uploads/";
    private static final String FONT_PATH = "src/main/resources/fonts/times.ttf"; // Путь к файлу шрифта
    private static final Font TITLE_FONT = createFont(18, Font.BOLD);
    private static final Font HEADER_FONT = createFont(14, Font.BOLD);
    private static final Font NORMAL_FONT = createFont();
    private static final Font AMOUNT_FONT = createFont(14, Font.BOLD, BaseColor.BLUE);

    public String generatePdf(PaymentPdfDto paymentPdfDto) throws DocumentException {
        if (paymentPdfDto == null) {
            return null;
        }

        String link = PAYMENT_DIRECTORY + paymentPdfDto.getPaymentId() + ".pdf";
        File directory = new File(PAYMENT_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(link)) {
            Document pdfDocument = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter writer = PdfWriter.getInstance(pdfDocument, fileOutputStream);
            pdfDocument.open();

            // Заголовок
            Paragraph title = new Paragraph("КВИТАНЦИЯ ОБ ОПЛАТЕ", TITLE_FONT);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            pdfDocument.add(title);

            // Основная информация
            PdfPTable mainInfo = new PdfPTable(2);
            mainInfo.setWidthPercentage(100);
            mainInfo.setSpacingBefore(10f);
            mainInfo.setWidths(new float[]{1, 2}); // Ширина колонок

            // Добавляем строки с проверкой данных
            addTableRow(mainInfo, "Номер платежа:", safeString(paymentPdfDto.getPaymentId()));
            addTableRow(mainInfo, "Номер заявки:", safeString(paymentPdfDto.getBidId()));
            addTableRow(mainInfo, "Дата платежа:",
                    safeString(paymentPdfDto.getPaymentDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
            addTableRow(mainInfo, "Услуга:", safeString(paymentPdfDto.getServiceName()));
            addTableRow(mainInfo, "Плательщик:", safeString(paymentPdfDto.getCitizenName()));

            pdfDocument.add(mainInfo);

            // Сумма платежа
            Paragraph amountHeader = new Paragraph("Сумма к оплате", HEADER_FONT);
            amountHeader.setAlignment(Element.ALIGN_CENTER);
            amountHeader.setSpacingAfter(10f);
            pdfDocument.add(amountHeader);

            Paragraph amountValue = new Paragraph(String.format("%.2f BYN", paymentPdfDto.getAmount()), AMOUNT_FONT);
            amountValue.setAlignment(Element.ALIGN_CENTER);
            amountValue.setSpacingAfter(20f);
            pdfDocument.add(amountValue);

            // QR-код
            addQRCode(writer, paymentPdfDto);

            // Нижний колонтитул
            addFooter(writer);

            pdfDocument.close();
        } catch (Exception e) {
            throw new DocumentException("Ошибка при генерации PDF: " + e.getMessage());
        }

        return link;
    }

    // Утилитарный метод для обработки null
    private String safeString(Object obj) {
        return obj != null ? obj.toString() : "Не указано";
    }

    // Метод для создания шрифта с использованием файла .ttf
    private static Font createFont() {
        return createFont(14, Font.NORMAL);
    }

    private static Font createFont(int size, int style) {
        return createFont(size, style, BaseColor.BLACK);
    }

    private static Font createFont(int size, int style, BaseColor color) {
        try {
            BaseFont baseFont = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(baseFont, size, style, color);
        } catch (IOException | DocumentException e) {
            throw new RuntimeException("Ошибка загрузки шрифта: " + e.getMessage(), e);
        }
    }

    private void addQRCode(PdfWriter writer, PaymentPdfDto dto) {
        try {
            String qrContent = String.format("PaymentID:%d;Amount:%.2f;Date:%s",
                    dto.getPaymentId(),
                    dto.getAmount(),
                    dto.getPaymentDate().format(DateTimeFormatter.ISO_DATE));

            BarcodeQRCode qrCode = new BarcodeQRCode(qrContent, 150, 150, null);
            Image qrImage = qrCode.getImage();
            qrImage.setAbsolutePosition(writer.getPageSize().getWidth() - 180, 50); // Нижний правый угол
            writer.getDirectContent().addImage(qrImage);
        } catch (Exception e) {
            // Игнорируем ошибки QR-кода
        }
    }

    private void addFooter(PdfWriter writer) {
        PdfContentByte cb = writer.getDirectContent();
        Rectangle pageSize = writer.getPageSize();

        try {
            cb.saveState();
            cb.beginText();
            cb.setFontAndSize(BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED), 8);
            cb.showTextAligned(Element.ALIGN_CENTER,
                    "Документ сформирован автоматически системой Госуслуги",
                    pageSize.getWidth() / 2, 20, 0);
            cb.endText();
            cb.restoreState();
        } catch (Exception e) {
            // Игнорируем ошибки футера
        }
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, HEADER_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }


}
