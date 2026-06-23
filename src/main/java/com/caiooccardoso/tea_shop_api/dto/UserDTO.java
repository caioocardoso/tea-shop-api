package com.caiooccardoso.tea_shop_api.dto;

import com.caiooccardoso.tea_shop_api.models.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserDTO {
    @NotBlank(message = "O primeiro nome é obrigatório")
    private String firstName;

    @NotBlank(message = "O sobrenome é obrigatório")
    private String lastName;

    @NotBlank(message = "O CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail informado é inválido")
    private String mail;

    @NotNull(message = "A data de nascimento é obrigatória")
    private LocalDate birthDate;

    @Valid
    private List<Address> addressList;
}