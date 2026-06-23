package com.caiooccardoso.tea_shop_api.services;

import com.caiooccardoso.tea_shop_api.dto.ProductDTO;
import com.caiooccardoso.tea_shop_api.exceptions.ProductAlreadyExistsException;
import com.caiooccardoso.tea_shop_api.exceptions.ProductNotFoundException;
import com.caiooccardoso.tea_shop_api.models.Product;
import com.caiooccardoso.tea_shop_api.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(ProductDTO productDTO) {
        if (productRepository.existsByNameIgnoreCase(productDTO.getName())) {
            throw new ProductAlreadyExistsException("Já existe um produto com este nome");
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setImagesURLs(productDTO.getImagesURLs());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());

        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        Product product = productRepository.getProductById(id);
        if (product == null) {
            throw new ProductNotFoundException("Produto não encontrado");
        }
        return product;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProductById(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.getProductById(id);
        if (existingProduct == null) {
            throw new ProductNotFoundException("Produto não encontrado");
        }

        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setImagesURLs(productDTO.getImagesURLs());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setQuantity(productDTO.getQuantity());

        return productRepository.save(existingProduct);
    }

    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Produto não encontrado");
        }

        productRepository.deleteById(id);
    }
}
