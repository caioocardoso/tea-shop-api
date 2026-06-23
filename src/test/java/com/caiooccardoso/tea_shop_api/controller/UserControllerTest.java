package com.caiooccardoso.tea_shop_api.controller;

import com.caiooccardoso.tea_shop_api.dto.UserDTO;
import com.caiooccardoso.tea_shop_api.exceptions.GlobalExceptionHandler;
import com.caiooccardoso.tea_shop_api.exceptions.UserAlreadyExistsException;
import com.caiooccardoso.tea_shop_api.exceptions.UserNotFoundException;
import com.caiooccardoso.tea_shop_api.models.Address;
import com.caiooccardoso.tea_shop_api.models.User;
import com.caiooccardoso.tea_shop_api.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private final UserService userService = mock(UserService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new UserController(userService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void shouldReturnCreatedWhenPayloadIsValid() throws Exception {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");
        User user = new User();
        user.setId(id);
        user.setFirstName("Caio");

        when(userService.addUser(any(UserDTO.class))).thenReturn(user);

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserBody()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("Caio"));
    }

    @Test
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        String invalidBody = """
                {
                  "firstName": "",
                  "lastName": "",
                  "cpf": "",
                  "mail": "email-invalido",
                  "birthDate": null,
                  "addressList": []
                }
                """;

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Dados inválidos para operação de usuário"))
                .andExpect(jsonPath("$.fields.firstName").exists())
                .andExpect(jsonPath("$.fields.lastName").exists())
                .andExpect(jsonPath("$.fields.cpf").exists())
                .andExpect(jsonPath("$.fields.mail").exists())
                .andExpect(jsonPath("$.fields.birthDate").exists())
                .andExpect(jsonPath("$.fields.addressList").exists());
    }

    @Test
    void shouldReturnConflictWhenUserAlreadyExists() throws Exception {
        when(userService.addUser(any(UserDTO.class)))
                .thenThrow(new UserAlreadyExistsException("Já existe um usuário com este CPF"));

        mockMvc.perform(post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Já existe um usuário com este CPF"));
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        UUID id = UUID.fromString("22222222-2222-2222-2222-222222222222");
        when(userService.getUserById(id))
                .thenThrow(new UserNotFoundException("Usuário não encontrado"));

        mockMvc.perform(get("/api/user/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    void shouldReturnOkWhenUpdatingExistingUser() throws Exception {
        UUID id = UUID.fromString("33333333-3333-3333-3333-333333333333");
        User user = new User();
        user.setId(id);
        user.setFirstName("João");

        when(userService.updateUserById(any(UUID.class), any(UserDTO.class))).thenReturn(user);

        mockMvc.perform(put("/api/user/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.firstName").value("João"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingUserThatDoesNotExist() throws Exception {
        UUID id = UUID.fromString("44444444-4444-4444-4444-444444444444");
        when(userService.updateUserById(any(UUID.class), any(UserDTO.class)))
                .thenThrow(new UserNotFoundException("Usuário não encontrado"));

        mockMvc.perform(put("/api/user/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUserBody()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    @Test
    void shouldReturnNoContentWhenDeletingExistingUser() throws Exception {
        UUID id = UUID.fromString("55555555-5555-5555-5555-555555555555");
        doNothing().when(userService).deleteUserById(id);

        mockMvc.perform(delete("/api/user/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingUserThatDoesNotExist() throws Exception {
        UUID id = UUID.fromString("66666666-6666-6666-6666-666666666666");
        doThrow(new UserNotFoundException("Usuário não encontrado"))
                .when(userService).deleteUserById(id);

        mockMvc.perform(delete("/api/user/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Usuário não encontrado"));
    }

    private String validUserBody() {
        return """
                {
                  "firstName": "Caio",
                  "lastName": "Cardoso",
                  "cpf": "12345678900",
                  "mail": "caio@email.com",
                  "birthDate": "1998-05-10",
                  "addressList": [
                    {
                      "nameOfRecipient": "Caio Cardoso",
                      "street": "Rua A",
                      "number": "10",
                      "unitOrApt": "Apto 12",
                      "city": "São Paulo",
                      "state": "SP",
                      "postalCode": "01001000",
                      "country": "Brasil"
                    }
                  ]
                }
                """;
    }
}