package com.caiooccardoso.tea_shop_api.dto;

import com.caiooccardoso.tea_shop_api.models.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateOrderDTO {
    @NotNull(message = "O id do usuário é obrigatório")
    private UUID customerId;

    @Valid
    @NotNull(message = "O endereço é obrigatório")
    private Address address;

    @Valid
    @NotNull(message = "A lista de itens é obrigatória")
    @NotEmpty(message = "A lista de itens não pode ser vazia")
    private List<OrderItemDTO> items;
}
