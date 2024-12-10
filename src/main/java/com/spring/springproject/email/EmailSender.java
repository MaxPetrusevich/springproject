package com.spring.springproject.email;


import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Properties;
import java.util.Random;
@Component
public class EmailSender {

    private static final String from = "makspetrusevich04@gmail.com";
    private static final String host = "smtp.gmail.com";
    public void sendMessageWithAttachment(String to, String subject, String text, String fileName, byte[] content) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "thuw mgib gzkg bzsz");
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);

            // Создаем multipart сообщение
            Multipart multipart = new MimeMultipart();

            // Часть с текстом
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(text);
            multipart.addBodyPart(textPart);

            // Часть с PDF
            MimeBodyPart attachmentPart = new MimeBodyPart();
            ByteArrayDataSource source = new ByteArrayDataSource(content, "application/pdf");
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(fileName);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);
            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка при отправке email", e);
        }
    }
    public Integer sendMail(String to) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "thuw mgib gzkg bzsz");
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
    public static void sendPdf(String email, String fileLink) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "thuw mgib gzkg bzsz");
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email));
            message.setSubject("OrderInfo");

            // Чтение файла по ссылке
            File file = new File(fileLink); // Путь к файлу
            if (!file.exists()) {
                throw new RuntimeException("File not found at the given path: " + fileLink);
            }

            byte[] fileContent = Files.readAllBytes(file.toPath());

            // Создание MimeBodyPart с PDF-файлом
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            Multipart multipart = new MimeMultipart();
            messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(fileContent, "application/pdf")));
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart.setFileName("orderInfo.pdf");

            // Установка содержимого сообщения
            message.setContent(multipart);

            // Отправка сообщения
            Transport.send(message);
        } catch (IOException | MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error while sending email with PDF", e);
        }
    }

    public Integer generateCode() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;

        return random.nextInt((max - min) + 1) + min;
    }
}