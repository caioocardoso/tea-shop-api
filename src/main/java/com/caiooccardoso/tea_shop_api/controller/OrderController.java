package com.caiooccardoso.tea_shop_api.controller;

import com.caiooccardoso.tea_shop_api.dto.CreateOrderDTO;
import com.caiooccardoso.tea_shop_api.dto.OrderDetailResponseDTO;
import com.caiooccardoso.tea_shop_api.dto.OrderSummaryResponseDTO;
import com.caiooccardoso.tea_shop_api.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDetailResponseDTO createOrder(@RequestBody @Valid CreateOrderDTO createOrderDTO) {
        return orderService.createOrder(createOrderDTO);
    }

    @GetMapping("/{id}")
    public OrderDetailResponseDTO getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @GetMapping
    public List<OrderSummaryResponseDTO> getAllOrders() {
        return orderService.getAllOrders();
    }
}
