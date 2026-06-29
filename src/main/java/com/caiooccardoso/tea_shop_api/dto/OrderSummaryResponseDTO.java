package com.caiooccardoso.tea_shop_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class OrderSummaryResponseDTO {
    private UUID id;
    private String status;
    private LocalDateTime createdAt;
    private Integer itemCount;
    private BigDecimal total;
}