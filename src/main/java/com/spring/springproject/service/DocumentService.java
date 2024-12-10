package com.spring.springproject.service;

import com.spring.springproject.entities.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    Document save(Document document, MultipartFile file) throws IOException;
    Document save(Document document);
    Document findById(Long id);
    List<Document> findAll();
    void delete(Long id);
    void update(Document document);
    byte[] getFileContent(Long id) throws IOException;
    long count();
    // Добавляем методы для поиска документов
    Page<Document> findAll(Pageable pageable, Long typeId, Long bidId);
    Page<Document> findAllByCitizen(Long citizenId, Pageable pageable, Long typeId, Long bidId);
} 