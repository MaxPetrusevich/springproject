package com.example.scheduler;

import com.example.entity.Subscription;
import com.example.enums.SubscriptionStatus;
import com.example.repository.SubscriptionRepository;
import com.example.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

    private final SubscriptionRepository subscriptionRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void checkExpiredSubscriptions() {
        log.info("Starting subscription expiration check");
        LocalDateTime now = LocalDateTime.now();

        List<Subscription> expiredSubscriptions = subscriptionRepository.findByStatusAndEndDateBefore(
            SubscriptionStatus.ACTIVE, now);

        for (Subscription subscription : expiredSubscriptions) {
            subscription.setStatus(SubscriptionStatus.EXPIRED);
            subscription.setActive(false);
            subscriptionRepository.save(subscription);

            emailService.sendSubscriptionExpiredNotification(
                subscription.getUser().getEmail(),
                subscription
            );
            
            log.info("Subscription {} expired for user {}", 
                subscription.getId(), 
                subscription.getUser().getEmail());
        }

        LocalDateTime threeDaysFromNow = now.plusDays(3);
        List<Subscription> nearlyExpiredSubscriptions = subscriptionRepository
            .findByStatusAndEndDateBetween(SubscriptionStatus.ACTIVE, now, threeDaysFromNow);

        for (Subscription subscription : nearlyExpiredSubscriptions) {
            // Отправляем уведомление о скором истечении срока
            emailService.sendSubscriptionExpirationWarning(
                subscription.getUser().getEmail(),
                subscription
            );
            
            log.info("Sent expiration warning for subscription {} to user {}", 
                subscription.getId(), 
                subscription.getUser().getEmail());
        }
        
        log.info("Finished subscription expiration check. Processed {} expired and {} nearly expired subscriptions",
            expiredSubscriptions.size(), nearlyExpiredSubscriptions.size());
    }
} 