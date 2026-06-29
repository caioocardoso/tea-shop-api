package com.caiooccardoso.tea_shop_api.repositories;

import com.caiooccardoso.tea_shop_api.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
}
