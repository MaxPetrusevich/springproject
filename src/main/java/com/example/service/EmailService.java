package com.example.service;

import com.example.entity.Payment;
import com.example.entity.Subscription;
import com.example.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final String FROM_EMAIL = "noreply@example.com";

    public void sendPaymentConfirmation(String to, Payment payment) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject("Подтверждение платежа");
        message.setText(String.format(
            "Платеж #%d на сумму %s создан и ожидает подтверждения.",
            payment.getId(),
            payment.getAmount()
        ));
        mailSender.send(message);
    }

    public void sendPaymentSuccess(String to, Payment payment) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject("Платеж успешно выполнен");
        message.setText(String.format(
            "Платеж #%d на сумму %s успешно выполнен.",
            payment.getId(),
            payment.getAmount()
        ));
        mailSender.send(message);
    }

    public void sendPaymentFailure(String to, Payment payment, String reason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject("Ошибка платежа");
        message.setText(String.format(
            "Платеж #%d на сумму %s не выполнен. Причина: %s",
            payment.getId(),
            payment.getAmount(),
            reason
        ));
        mailSender.send(message);
    }

    public void sendSubscriptionConfirmation(String to, Subscription subscription) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject("Подписка оформлена");
        message.setText(String.format(
            "Подписка на план '%s' успешно оформлена. Срок действия: с %s по %s",
            subscription.getPlan().getName(),
            subscription.getStartDate(),
            subscription.getEndDate()
        ));
        mailSender.send(message);
    }

    public void sendSubscriptionCancellation(String to, Subscription subscription) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject("Подписка отменена");
        message.setText(String.format(
            "Подписка #%d на план '%s' отменена.",
            subscription.getId(),
            subscription.getPlan().getName()
        ));
        mailSender.send(message);
    }

    public void sendSubscriptionRenewal(String to, Subscription subscription) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject("Подписка продлена");
        message.setText(String.format(
            "Подписка на план '%s' продлена. Новый срок действия: с %s по %s",
            subscription.getPlan().getName(),
            subscription.getStartDate(),
            subscription.getEndDate()
        ));
        mailSender.send(message);
    }

    public void sendExpirationNotification(String to) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(to);
        message.setSubject("Срок действия подписки истек");
        message.setText("Срок действия вашей подписки истек. Для продолжения использования сервиса, пожалуйста, продлите подписку.");
        mailSender.send(message);
    }

    public void sendSubscriptionExpiredNotification(String email, Subscription subscription) {
        String subject = "Подписка истекла";
        String text = String.format(
            "Уважаемый пользователь!\n\n" +
            "Ваша подписка на план \"%s\" истекла.\n" +
            "Для продолжения использования сервиса, пожалуйста, продлите подписку.\n\n" +
            "С уважением,\nКоманда поддержки",
            subscription.getPlan().getName()
        );
        
        sendEmail(email, subject, text);
    }

    public void sendSubscriptionExpirationWarning(String email, Subscription subscription) {
        String subject = "Подписка скоро истечет";
        String text = String.format(
            "Уважаемый пользователь!\n\n" +
            "Ваша подписка на план \"%s\" истекает через 3 дня.\n" +
            "Для бесперебойного использования сервиса рекомендуем продлить подписку заранее.\n\n" +
            "С уважением,\nКоманда поддержки",
            subscription.getPlan().getName()
        );
        
        sendEmail(email, subject, text);
    }

    private void sendEmail(String email, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_EMAIL);
        message.setTo(email);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
} 