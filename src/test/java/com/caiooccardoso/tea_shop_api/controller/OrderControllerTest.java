package com.caiooccardoso.tea_shop_api.controller;

import com.caiooccardoso.tea_shop_api.exceptions.GlobalExceptionHandler;
import com.caiooccardoso.tea_shop_api.exceptions.InsufficientStockException;
import com.caiooccardoso.tea_shop_api.exceptions.OrderNotFoundException;
import com.caiooccardoso.tea_shop_api.exceptions.ProductNotFoundException;
import com.caiooccardoso.tea_shop_api.exceptions.UserNotFoundException;
import com.caiooccardoso.tea_shop_api.dto.OrderDetailResponseDTO;
import com.caiooccardoso.tea_shop_api.dto.OrderItemResponseDTO;
import com.caiooccardoso.tea_shop_api.services.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest {

    private final OrderService orderService = mock(OrderService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new OrderController(orderService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void shouldReturnCreatedWhenPayloadIsValid() throws Exception {
        OrderDetailResponseDTO order = new OrderDetailResponseDTO();
        UUID orderId = UUID.randomUUID();
        order.setId(orderId);
        order.setTotal(BigDecimal.valueOf(40));

        OrderItemResponseDTO item = new OrderItemResponseDTO();
        item.setProductId(1L);
        item.setProductName("Chá Verde");
        item.setUnitPrice(BigDecimal.valueOf(20));
        item.setQuantity(2);
        item.setLineTotal(BigDecimal.valueOf(40));
        order.setItems(List.of(item));

        when(orderService.createOrder(any())).thenReturn(order);

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.items[0].productId").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].lineTotal").value(40));
    }

    @Test
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        String invalidBody = """
                {
                  "items": []
                }
                """;

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Dados inválidos para operação de pedido"))
                .andExpect(jsonPath("$.fields.customerId").exists())
                .andExpect(jsonPath("$.fields.address").exists())
                .andExpect(jsonPath("$.fields.items").exists());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExistOnCreateOrder() throws Exception {
        when(orderService.createOrder(any())).thenThrow(new UserNotFoundException("Usuário não encontrado"));

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderBody()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExistOnCreateOrder() throws Exception {
        when(orderService.createOrder(any())).thenThrow(new ProductNotFoundException("Produto não encontrado"));

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderBody()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Produto não encontrado"));
    }

    @Test
    void shouldReturnBadRequestWhenStockIsInsufficientOnCreateOrder() throws Exception {
        when(orderService.createOrder(any()))
                .thenThrow(new InsufficientStockException("Estoque insuficiente para o produto: Chá Verde"));

        mockMvc.perform(post("/api/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderBody()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Estoque insuficiente para o produto: Chá Verde"));
    }

    @Test
    void shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(orderService.getOrderById(id)).thenThrow(new OrderNotFoundException("Pedido não encontrado"));

        mockMvc.perform(get("/api/order/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Pedido não encontrado"));
    }

    private String validOrderBody() {
        return """
                {
                  "customerId": "6f0eec83-7f41-422f-8cd3-7d9bf26fbb8a",
                  "address": {
                    "nameOfRecipient": "Caio",
                    "street": "Rua das Flores",
                    "number": "123",
                    "unitOrApt": "Apto 12",
                    "city": "São Paulo",
                    "state": "SP",
                    "postalCode": "01000-000",
                    "country": "Brasil"
                  },
                  "items": [
                    {
                      "productId": 1,
                      "quantity": 2
                    }
                  ]
                }
                """;
    }
}
