package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.ProductRequest;
import com.licorerajm.backend.dto.ProductResponse;
import com.licorerajm.backend.entity.Category;
import com.licorerajm.backend.entity.Product;
import com.licorerajm.backend.exception.DuplicateResourceException;
import com.licorerajm.backend.exception.ResourceNotFoundException;
import com.licorerajm.backend.repository.CategoryRepository;
import com.licorerajm.backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductResponse> findAll() {

        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse findById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "El producto no fue encontrado"));

        return toResponse(product);
    }

    public ProductResponse create(ProductRequest request) {

        if (productRepository.existsByBarcode(request.getBarcode())) {
            throw new DuplicateResourceException(
                    "Ya existe un producto con ese código de barras");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "La categoría no fue encontrada"));

        Product product = new Product();

        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setCategory(category);
        product.setProvider(request.getProvider());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSalePrice(request.getSalePrice());

        // El stock inicial se manejará mediante entradas de inventario.
        product.setCurrentStock(0);

        product.setMinimumStock(request.getMinimumStock());
        product.setActive(true);

        Product savedProduct = productRepository.save(product);

        return toResponse(savedProduct);
    }

    public ProductResponse update(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "El producto no fue encontrado"));

        if (!product.getBarcode().equals(request.getBarcode())
                && productRepository.existsByBarcode(request.getBarcode())) {

            throw new DuplicateResourceException(
                    "Ya existe un producto con ese código de barras");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "La categoría no fue encontrada"));

        product.setBarcode(request.getBarcode());
        product.setName(request.getName());
        product.setCategory(category);
        product.setProvider(request.getProvider());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSalePrice(request.getSalePrice());
        product.setMinimumStock(request.getMinimumStock());

        Product updatedProduct = productRepository.save(product);

        return toResponse(updatedProduct);
    }

    public ProductResponse deactivate(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "El producto no fue encontrado"));

        product.setActive(false);

        Product updatedProduct = productRepository.save(product);

        return toResponse(updatedProduct);
    }

    public ProductResponse activate(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "El producto no fue encontrado"));

        product.setActive(true);

        Product updatedProduct = productRepository.save(product);

        return toResponse(updatedProduct);
    }

    private ProductResponse toResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getBarcode(),
                product.getName(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getProvider(),
                product.getPurchasePrice(),
                product.getSalePrice(),
                product.getCurrentStock(),
                product.getMinimumStock(),
                product.getActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}