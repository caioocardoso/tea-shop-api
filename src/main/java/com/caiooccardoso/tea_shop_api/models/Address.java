package com.caiooccardoso.tea_shop_api.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {
    private String nameOfRecipient;
    private String street;
    private String number;
    private String unitOrApt;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}