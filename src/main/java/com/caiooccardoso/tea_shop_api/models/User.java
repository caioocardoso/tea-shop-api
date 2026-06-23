package com.caiooccardoso.tea_shop_api.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
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
    @CollectionTable(name = "user_addresses", joinColumns = @JoinColumn(name = "user_id"))
    private List<Address> addressList = new ArrayList<>();
    @OneToMany(mappedBy = "customer")
    private List<Order> orderList = new ArrayList<>();

}
