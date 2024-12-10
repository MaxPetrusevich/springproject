package com.spring.springproject.controller;

import com.spring.springproject.dto.DocumentRequestDto;
import com.spring.springproject.entities.Document;
import com.spring.springproject.exception.FileStorageException;
import com.spring.springproject.service.BidService;
import com.spring.springproject.service.DocumentService;
import com.spring.springproject.service.impl.TypeServiceImpl;
import com.spring.springproject.mapper.EntityMapper;
import com.spring.springproject.util.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final BidService bidService;
    private final TypeServiceImpl typeService;
    private final EntityMapper mapper;
    
    @Autowired
    private List<String> allowedFileTypes;

    @GetMapping
    public ModelAndView listDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long bidId
    ) {
        ModelAndView mav = new ModelAndView("admin/doc");
        Page<Document> documents = documentService.findAll(
            PageRequest.of(page, size), 
            typeId, 
            bidId
        );
        mav.addObject("documents", documents);
        mav.addObject("bids", bidService.findAll());
        mav.addObject("types", typeService.findAll());
        return mav;
    }

    @GetMapping("/create")
    public ModelAndView createDocumentForm() {
        ModelAndView mav = new ModelAndView("admin/doc-edit");
        DocumentRequestDto document = new DocumentRequestDto();
        document.setLoadingDate(LocalDateTime.now());
        mav.addObject("document", document);
        mav.addObject("types", typeService.findAll());
        mav.addObject("bids", bidService.findAll());
        return mav;
    }

    @PostMapping
    public String createDocument(
            @Valid @ModelAttribute DocumentRequestDto documentDto,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            // Проверяем тип файла
            if (!FileUtils.isValidFileType(file, allowedFileTypes)) {
                throw new FileStorageException("Недопустимый тип файла. Разрешены только: " + String.join(", ", allowedFileTypes));
            }

            Document document = mapper.toDocument(documentDto);
            
            // Устанавливаем связи
            if (documentDto.getTypeId() != null) {
                document.setType(typeService.findById(documentDto.getTypeId()));
            }
            if (documentDto.getBidId() != null) {
                document.setBid(bidService.findById(documentDto.getBidId()));
            }

            documentService.save(document, file);
            return "redirect:/admin/documents";
        } catch (IOException e) {
            throw new FileStorageException("Не удалось сохранить файл", e);
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        try {
            Document document = documentService.findById(id);
            byte[] content = documentService.getFileContent(id);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getMimeType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getOriginalFilename() + "\"")
                    .body(content);
        } catch (IOException e) {
            throw new FileStorageException("Не удалось загрузить файл", e);
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteDocument(@PathVariable Long id) {
        documentService.delete(id);
        return "redirect:/admin/documents";
    }

    @ExceptionHandler(FileStorageException.class)
    public ModelAndView handleFileStorageException(FileStorageException e) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorMessage", e.getMessage());
        return mav;
    }
}
