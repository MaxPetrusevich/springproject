package com.spring.springproject.mapper;

import com.spring.springproject.dto.*;
import com.spring.springproject.entities.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EntityMapper {
    private final ModelMapper modelMapper;

    public Bid toBid(BidRequestDto dto) {
        return modelMapper.map(dto, Bid.class);
    }
    
    public BidStatus toBidStatus(BidStatusRequestDto dto) {
        return modelMapper.map(dto, BidStatus.class);
    }
    
    public Category toCategory(CategoryRequestDto dto) {
        return modelMapper.map(dto, Category.class);
    }
    
    public Citizen toCitizen(CitizenRequestDto dto) {
        return modelMapper.map(dto, Citizen.class);
    }
    
    public GovService toGovService(GovServiceRequestDto dto) {
        return modelMapper.map(dto, GovService.class);
    }
    
    public PaymentStatus toPaymentStatus(PaymentStatusRequestDto dto) {
        return modelMapper.map(dto, PaymentStatus.class);
    }
    
    public User toUser(UserRequestDto dto) {
        return modelMapper.map(dto, User.class);
    }
    
    // Методы для преобразования сущностей в DTO
    public BidRequestDto toBidDto(Bid entity) {
        return modelMapper.map(entity, BidRequestDto.class);
    }
    
    public BidStatusRequestDto toBidStatusDto(BidStatus entity) {
        return modelMapper.map(entity, BidStatusRequestDto.class);
    }
    
    public CategoryRequestDto toCategoryDto(Category entity) {
        return modelMapper.map(entity, CategoryRequestDto.class);
    }
    
    public CitizenRequestDto toCitizenDto(Citizen entity) {
        return modelMapper.map(entity, CitizenRequestDto.class);
    }
    
    public GovServiceRequestDto toGovServiceDto(GovService entity) {
        return modelMapper.map(entity, GovServiceRequestDto.class);
    }
    
    public PaymentStatusRequestDto toPaymentStatusDto(PaymentStatus entity) {
        return modelMapper.map(entity, PaymentStatusRequestDto.class);
    }
    
    public UserRequestDto toUserDto(User entity) {
        UserRequestDto dto = modelMapper.map(entity, UserRequestDto.class);
        dto.setRoleId(entity.getRole().getId());
        return dto;
    }

    public DocumentRequestDto toDocumentDto(Document document) {
        DocumentRequestDto dto = new DocumentRequestDto();
        dto.setId(document.getId());
        dto.setName(document.getName());
        dto.setTypeId(document.getType() != null ? document.getType().getId() : null);
        dto.setBidId(document.getBid() != null ? document.getBid().getId() : null);
        dto.setLoadingDate(document.getLoadingDate());
        dto.setMimeType(document.getMimeType());
        dto.setFileSize(document.getFileSize());
        dto.setOriginalFilename(document.getOriginalFilename());
        dto.setHash(document.getHash());
        return dto;
    }

    public Document toDocument(DocumentRequestDto dto) {
        return Document.builder()
                .id(dto.getId())
                .name(dto.getName())
                .loadingDate(dto.getLoadingDate() != null ? dto.getLoadingDate() : LocalDateTime.now())
                .mimeType(dto.getMimeType())
                .fileSize(dto.getFileSize())
                .originalFilename(dto.getOriginalFilename())
                .hash(dto.getHash())
                .build();
    }

    public Payment toPayment(PaymentRequestDto dto) {
        return modelMapper.map(dto, Payment.class);
    }
    
    public PaymentRequestDto toPaymentDto(Payment entity) {
        return modelMapper.map(entity, PaymentRequestDto.class);
    }
}