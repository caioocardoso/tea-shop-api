package com.caiooccardoso.tea_shop_api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderAddressResponseDTO {
    private String nameOfRecipient;
    private String street;
    private String number;
    private String unitOrApt;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}