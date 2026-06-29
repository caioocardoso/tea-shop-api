package com.caiooccardoso.tea_shop_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderDetailResponseDTO {
    private UUID id;
    private String status;
    private LocalDateTime createdAt;
    private OrderCustomerResponseDTO customer;
    private OrderAddressResponseDTO shippingAddress;
    private List<OrderItemResponseDTO> items;
    private Integer itemCount;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal discount;
    private BigDecimal total;
}