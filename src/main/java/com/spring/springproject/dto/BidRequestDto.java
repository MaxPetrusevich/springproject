package com.spring.springproject.dto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class BidRequestDto {

    private Long citizenId;
    private Long serviceId;
    private Long statusId;
    private LocalDate date;

}
