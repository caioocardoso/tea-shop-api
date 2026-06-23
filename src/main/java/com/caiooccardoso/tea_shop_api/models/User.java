package com.caiooccardoso.tea_shop_api.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String firstName;
    private String lastName;
    private String cpf;
    private String mail;
    private LocalDate birthDate;
    @ElementCollection
    @CollectionTable(name = "user_acdresses", joinColumns = @JoinColumn(name = "user_id"))
    private List<Address> addressList = new ArrayList<>();

}
