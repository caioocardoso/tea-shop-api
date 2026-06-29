package com.caiooccardoso.tea_shop_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OrderCustomerResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
}