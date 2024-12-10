package com.spring.springproject.service.impl;

import com.spring.springproject.entities.Document;
import com.spring.springproject.repositories.DocumentRepository;
import com.spring.springproject.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional
    public Document save(Document document) {
        document.setLoadingDate(LocalDateTime.now());
        return documentRepository.save(document);
    }

    @Override
    @Transactional(readOnly = true)
    public Document findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id: " + id));
    }

    @Override
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Document document = findById(id);
        
        // Удаляем файл
        try {
            Path filePath = Paths.get(uploadDir, document.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Логируем ошибку, но продолжаем удаление записи из БД
            e.printStackTrace();
        }

        documentRepository.delete(document);
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getFileContent(Long id) throws IOException {
        Document document = findById(id);
        Path filePath = Paths.get(uploadDir, document.getFilePath());
        return Files.readAllBytes(filePath);
    }

    @Override
    public long count() {
        return documentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Document> findAll(Pageable pageable, Long typeId, Long bidId) {
        Specification<Document> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (typeId != null) {
                predicates.add(cb.equal(root.get("type").get("id"), typeId));
            }
            if (bidId != null) {
                predicates.add(cb.equal(root.get("bid").get("id"), bidId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return documentRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Document> findAllByCitizen(Long citizenId, Pageable pageable, Long typeId, Long bidId) {
        Specification<Document> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Основной фильтр по гражданину
            predicates.add(cb.equal(root.get("bid").get("citizen").get("id"), citizenId));

            if (typeId != null) {
                predicates.add(cb.equal(root.get("type").get("id"), typeId));
            }
            if (bidId != null) {
                predicates.add(cb.equal(root.get("bid").get("id"), bidId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return documentRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public void update(Document document) {
        if (!documentRepository.existsById(document.getId())) {
            throw new EntityNotFoundException("Document not found with id: " + document.getId());
        }
        documentRepository.save(document);
    }

    @Override
    @Transactional
    public Document save(Document document, MultipartFile file) throws IOException {
        // Создаем структуру директорий по дате
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path uploadPath = Paths.get(uploadDir, dateDir);
        Files.createDirectories(uploadPath);

        // Генерируем уникальное имя файла
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;

        // Сохраняем файл
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);

        // Вычисляем хеш файла
        String hash = DigestUtils.sha256Hex(file.getInputStream());

        // Заполняем метаданные документа
        document.setFilePath(dateDir + "/" + newFilename);
        document.setOriginalFilename(originalFilename);
        document.setFileSize(file.getSize());
        document.setMimeType(file.getContentType());
        document.setHash(hash);
        document.setLoadingDate(LocalDateTime.now());

        return documentRepository.save(document);
    }
} 