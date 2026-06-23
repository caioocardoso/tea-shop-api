package com.caiooccardoso.tea_shop_api.controller;

import com.caiooccardoso.tea_shop_api.dto.ProductDTO;
import com.caiooccardoso.tea_shop_api.exceptions.GlobalExceptionHandler;
import com.caiooccardoso.tea_shop_api.exceptions.ProductAlreadyExistsException;
import com.caiooccardoso.tea_shop_api.exceptions.ProductNotFoundException;
import com.caiooccardoso.tea_shop_api.models.Product;
import com.caiooccardoso.tea_shop_api.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

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

class ProductControllerTest {

    private final ProductService productService = mock(ProductService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new ProductController(productService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @Test
    void shouldReturnCreatedWhenPayloadIsValid() throws Exception {
        Product product = new Product();
        product.setId(1L);
        product.setName("Chá Verde");

        when(productService.addProduct(any(ProductDTO.class))).thenReturn(product);

        ProductDTO dto = new ProductDTO();
        dto.setName("Chá Verde");
        dto.setDescription("Descrição");
        dto.setImagesURLs(List.of("https://img.com/1"));
        dto.setPrice(BigDecimal.valueOf(25.50));
        dto.setQuantity(5);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "description": "",
                  "imagesURLs": [],
                  "price": 0,
                  "quantity": -1
                }
                """;

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Dados inválidos para criação do produto"))
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.fields.description").exists())
                .andExpect(jsonPath("$.fields.imagesURLs").exists())
                .andExpect(jsonPath("$.fields.price").exists())
                .andExpect(jsonPath("$.fields.quantity").exists());
    }

    @Test
    void shouldReturnConflictWhenProductAlreadyExists() throws Exception {
        when(productService.addProduct(any(ProductDTO.class)))
                .thenThrow(new ProductAlreadyExistsException("Já existe um produto com este nome"));

        String validBody = """
                {
                  "name": "Chá Preto",
                  "description": "Descrição",
                  "imagesURLs": ["https://img.com/1"],
                  "price": 9.99,
                  "quantity": 4
                }
                """;

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Já existe um produto com este nome"));
    }

    @Test
    void shouldReturnBadRequestWhenPayloadIsMalformed() throws Exception {
        String malformedBody = "{ \"name\": \"Teste\", \"price\": }";

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Corpo da requisição inválido"));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ProductNotFoundException("Produto não encontrado"));

        mockMvc.perform(get("/api/product/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Produto não encontrado"))
                .andExpect(jsonPath("$.path").value("/api/product/99"));
    }

    @Test
    void shouldReturnOkWhenUpdatingExistingProduct() throws Exception {
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Chá Branco");

        when(productService.updateProductById(any(Long.class), any(ProductDTO.class)))
                .thenReturn(updatedProduct);

        String body = """
                {
                  "name": "Chá Branco",
                  "description": "Descrição atualizada",
                  "imagesURLs": ["https://img.com/1"],
                  "price": 19.90,
                  "quantity": 3
                }
                """;

        mockMvc.perform(put("/api/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Chá Branco"));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingProductThatDoesNotExist() throws Exception {
        when(productService.updateProductById(any(Long.class), any(ProductDTO.class)))
                .thenThrow(new ProductNotFoundException("Produto não encontrado"));

        String body = """
                {
                  "name": "Chá Branco",
                  "description": "Descrição atualizada",
                  "imagesURLs": ["https://img.com/1"],
                  "price": 19.90,
                  "quantity": 3
                }
                """;

        mockMvc.perform(put("/api/product/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Produto não encontrado"))
                .andExpect(jsonPath("$.path").value("/api/product/99"));
    }

    @Test
    void shouldReturnBadRequestWhenUpdatingWithInvalidPayload() throws Exception {
        String invalidBody = """
                {
                  "name": "",
                  "description": "",
                  "imagesURLs": [],
                  "price": 0,
                  "quantity": -1
                }
                """;

        mockMvc.perform(put("/api/product/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Dados inválidos para criação do produto"))
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.fields.description").exists())
                .andExpect(jsonPath("$.fields.imagesURLs").exists())
                .andExpect(jsonPath("$.fields.price").exists())
                .andExpect(jsonPath("$.fields.quantity").exists());
    }

    @Test
    void shouldReturnNoContentWhenDeletingExistingProduct() throws Exception {
        doNothing().when(productService).deleteProductById(1L);

        mockMvc.perform(delete("/api/product/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingProductThatDoesNotExist() throws Exception {
        doThrow(new ProductNotFoundException("Produto não encontrado"))
                .when(productService).deleteProductById(99L);

        mockMvc.perform(delete("/api/product/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Produto não encontrado"))
                .andExpect(jsonPath("$.path").value("/api/product/99"));
    }
}