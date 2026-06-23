package com.caiooccardoso.tea_shop_api.model;

import jakarta.persistence.*;

import java.util.UUID;

@Embeddable
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