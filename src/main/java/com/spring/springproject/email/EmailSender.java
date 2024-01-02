package com.spring.springproject.email;


import com.spring.springproject.entities.Order;
import com.spring.springproject.service.impl.PdfService;
import org.dom4j.DocumentException;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.util.Properties;
import java.util.Random;
@Component
public class EmailSender {

    private static final String from = "mail_excel_test@mail.ru";
    private static final String host = "smtp.mail.ru";

    public Integer sendMail(String to) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "yzc6dWWH0XGwLmAyPmGP");
            }
        });

        try {
            var generatedCode = generateCode();
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("EmailConfirmation");
            message.setSubject("Confirming email");
            message.setText("Your code for confirming email: " + generatedCode);

            Transport.send(message);
            return generatedCode;
        } catch (SendFailedException e) {
            e.printStackTrace();
            return 1;
        } catch (MessagingException e) {
            e.printStackTrace();
            return 0;
        }
    }
    public static void sendPdf(String email, Order order){
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "yzc6dWWH0XGwLmAyPmGP");
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("OrderInfo");
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart(); // Укажите путь к вашему PDF-файлу
            messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(PdfService.generatePdf(order).toByteArray(), "application/pdf")));
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart.setFileName("orderInfo.pdf");
            // Установка содержимого сообщения
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            // Отправка сообщения
            Transport.send(message);
        }catch (AddressException e) {
            throw new RuntimeException(e);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public Integer generateCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;

        return random.nextInt((max - min) + 1) + min;
    }
}