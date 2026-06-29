package com.caiooccardoso.tea_shop_api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JsonIgnoreProperties("orderList")
    private User customer;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> items;
    @Embedded
    private Address address;
    private String status;
    private LocalDateTime createdAt;
    private BigDecimal total;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = "CREATED";
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
