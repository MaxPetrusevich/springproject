package com.spring.springproject.service.impl;

import com.spring.springproject.dto.PaymentPdfDto;
import com.spring.springproject.email.EmailSender;
import com.spring.springproject.entities.*;
import com.spring.springproject.repositories.DocumentRepository;
import com.spring.springproject.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.spring.springproject.service.impl.PdfService.PAYMENT_DIRECTORY;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DocumentRepository documentRepository; // Repository for Document
    private final PdfService pdfService; // PdfService to generate the PDF
    private final TypeServiceImpl typeService;

    // Find all payments
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

       // Для списка платежей с фильтрами
       public Page<Payment> findAll(Pageable pageable, Long bidId, Long statusId) {
        Specification<Payment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (bidId != null) {
                predicates.add(cb.equal(root.get("bid").get("id"), bidId));
            }
            if (statusId != null) {
                predicates.add(cb.equal(root.get("status").get("id"), statusId));
            }
            
            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return paymentRepository.findAll(spec, pageable);
    }

    // Find payments by date
    public List<Payment> findByDate(LocalDate date) {
        return paymentRepository.findByDate(date);
    }

    // Find a Payment by its ID
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    // Save a new Payment
    @Transactional
    public Payment save(Payment payment) {
        try {
            Payment savedPayment = paymentRepository.save(payment);
            if (savedPayment.getBid() != null) {
                String pdfLink = generateAndSaveReceipt(savedPayment);
                // Отправляем PDF на email пользователя
                String userEmail = savedPayment.getBid().getCitizen().getEmail();
                if (userEmail != null && !userEmail.isEmpty()) {
                    EmailSender.sendPdf(userEmail, pdfLink);
                }
            }
            return savedPayment;
        } catch (Exception e) {
            throw new RuntimeException("Error saving payment: " + e.getMessage(), e);
        }
    }

    private String generateAndSaveReceipt(Payment payment) {
        PaymentPdfDto pdfDto = createPdfDto(payment);
        try {
            String pdfLink = pdfService.generatePdf(pdfDto);
            saveDocument(payment, pdfLink);
            return pdfLink;
        } catch (Exception e) {
            throw new RuntimeException("Error generating receipt: " + e.getMessage(), e);
        }
    }

    // Update an existing Payment
    @Transactional
    public Payment update(Long id, Payment payment) {
        if (paymentRepository.existsById(id)) {
            payment.setId(id);  // Set the ID to the existing one for update
            return paymentRepository.save(payment);
        }
        return null; // Return null if payment does not exist
    }

    // Delete a Payment by its ID
    @Transactional
    public void delete(Long id) {
        paymentRepository.deleteById(id);
    }

    private PaymentPdfDto createPdfDto(Payment payment) {
        Bid bid = payment.getBid();
        Citizen citizen = bid.getCitizen();
        GovService service = bid.getService();
        Establishment establishment = service.getEstablishment();

        return new PaymentPdfDto(
                payment.getId(),
                bid.getId(),
                payment.getSum(),
                payment.getDate(),
                service.getName(),
                service.getDescription(),
                String.format("%s %s %s",
                        citizen.getLastName(),
                        citizen.getFirstName(),
                        citizen.getMiddleName()),
                citizen.getIdentifyNumber(),
                citizen.getPhone(),
                citizen.getEmail(),
                establishment.getName(),
                establishment.getAddress(),
                establishment.getPhone(),
                bid.getStatus().getStatus()
        );
    }


    private void saveDocument(Payment payment, String pdfLink) throws IOException {
        // Создаем структуру директорий по дате
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadPath = Paths.get(PAYMENT_DIRECTORY, dateDir);
        Files.createDirectories(uploadPath);

        // Генерируем уникальное имя для файла, если необходимо
        String originalFilename = "PaymentDocument" + payment.getId() + ".pdf";
        String newFilename = UUID.randomUUID() + ".pdf"; // Генерация нового имени

        // Сохраняем PDF файл (если необходимо переместить файл из pdfLink)
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(Paths.get(pdfLink), filePath); // Копируем файл из исходной папки по ссылке

        // Вычисляем хеш файла
        String hash = DigestUtils.sha256Hex(Files.newInputStream(filePath));

        // Заполняем метаданные документа
        Document document = Document.builder()
                .name(originalFilename)  // Оригинальное имя документа
                .bid(payment.getBid())    // Привязываем к заявке
                .loadingDate(payment.getDate().atStartOfDay())  // Дата загрузки
                .filePath(dateDir + "/" + newFilename)
                .originalFilename(newFilename)// Путь к файлу
                .type(typeService.findById(5L))  // Тип документа (например, "Квитанция")
                .fileSize(Files.size(filePath))  // Размер файла
                .mimeType("application/pdf")  // MIME тип PDF
                .hash(hash)  // Хеш файла
                .build();

        // Сохраняем документ в базе данных
        documentRepository.save(document);
    }

}
